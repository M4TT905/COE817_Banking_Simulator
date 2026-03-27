/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.SecretKey;

/**
 *
 * @author matthewhvizdos
 */
public class ClientData {
    private String id = null;
    private PublicKey pbk = null;
    private SecretKey sk = null;
    private Nonce nonce = null;
    
    public ClientData() {}
    
    public void setPKey(String s) {
        try {
            byte[] bytes = Base64.getDecoder().decode(s);
            X509EncodedKeySpec pbspec = new X509EncodedKeySpec(bytes);
            pbk = KeyFactory.getInstance("RSA").generatePublic(pbspec);
        } catch (Exception e) {}
    }
    public PublicKey getPKey() { return pbk; }
    public String getPKeyString() { 
        if (pbk == null) { return null; }
        return Base64.getEncoder().encodeToString(pbk.getEncoded()); 
    }
    
    public void generateSKey() {
        sk = KeyStringGenerator.getNewKey();
    }
    public SecretKey getSKey() { return sk; }
    public String getSKeyString() { 
        if (sk == null) { return null; }
        return Base64.getEncoder().encodeToString(sk.getEncoded()); 
    }
    
    public void setId(String s) { id = s; }
    public String getId() { return id; }
    
    public void makeNonce() { nonce = new Nonce(); }
    public Nonce getNonce() { return nonce; }

    
}
