/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author jonathan
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.Key;
import javax.crypto.SecretKey;

public class DecryptAudit {

    private static final String AUDIT_FILE = "audit.txt";
    private static final SecretKey AUDIT_KEY = PreprogrammedKeys.getKey("AUDIT");

    public static void decryptAudit(Key key) {
        try (BufferedReader br = new BufferedReader(new FileReader(AUDIT_FILE))) {

            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String decrypted = Encryption.decrypt(line, key);

                if (decrypted == null) {
                    System.out.println(lineNumber + ": Failed");
                } else {
                    System.out.println(lineNumber + ": " + decrypted);
                }

                lineNumber++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        
        DecryptAudit.decryptAudit(AUDIT_KEY);
    }
}
