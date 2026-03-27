/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;

/**
 *
 * @author matthewhvizdos
 */
public class KDCThread implements Runnable{
    private final BlockingQueue<String> notifications;
    private final Socket csock;
    private final Map<String, ClientData> map;
    private volatile boolean running = true;
    
    private PrivateKey prk = null;
    
    private ClientData cd = null;
    
    private PrintWriter output = null;
    private BufferedReader input = null;
    private Set<String> nonces = new HashSet<>();
    
    private SecretKey ORIGINAL_KEY = null;
    private SecretKey MasterSecret = null;
    
    private final Map<String, String> users;
    
    
    public KDCThread(Socket csock, Map<String, ClientData> key_map, BlockingQueue<String> notifications, Map<String, String> users) {
        this.csock = csock;
        this.map = key_map;
        this.notifications = notifications;
        this.users = users;
        
        try {
            output = new PrintWriter(csock.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(csock.getInputStream()));
        } catch (IOException e) {}
        
        try { // Generate the private key
            PKCS8EncodedKeySpec prspec = new PKCS8EncodedKeySpec(Encryption.conv(KDC.PRK));
            prk = KeyFactory.getInstance("RSA").generatePrivate(prspec);
        
        } catch (Exception e) {}
    }
    
    
    private String read() { try { return input.readLine(); } catch (Exception e) { return null;} }
    private void write(String s) { try { output.println(s); } catch (Exception e) {} }
    
    
    private void syncKeys() {
        String in = read();
        String parts[] = in.split(KDC.DELIM_REGEX);
        String id = parts[0];
        Nonce n_a = Nonce.toNonce(parts[1]);
        if (nonces.contains(n_a.toString())) {
            System.out.println(ansi.BOLD + ansi.BRED + "Nonces aren't working out" + ansi.RESET);
            System.exit(-1);
        } else { nonces.add(n_a.toString()); }
        
        ORIGINAL_KEY = PreprogrammedKeys.getKey(id);
        if (ORIGINAL_KEY == null) {
            write("Invalid id");
            System.out.println(ansi.BOLD + ansi.BRED + "Invalid Key Used -- Terminating thread" + ansi.RESET);
            System.exit(-1);
        }
        
        Nonce n_b = new Nonce();
        write(id + KDC.DELIM + n_a.toString() + KDC.DELIM + n_b.toString());
        // By this point server & client agree on key
    }
    
    private boolean loginUser() {
        String in = read();
        String dec = Encryption.decrypt(in, ORIGINAL_KEY);
        String parts[] = dec.split(KDC.DELIM_REGEX);
        String username = parts[0];
        String password_hash = parts[1];
        Nonce N_A = Nonce.toNonce(parts[2]);
        
        String stored_password = users.get(username);
        
    
        return false;
    }
    
    
    
    
    private void setup() {
        cd = new ClientData();
        
        loginUser();
        
        // Read input
        String in = read();
        
        cd.setPKey(in); // Make the public key
        cd.makeNonce(); // Make nonce for the client
        
        write("connected");
    }
    
    private void getId() {
        String in = read();
        
        cd.setId(in);
        map.put(in, cd); // Add client data to the map
        
        String out = cd.getNonce().toString() + KDC.DELIM + KDC.ID;
        
        String enc = Encryption.encrypt(out, cd.getPKey()); // Encrypt with clients public key
        write(enc);
    }
    
    private void nonceRespond() {
        String in = read();
        
        String dec = Encryption.decrypt(in, prk);
        
        
        String [] inputs = dec.split(KDC.DELIM_REGEX);
        Nonce n_c = Nonce.toNonce(inputs[1]);
        
        if (!n_c.equals(cd.getNonce())) {
            System.out.println("Incorrect nonce recieved");
            System.exit(-1); // Terminate process
        }
        
        String out = cd.getNonce().toString();
        String enc = Encryption.encrypt(out, cd.getPKey());
        write(enc);
        
        
        cd.generateSKey();
        
        out = KeyStringGenerator.SKeyToString(cd.getSKey());
        enc = Encryption.double_encrypt(out, prk, cd.getPKey());
        write(enc);
        
    }
    
    private void notifyJoin() { // Broadcast to other threads that a new client has joined
        int otherThreads = map.size() - 1;
        if (otherThreads <= 0) { return; }
        String msg = KDC.MSG_JOIN + KDC.U_DELIM + cd.getId() + KDC.DELIM + cd.getPKeyString();
        int i;
        for (i = 0; i < otherThreads; i ++) { notifications.offer(msg); }
        System.out.println("Notified " + i + " other threads : \033[36m" + msg + "\033[0m");
        try { TimeUnit.SECONDS.sleep(2); } catch (Exception e) {}
        for (Map.Entry<String, ClientData> c : map.entrySet()) {
            msg = KDC.RESERVED_CHAR + KDC.DELIM_REGEX + c.getKey() + KDC.DELIM + c.getValue().getPKeyString();
            write(msg);
        }
    }
    
    private void listen(String s) { // We are the creator in this situation
        String [] msgs = s.split(KDC.DELIM_REGEX); // SELF | OTHER
        String msg = Encryption.decrypt(msgs[0], cd.getSKey());
        String sig = msgs[1];
        String nonce = msgs[2]; // Read the nonce to see if it has already been received
        if (nonces.contains(nonce)) {
            System.out.println("Received a repeat nonce -- Discarding message");
            return;
        } else { nonces.add(nonce); }
        
        String notif = KDC.MSG_UPDATE + KDC.U_DELIM + msg + KDC.DELIM + sig;
        System.out.println("Notification : \033[35m" + notif + "\033[0m");
        int otherThreads = map.size();
        if (otherThreads <= 0) {
            System.out.println("No other clients to send to");
            return;
        }
        
        for (int i = 0; i < otherThreads; i ++) { notifications.offer(notif); } // Send once per active thread (including self)
    }
    
    private void manageNotifications(String notif) {
        String text[] = notif.split(KDC.U_DELIM_REGEX); // Just gets rid of the initial escape sequence
        System.out.println("New Request : \033[33m" + text[0] + "\033[0m");
        if (text[0].equals(KDC.MSG_JOIN)) {
            write(KDC.RESERVED_CHAR + KDC.DELIM_REGEX + text[1]);
        } else if (text[0].equals(KDC.MSG_UPDATE)) {
            String temp[] = text[1].split(KDC.DELIM_REGEX);
            if (temp[0].equals(cd.getId())) { return; }
            String M = temp[0] + KDC.DELIM + temp[1];
            String sig = temp[2];
            String enc = Encryption.encrypt(M, cd.getSKey()) + KDC.DELIM + sig;
            write(enc);
        } 
        try { TimeUnit.SECONDS.sleep(1); } catch (Exception e) {}
    }
    
    private void socket_input() {
        while (running) {
            String in = read();
            if  (in == null) {
                shutdown();
                break;
            }
            listen(in);
        }
    }
    
    private void handle_notif() {
        while (running) {
            try {
                String notification = notifications.poll(100, TimeUnit.MILLISECONDS);
                if (notification != null) {
                    manageNotifications(notification);
                }
            } catch (Exception e) {}
        }
    }
   
    
    protected void shutdown() {
        if (!running) { return; }
        running = false;
        try {
            System.out.println("Shutting down thread");
            output.close();
            input.close();
            csock.close();
        } catch (Exception e) {}
    }
    
    
    @Override
    public void run() {
        setup();
        getId();
        nonceRespond();
        notifyJoin();
        new Thread(this::socket_input).start();
        new Thread(this::handle_notif).start();
    }
    
}
