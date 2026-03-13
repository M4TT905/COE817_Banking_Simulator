package main;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
public class Encryption {
    /**
     * Symmetrically encrypts a string using the provided key
     * @param input Input string
     * @param k Input Key
     * @return Encrypted String 
     */
    public static String encrypt(String input, Key k) {
        try {
            Cipher c = null;
            if (k instanceof SecretKey) {
                c = Cipher.getInstance("AES");
            } else if (k instanceof PrivateKey) {
                c = Cipher.getInstance("RSA");
            } else if (k instanceof PublicKey) {
                c = Cipher.getInstance("RSA");
            } else {
                throw new InvalidKeyException("Invalid key class");
            }
            c.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted_text = c.doFinal(input.getBytes(StandardCharsets.UTF_8));
            String out = Base64.getEncoder().encodeToString(encrypted_text);
            return out;
        } catch (Exception e) { return null; }
    }
    
    /**
     * Symmetrically decrypts a string using the provided key
     * @param input Input encrypted string
     * @param k input key
     * @return Decrypted string
     */
    public static String decrypt(String input, Key k) {
        try {
            Cipher c = null; 
            if (k instanceof SecretKey) {
                c = Cipher.getInstance("AES");
            } else if (k instanceof PrivateKey) {
                c = Cipher.getInstance("RSA");
            } else if (k instanceof PublicKey) {
                c = Cipher.getInstance("RSA");
            } else {
                throw new InvalidKeyException("Invalid key class");
            }
            c.init(Cipher.DECRYPT_MODE, k);
            byte[] decrypted_text = c.doFinal(Base64.getDecoder().decode(input));
            return new String(decrypted_text, StandardCharsets.UTF_8);
        } catch (Exception e) { return null; }
    }
    
    
    /**
     * Double encrypt a message
     * @param msg Message to encrypt twice
     * @param prk Must be a 1024 bit Private Key
     * @param pbk Must be a 2048 bit Public Key
     * @return Encrypted string to write
     */
    public static String double_encrypt(String msg, PrivateKey prk, PublicKey pbk) {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        byte[] inner;
        byte[] outer;
        try {
            Cipher in = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            in.init(Cipher.ENCRYPT_MODE, prk);
            inner = in.doFinal(bytes);
            
            Cipher out = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            out.init(Cipher.ENCRYPT_MODE, pbk);
            outer = out.doFinal(inner);
        } catch (Exception e) { return null; }
        return Base64.getEncoder().encodeToString(outer);
    }
    
    /**
     * Double decrypt a message
     * @param msg Message to decrypt twice
     * @param prk Must be a 1024 bit Private Key
     * @param pbk Must be a 2048 bit Public Key
     * @return Decrypted string 
     */
    public static String double_decrypt(String msg, PrivateKey prk, PublicKey pbk) {
        byte[] bytes = Base64.getDecoder().decode(msg);
        byte[] outer;
        byte[] inner;
        
        try {
            Cipher out = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            out.init(Cipher.DECRYPT_MODE, prk);
            outer = out.doFinal(bytes);
            
            Cipher in = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            in.init(Cipher.DECRYPT_MODE, pbk);
            inner = in.doFinal(outer);
        } catch (Exception e) { return null; }
        return new String(inner, StandardCharsets.UTF_8);
    }
    
    /**
     * Convert a string into base64 encoding bytes
     * @param s String to convert
     * @return Bytes
     */
    public static byte[] conv(String s) { return Base64.getDecoder().decode(s); }
    
    /**
     * Convert a string into a private key
     * @param prk The private key string
     * @return Returns a PrivateKey object
     * @throws Exception 
     */
    public static PrivateKey makePrivateKey(String prk) throws Exception {
        PKCS8EncodedKeySpec prspec = new PKCS8EncodedKeySpec(conv(prk));
        return KeyFactory.getInstance("RSA").generatePrivate(prspec);
    }
    
    /**
     * Covert a string into a public key
     * @param pbk The public key string
     * @return Returns a PublicKey object
     * @throws Exception 
     */
    public static PublicKey makePublicKey(String pbk) throws Exception {
        X509EncodedKeySpec pbspec = new X509EncodedKeySpec(conv(pbk));
        return KeyFactory.getInstance("RSA").generatePublic(pbspec);
    }
    
    /**
     * Digital signature sign function
     * @param msg Message to sign
     * @param prk The private key to sign with
     * @return Return a string of just the digital signature
     */
    public static String sign(String msg, PrivateKey prk) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(prk);
            signature.update(msg.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Digital signature verify function
     * @param msg Message to verify
     * @param sig Digital signature of the message
     * @param pbk The public key of the client the message came from
     * @return True if the signature is valid, false otherwise
     */
    public static boolean verify(String msg, byte[] sig, PublicKey pbk) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(pbk);
            signature.update(msg.getBytes(StandardCharsets.UTF_8));
            return signature.verify(sig);
        } catch (Exception e) {
            return false;
        }
    }
    
}
