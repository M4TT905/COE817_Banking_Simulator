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
import java.util.Map;
import java.util.Scanner;
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
    
    private String id;
    private String id_KDC;
    
    private Nonce N_self = null;
    private Nonce N_KDC = null;
    
    private PrivateKey PRK_SELF = null;
    private PublicKey PBK_KDC = null;
    private SecretKey SYM_KEY = null;
    
    
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
        } catch (Exception e) { e.getLocalizedMessage(); }
        
        this.connect(pbk); // Do initial connection
        this.sendId();
        this.sendNonces();
    }
    
    private String read() { try { return input.readLine(); } catch (Exception e) { return null;} }
    private void write(String s) { try {  output.println(s); } catch (Exception e) {} }
    
    
    
    private void connect(String pbk) {
        String out = pbk;
        System.out.println("\033[90mInitiating Setup with KDC Server\033[0m");
        write(out);
        String in = read(); // Unblocks
        System.out.println("\033[92mConnected to KDC Server\033[0m");
    }
    
    private void sendId() {
        String out = id; // Line 1
        write(out);
        
        String in = read(); // Line 2 read
        String dec = Encryption.decrypt(in, PRK_SELF); // Decrypt using private key
        String [] msg = dec.split(DELIM_REGEX);
        N_KDC =  Nonce.toNonce(msg[0]);
        id_KDC = msg[1];
    }
    
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