package com.example.song_finder_fx.Controller;

import java.util.HashMap;
import java.util.Map;

public class ISRCEncryptor {

    // Custom mapping for substitution cipher
    private static final Map<Character, Character> encryptionMap = new HashMap<>();
    private static final Map<Character, Character> decryptionMap = new HashMap<>();

    static {
        // Mapping for digits 0-9
        encryptionMap.put('0', 'A');
        encryptionMap.put('1', 'B');
        encryptionMap.put('2', 'C');
        encryptionMap.put('3', 'D');
        encryptionMap.put('4', 'E');
        encryptionMap.put('5', 'F');
        encryptionMap.put('6', 'G');
        encryptionMap.put('7', 'H');
        encryptionMap.put('8', 'I');
        encryptionMap.put('9', 'J');

        // Mapping for uppercase letters A-Z
        encryptionMap.put('A', 'K');
        encryptionMap.put('B', 'L');
        encryptionMap.put('C', 'M');
        encryptionMap.put('D', 'N');
        encryptionMap.put('E', 'O');
        encryptionMap.put('F', 'P');
        encryptionMap.put('G', 'Q');
        encryptionMap.put('H', 'R');
        encryptionMap.put('I', 'S');
        encryptionMap.put('J', 'T');
        encryptionMap.put('K', 'U');
        encryptionMap.put('L', 'V');
        encryptionMap.put('M', 'W');
        encryptionMap.put('N', 'X');
        encryptionMap.put('O', 'Y');
        encryptionMap.put('P', 'Z');
        encryptionMap.put('Q', '1');
        encryptionMap.put('R', '2');
        encryptionMap.put('S', '3');
        encryptionMap.put('T', '4');
        encryptionMap.put('U', '5');
        encryptionMap.put('V', '6');
        encryptionMap.put('W', '7');
        encryptionMap.put('X', '8');
        encryptionMap.put('Y', '9');
        encryptionMap.put('Z', '0');

        // Create reverse mapping for decryption
        for (Map.Entry<Character, Character> entry : encryptionMap.entrySet()) {
            decryptionMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static String encryptISRC(String isrc) {
        StringBuilder encryptedISRC = new StringBuilder();

        for (char c : isrc.toCharArray()) {
            encryptedISRC.append(encryptionMap.getOrDefault(c, c));
        }

        return encryptedISRC.toString();
    }

    // Decryption method to reverse the encryption
    private static String decryptISRC(String encryptedISRC) {
        StringBuilder decryptedISRC = new StringBuilder();

        for (char c : encryptedISRC.toCharArray()) {
            decryptedISRC.append(decryptionMap.getOrDefault(c, c));
        }

        return decryptedISRC.toString();
    }

    public static void main(String[] args) {
        String isrc = "LKA0W2400308";
        String encryptedISRC = encryptISRC(isrc);

        System.out.println("Original ISRC: " + isrc);
        System.out.println("Encrypted ISRC: " + encryptedISRC);

        String decryptedISRC = decryptISRC(encryptedISRC);
        System.out.println("Decrypted ISRC: " + decryptedISRC);
    }
}
