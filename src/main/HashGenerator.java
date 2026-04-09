/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Scanner;

/**
 *
 * @author matthewhvizdos
 */
public class HashGenerator {
    
    private static final Scanner SCAN = new Scanner(System.in);
    public static void main(String[] args) {
        while (true) {
            System.out.print("Text to hash: ");
            String input = SCAN.nextLine();
            String hash = "";
            try {
                hash = Encryption.hash(input);
            } catch (Exception e) {}
            System.out.println("Output: " + hash);
        }
    }
}
