package main;

import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author matthewhvizdos
 */
public class KeyStringGenerator {
    private static KeyGenerator kg;
    
    static { 
        try {
            kg = KeyGenerator.getInstance("AES");
            kg.init(128);
        } catch (Exception e) {}
    }
    
    private KeyStringGenerator() {}
    
    public static SecretKey getNewKey() {
        return kg.generateKey();
    }
    
    public static String SKeyToString(SecretKey s) {
        return Base64.getEncoder().encodeToString(s.getEncoded());
    }
}
