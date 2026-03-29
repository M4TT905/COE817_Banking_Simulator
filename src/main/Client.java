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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author matthewhvizdos
 */
public abstract class Client {
    private static final String K_PBK = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnB2jzPNxSuZrqzYXqLFpa7HYRey8PBRrM8I+IO4oQfDcnEH9U0q0vmABY7OfLFoA5+GgT1mRjq4f0sYjex8AmhvGYg5VjvRr4jhh1tym6fKcx8afZFN0Nmg0PGIPT9MdBw80LKhnSKOkzpEajVcHkgWj51zmEbDWQeBjPgbw6PwIDAQAB";
    
    
    private static final String DELIM_REGEX = "\\|";
    private static final char DELIM = '|';
    private static final char RESERVED_CHAR = '\uFFFF';
    
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8170;
    private final int C_PORT;
    
    protected static final String A_ID = "Alice";
    protected static final String B_ID = "Bob";
    protected static final String C_ID = "Charlie";
    
    private Map<String, PublicKey> key_map = null;
    private Set<String> nonces = new HashSet<>();
    
    private String id;
    private String id_KDC;
    
    private Nonce N_self = null;
    private Nonce N_KDC = null;
    
    private PrivateKey PRK_SELF = null;
    private PublicKey PBK_KDC = null;
    private SecretKey SYM_KEY = null;
    
    private SecretKey ORIGINAL_KEY = null; // original shared key between client and server
    private SecretKey ENC_KEY;
    private SecretKey MAC_KEY;
    
    private SecretKey MasterSecret;
    
    private Socket sock = null;
    private PrintWriter output = null;
    private BufferedReader input = null;
    
    private static final Scanner SCAN = new Scanner(System.in);
    private volatile boolean running = true;
    
    
    
    protected Client(String pbk, String prk, String id, int port) {
        this.key_map = new HashMap<>();
        this.id = id;
        this.C_PORT = port;
        
        this.N_self = new Nonce();
        System.out.print("Generated new nonce: \033[33m");
        N_self.print();
        System.out.print("\033[0m");
        try { // Connect to server
            sock = new Socket(HOST, PORT);
            output = new PrintWriter(sock.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (IOException ex) { ex.getLocalizedMessage(); }
        
        try { // Create pub/private keys
            PRK_SELF = Encryption.makePrivateKey(prk);
            PBK_KDC = Encryption.makePublicKey(K_PBK);
            ORIGINAL_KEY = PreprogrammedKeys.getKey(id);
            if (ORIGINAL_KEY == null) { throw new Exception("Could not get original key"); }
        } catch (Exception e) { e.getLocalizedMessage(); }
        
        
        this.syncKeys();
        
        this.connect(pbk); // Do initial connection
        this.sendId(); // May delete if unecessary
        this.sendNonces(); // May delete if unecessary
    }
    
    private String read() { try { return input.readLine(); } catch (IOException e) { return null;} }
    private void write(String s) { try {  output.println(s); } catch (Exception e) {} }
    
    private void syncKeys() { // ATM Setup
        Nonce n = new Nonce();
        String out = id + DELIM + n.toString();
        write(out);
        String in = read(); // id||NA||NB
        String parts [] = in.split(DELIM_REGEX);
        id_KDC = parts[0];
        Nonce n_1 = Nonce.toNonce(parts[1]);
        Nonce n_b = Nonce.toNonce(parts[2]);
        if (!n.equals(n_1) || nonces.contains(n_b.toString())) {
            System.out.println(ansi.BOLD + ansi.BRED + "Nonces aren't working out" + ansi.RESET);
            System.exit(-1);
        }
        nonces.add(n_b.toString());
        // By this point server & client agree on key
    }
    
    /**
     * Creates the MasterSecret Symmetric Key
     * @param A The client Nonce
     * @param B The server Nonce
     * @return Returns true on success
     */
    private boolean makeMasterSecret(Nonce A, Nonce B) {
        try {
            MasterSecret = Encryption.deriveMasterSecret(ORIGINAL_KEY, A, B);
ENC_KEY = Encryption.deriveEncryptionKey(MasterSecret);
MAC_KEY = Encryption.deriveHMACKey(MasterSecret);
            //System.out.println(MasterSecret.toString()); // Debug to see if keys match, MP:added 2 more secret keys
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    /**
     * ATM->BANK: E(K_O, UNAME||H(PASSWORD)||NA)
     * BANK->ATM: E(K_O, NAME||NA||NB)
     * Initial login step
     * @return Returns true on successful login
     */
    private boolean login() {
        
        // ATM->BANK: E(K_O, UNAME||H(PASSWORD)||NA)
        
        System.out.print(ansi.YELLOW + "Username: " + ansi.RESET);
        String uname = SCAN.nextLine(); // Get username
        System.out.print(ansi.YELLOW + "Password: " + ansi.BLACK + ansi.BGBLACK);
        String pwd = SCAN.nextLine(); // Get pwrd
        System.out.print(ansi.RESET);
        try { // Hash password
            pwd = Encryption.hash(pwd);
        } catch (Exception e) { 
            e.getStackTrace(); 
            return false;
        }
        Nonce n = new Nonce(); // Create new nonce
        String out = uname + DELIM + pwd + DELIM + n.toString(); // Fill message
        String enc = Encryption.encrypt(out, ORIGINAL_KEY); // Encrypt message
        write(enc); // Send msg
        
        // BANK->ATM: E(K_O, NAME||NA||NB)
        
        String in = read(); // Read response
        if (in.equals("ERROR")) { return false; }
        String dec = Encryption.decrypt(in, ORIGINAL_KEY); // Decrypt response
        String splits[] = dec.split(DELIM_REGEX); // Split along delimiter
        id_KDC = splits[0]; // First is id
        String my_nonce = splits[1]; // Next is the nonce sent
        if (!n.equals(Nonce.toNonce(my_nonce))) {  // check nonces match
            System.out.println(ansi.BOLD + ansi.RED + "Did not recieve the original nonce back -- Discarding message" + ansi.RESET);
            return false;
        }
        
        String nonce = splits[2]; // Last is a new nonce sent by server
        if (nonces.contains(nonce)) { // If nonce is seen before
            System.out.println(ansi.BOLD + ansi.RED + "Received a repeat nonce -- Discarding message" + ansi.RESET);
            return false;
        } else { nonces.add(nonce); } // Add to list
        System.out.println(ansi.GREEN + "Successfully logged in" + ansi.RESET);
        Nonce b = Nonce.toNonce(nonce);
        return makeMasterSecret(n, b);
    }
    
    /**
     * Now has the user login step
     * @param pbk 
     */
    private void connect(String pbk) {
        String out = pbk;
        System.out.println(ansi.BBLACK + "Initiating Setup with KDC Server\033[0m" + ansi.RESET);
        
        while (!login());
    }
    
    
    /**
     * MAY NOT NEED
     */
    private void sendId() {
        String out = id; // Line 1
        write(out);
        
        String in = read(); // Line 2 read
        String dec = Encryption.decrypt(in, PRK_SELF); // Decrypt using private key
        String [] msg = dec.split(DELIM_REGEX);
        N_KDC =  Nonce.toNonce(msg[0]);
        id_KDC = msg[1];
    }
    
    /**
     * MAY NOT NEED
     */
    private void sendNonces() {
        String out = N_self.toString() + DELIM + N_KDC.toString();
        String enc = Encryption.encrypt(out, PBK_KDC);
        write(enc);
        
        // Read both responses
        String msg1 = read();
        String msg2 = read();
        
        // Decrypt
        String msg1_dec = Encryption.decrypt(msg1, PRK_SELF);
        
        Nonce temp = Nonce.toNonce(msg1_dec);
        
        String msg2_dec = Encryption.double_decrypt(msg2, PRK_SELF, PBK_KDC); // Double decrypt response 2
        
        byte[] key_bytes = Base64.getDecoder().decode(msg2_dec);
        SYM_KEY = new SecretKeySpec(key_bytes, "AES");
    }
    
    private void listen() { 
        while (running) {
            String in = read();
            if (in == null) { break; }
            if (in.charAt(0) == RESERVED_CHAR) { // CHAR|ID|PUBK
                String other_id = in.split(DELIM_REGEX)[1];
                if (other_id.equals(this.id) || key_map.containsKey(other_id)) { continue; }
                String other_pbk = in.split(DELIM_REGEX)[2];
                try { 
                    key_map.put(other_id, Encryption.makePublicKey(other_pbk)); 
                    System.out.println("Added new public key : \033[32m" + this.id + DELIM + other_id + "\033[0m");
                } catch (Exception e) { System.out.println("\033[31mCould not add public key\033[0m"); }
                
            } else { // E(Ks, [IDclient, M])||Sigclient(IDclient, M)
                String text[] = in.split(DELIM_REGEX);
                String M = Encryption.decrypt(text[0], SYM_KEY);
                System.out.println("\nMessage : \033[31m" + M + "\033[0m");
                System.out.println("Signature : \033[32m" + text[1] + "\033[0m");
                String valid[] = M.split(DELIM_REGEX); // valid[0] = id, valid[1] = M
                System.out.println(valid[0] + " and " + this.id);
                if  (valid[0].equals(this.id)) { continue; }
                
                PublicKey pk = key_map.get(valid[0]);
                if (pk == null) { continue; } // Doesnt know who sent the msg
                boolean verified = false;
                
                try { // Check if the signature is valid
                    verified = Encryption.verify(valid[1], Encryption.conv(text[1]), pk);
                } catch (Exception e) {}
                
                if (!verified) {
                    System.out.println("Received an unverified message -- msg thown out");
                    continue;
                }
                
                System.out.print("Received a verifid message from " + valid[0] + ": \033[93m");
                System.out.println(valid[1] + "\033[0m");
                System.out.print("What message would you like to send: ");
            }
        }
    }
    
    private void messageOthers() { // Client -> KDC : E(Ks, [IDclient, M]) || Sig_client(IDclient, M)
        while (running) {
            System.out.print("What message would you like to send: ");
            String M = SCAN.nextLine();
            String sig = Encryption.sign(M, PRK_SELF);
            String msg = this.id + DELIM + M;
            String enc = Encryption.encrypt(msg, SYM_KEY);
            write(enc + DELIM + sig + DELIM + new Nonce().toString()); // Adds a nonce to prevent replay
            System.out.println("Sent message");
        }
    }
    
    private void run () { // Main client loop
        new Thread(this::listen).start();
        try { TimeUnit.SECONDS.sleep(5); } catch (Exception e) {}
        new Thread(this::messageOthers).start();
    }
    
    /**
     * Shuts down the thread
     */
    private void shutdown() { // Shutdown from termination
        running = false;
        try {
            System.out.println("\n\033[91m\033[1mShutting down " + this.id + "\033[0m");
            output.close();
            input.close();
            sock.close();
        } catch (IOException ex) { System.out.println("rip"); }
    }
    
    protected void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdown()));
        run();
    }
    
}