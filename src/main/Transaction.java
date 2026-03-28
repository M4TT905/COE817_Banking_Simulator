/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import java.security.Key;
import java.time.LocalDateTime;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
/**
 * The Transaction class represents secure banking operations between an ATM client
 * and a bank server. It supports actions such as withdrawal, deposit,
 * and balance inquiry.
 *
 * This class is responsible for:
 * - Creating transaction messages
 * - Securing messages using encryption and HMAC
 * - Verifying and decoding received messages
 * - Auditing transactions for accountability
 *
 * Security Note:
 * All messages are encrypted and authenticated using an HMAC to ensure
 * confidentiality and integrity.
 */
public class Transaction {

    String auditFilePath = "audit.txt";

    /**
     * Creates and secures a transaction message for actions that involve a monetary value,
     * such as deposit or withdrawal.
     *
     * The message format before processing is:
     * actionType|amount
     *
     * After processing, the message is encrypted and appended with an HMAC.
     *
     * @param action the transaction action (type and amount)
     * @param encryptKey the key used for encryption
     * @param HMACkey the key used to generate the HMAC
     * @return the secured message (HMAC + encrypted message)
     */
    public String createActionMessage(Action action, Key encryptKey, Key HMACkey) {

        String message = action.actionType.toString() + "|" + String.valueOf(action.amount);
        String processedMessage = applyTransacationProtocol(message, encryptKey, HMACkey);

        return processedMessage;
    }

    /**
     * Processes an incoming secured message.
     * This method is intended to:
     * - Verify the HMAC
     * - Decrypt the message
     * - Convert it back into an Action object
     *
     * NOTE: Method implementation is incomplete.
     *
     * @param message the received secured message
     * @return the processed message result (implementation dependent)
     */
    public String processActionMessage(String message) {

        String processedMessage = null;

        return processedMessage;
    }

    /**
     * Applies the transaction security protocol to a plaintext message.
     * The protocol consists of:
     * 1. Encrypting the message
     * 2. Generating an HMAC over the encrypted message
     *
     * Final format:
     * HMAC|encryptedMessage
     *
     * @param message the plaintext transaction message
     * @param encryptKey the key used for encryption
     * @param HMACkey the key used for HMAC generation
     * @return the secured message
     */
    private String applyTransacationProtocol(String message, Key encryptKey, Key HMACkey) {

        String encryptedMessage = Encryption.encrypt(message, encryptKey);
        String processedMessage = Encryption.generateHMAC(HMACkey, encryptedMessage) + "|" + encryptedMessage;

        return processedMessage;
    }

    /**
     * Reverses the transaction security protocol.
     * This method:
     * 1. Splits the message into HMAC and encrypted content
     * 2. Verifies the HMAC
     * 3. Decrypts the message
     * 4. Parses it into an Action object
     *
     * @param message the secured message in the format HMAC|encryptedMessage
     * @param encryptKey the key used for decryption
     * @param HMACkey the key used for HMAC verification
     * @return the reconstructed Action object
     * @throws IllegalArgumentException if HMAC verification fails or message format is invalid
     */
    private Action removeAndVerifyTransacationProtocol(String message, Key encryptKey, Key HMACkey)
            throws IllegalArgumentException {

        String[] messages = message.split("\\|");

        String decryptedMessage = Encryption.decrypt(messages[1], encryptKey);

        if (!Encryption.verifyHMAC(HMACkey, decryptedMessage, messages[0])) {
            throw new IllegalArgumentException("HMAC Invalid");
        }

        String[] actionValue = decryptedMessage.split("\\|");

        return new Action(
                Action.ActionType.valueOf(actionValue[0]),
                Double.parseDouble(actionValue[1])
        );
    }

    /**
     * Records a transaction in an encrypted audit log file.
     * Each entry includes:
     * - Action details
     * - Customer ID
     * - Timestamp
     *
     * The log entry is encrypted before being written to disk.
     *
     * @param action the transaction performed
     * @param id the customer ID
     * @param key the encryption key for securing the audit log
     * @return true if the transaction is successfully logged, false otherwise
     * @throws IOException if a file writing error occurs
     */
    private boolean auditTransaction(Action action, String id, Key key) throws IOException {

        LocalDateTime now = LocalDateTime.now();
        String auditDetails = action.toString() + " " + id + " " + now;
        String encryptedString = Encryption.encrypt(auditDetails, key);

        try (FileWriter fw = new FileWriter(auditFilePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(encryptedString);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}