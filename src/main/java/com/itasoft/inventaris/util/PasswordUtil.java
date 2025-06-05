package com.itasoft.inventaris.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    // Metode untuk menghasilkan hash SHA-256 dari password
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    plainTextPassword.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            // Algoritma SHA-256 seharusnya selalu ada
            System.err.println("Error hashing password: Algoritma SHA-256 tidak ditemukan.");
            e.printStackTrace();
            // Dalam kasus nyata, ini adalah error kritis dan harus ditangani dengan baik
            // Mungkin melempar RuntimeException atau mengembalikan null dan ditangani di pemanggil
            return null; // Atau throw new RuntimeException("SHA-256 not available", e);
        }
    }

    // Metode helper untuk mengubah array byte menjadi string heksadesimal
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Metode untuk memverifikasi password plain text dengan hash yang tersimpan
    public static boolean checkPassword(String plainTextPassword, String hashedPasswordFromDb) {
        if (plainTextPassword == null || hashedPasswordFromDb == null) {
            return false;
        }
        String hashedPlainTextPassword = hashPassword(plainTextPassword);
        return hashedPasswordFromDb.equals(hashedPlainTextPassword);
    }

    // (Opsional) Main method untuk menguji atau generate hash
    public static void main(String[] args) {
        String pass1 = "admin123";
        String hashedPass1 = hashPassword(pass1);
        System.out.println("Password: " + pass1);
        System.out.println("Hashed  : " + hashedPass1);
        System.out.println("Verifikasi 'admin123': " + checkPassword("admin123", hashedPass1)); // true
        System.out.println("Verifikasi 'admin1234': " + checkPassword("admin1234", hashedPass1)); // false

        String pass2 = "staff123";
        String hashedPass2 = hashPassword(pass2);
        System.out.println("\nPassword: " + pass2);
        System.out.println("Hashed  : " + hashedPass2);
    }
}