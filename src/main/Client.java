/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author matthewhvizdos
 */
public class Client {
    
    private volatile boolean running = true;
    
    private Socket sock = null;
    private PrintWriter output = null;
    private BufferedReader input = null;
    
    
    private String read() { try { return input.readLine(); } catch (Exception e) { return null;} }
    private void write(String s) { try {  output.println(s); } catch (Exception e) {} }
    
    
    public Client() {
    
    
    }
    
    
    
    private void user_input() {
        while (running) {
            
        }
    }
    
    private void run () { // Main client loop
        
    }
    
    private void shutdown() { // Shutdown from termination
        running = false;
        try {
            System.out.println("\n\033[91m\033[1mShutting down \033[0m");
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
