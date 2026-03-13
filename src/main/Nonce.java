package main;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matthewhvizdos
 */
public class Nonce {
    private byte[] data = new byte[16];
    private SecureRandom sr = new SecureRandom();
    
    
    public Nonce() {
        sr.nextBytes(data);
    }
    
    public void resetNonce() {
        sr.nextBytes(data);
    }
    
    @Override
    public String toString() {
        return Base64.getEncoder().encodeToString(data);
    }
    
    
    public static byte[] toByteArray(String s) {
        return Base64.getDecoder().decode(s);
    }
    
    public static byte[] toByteArray(Nonce n) {
        return n.data;
    }
    
    /**
     * Converts a string into a Nonce
     * @param s
     * @return 
     */
    public static Nonce toNonce(String s) {
        return newNonce(toByteArray(s));
    }
    
    /**
     * Debug Function
     */
    public void print() {
        System.out.println(this.toString());
    }
    
    /**
     * Converts a byte array into a nonce
     * @param b
     * @return
     * @throws IllegalArgumentException 
     */
    public static Nonce newNonce(byte[] b) throws IllegalArgumentException {
        if (b.length != 16) {
            throw new IllegalArgumentException("\033[31mNonce must be 16 bytes\033[0m");
        }
        Nonce temp = new Nonce();
        temp.data = b.clone();
        return temp;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o.getClass() != this.getClass()) {
            return false;
        }
        
        Nonce n = (Nonce) o;
        return Arrays.equals(this.data, n.data);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
