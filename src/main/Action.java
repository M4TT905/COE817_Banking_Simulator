/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author sugen
 */
public class Action {
    ActionType actionType;
    double amount;
    /**
     * Enum representing the type of transaction action.
     */
    public enum ActionType {
        withdraw, deposit, inquiry
    }
    
    public Action (ActionType actionType, double amount){
        this.actionType=actionType;
        this.amount=amount;
    }
    public Action (ActionType actionType){
        this.actionType=actionType;
        this.amount = 0;
    }    
}
