/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author sugen
 */
/**
 * The Transaction class represents secure banking operations between an ATM client
 * and a bank server. It supports three types of actions: withdrawal, deposit,
 * and balance inquiry.
 *
 * This class is responsible for:
 * - Sending transaction requests securely
 * - Applying a transaction protocol (e.g., encryption + MAC)
 * - Auditing transactions for accountability
 */
public class Transaction {

    /**
     * Enum representing the type of transaction action.
     */
    public enum Action {
        withdraw, deposit, inquiry
    }

    /**
     * Sends a transaction request that does not require an amount
     * (e.g., balance inquiry).
     *
     * @param action the transaction action (must be inquiry)
     * @return the status of the transaction ("success" or "failed")
     */
    public String sendAction(Action action){
        String status = "failed";
        return status;
    }

    /**
     * Sends a transaction request that involves a monetary amount
     * (e.g., deposit or withdrawal).
     *
     * @param action the transaction action (deposit or withdraw)
     * @param amount the amount involved in the transaction
     * @return the status of the transaction ("success" or "failed")
     */
    public String sendAction(Action action, double amount){
        String status = "failed";
        return status;
    }

    /**
     * Applies the secure transaction protocol to a message.
     * This includes encryption and MAC generation to ensure
     * confidentiality and integrity.
     *
     * @param message the plaintext transaction message
     * @return the secured (encrypted + MAC) message
     */
    private String applyTransacationProtocol(String message) {
        return message;
    }

    /**
     * Records a transaction in the audit log.
     * The log entry includes customer ID, action type, and timestamp.
     * The log data must be encrypted before storage.
     *
     * @param action the action performed by the customer
     * @param customerId the ID of the customer
     * @return true if audit logging succeeds, false otherwise
     */
    private boolean auditTransaction(Action action) {
        return false;
    }
}
