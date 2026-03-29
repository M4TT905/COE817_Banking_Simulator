package main;

import javax.crypto.SecretKey;

public class Part3Test {
    public static void main(String[] args) {
        try {
            // Step 1: get original shared key
            SecretKey originalKey = PreprogrammedKeys.getKey("Alice");

            // Step 2: make two nonces
            Nonce nonceA = new Nonce();
            Nonce nonceB = new Nonce();

            // Step 3: derive master secret
            SecretKey masterSecret = Encryption.deriveMasterSecret(originalKey, nonceA, nonceB);

            // Step 4: derive encryption and MAC keys
            SecretKey encKey = Encryption.deriveEncryptionKey(masterSecret);
            SecretKey macKey = Encryption.deriveHMACKey(masterSecret);

            // Print keys
            System.out.println("Master Secret: " + java.util.Base64.getEncoder().encodeToString(masterSecret.getEncoded()));
            System.out.println("ENC_KEY:       " + java.util.Base64.getEncoder().encodeToString(encKey.getEncoded()));
            System.out.println("MAC_KEY:       " + java.util.Base64.getEncoder().encodeToString(macKey.getEncoded()));

            // Step 5: create transaction
            Transaction transaction = new Transaction();
            Action action = new Action(Action.ActionType.deposit, 100.0);

            String securedMessage = transaction.createActionMessage(action, encKey, macKey);
            System.out.println("\nSecured Message:");
            System.out.println(securedMessage);

            // Step 6: verify and decode transaction
            Action recoveredAction = transaction.removeAndVerifyTransacationProtocol(securedMessage, encKey, macKey);

            System.out.println("\nRecovered Action:");
            System.out.println("Type: " + recoveredAction.actionType);
            System.out.println("Amount: " + recoveredAction.amount);

            System.out.println("\nPart 3 test passed.");

        } catch (Exception e) {
            System.out.println("Part 3 test failed.");
            e.printStackTrace();
        }
    }
}