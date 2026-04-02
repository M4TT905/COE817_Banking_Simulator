//MP: this was empty... so in order to test part 3 i had to fill it up with code, now this code here is a placeholder feel free to remove it only if needed!
package main;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PreprogrammedKeys {

    public static SecretKey getKey(String id) {
        try {
            // Simple fixed keys for testing (must be 16 bytes for AES-128)
            if (id.equals("Alice")) {
                return new SecretKeySpec("AAAAAAAAAAAAAAAA".getBytes(), "AES");
            } else if (id.equals("Bob")) {
                return new SecretKeySpec("BBBBBBBBBBBBBBBB".getBytes(), "AES");
            } else if (id.equals("Charlie")) {
                return new SecretKeySpec("CCCCCCCCCCCCCCCC".getBytes(), "AES");
            } else if (id.equals("AUDIT")) {
                return new SecretKeySpec("0123456789ABCDEF".getBytes(), "AES");
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}