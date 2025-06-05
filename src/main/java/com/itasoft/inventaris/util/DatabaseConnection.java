package com.itasoft.inventaris.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String URL = "jdbc:mariadb://localhost:3306/db_inventaris_sederhana";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static Connection connection = null;

    public static Connection getConnection() {
        // if (connection == null) { // Ini akan membuat koneksi singleton, bisa jadi masalah jika tidak di-manage dgn baik
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            // System.out.println("Koneksi ke database berhasil!");
        } catch (SQLException e) {
            System.err.println("Koneksi ke database gagal: " + e.getMessage());
            e.printStackTrace();
            // JOptionPane.showMessageDialog(null, "Koneksi Database Gagal: " + e.getMessage(), "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            connection = null;
        }
        // }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
                connection = null;
                // System.out.println("Koneksi ke database ditutup.");
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // public static void main(String[] args) {
    //     Connection conn = DatabaseConnection.getConnection();
    //     if (conn != null) {
    //         System.out.println("Tes koneksi dari main method berhasil!");
    //         DatabaseConnection.closeConnection();
    //     } else {
    //         System.out.println("Tes koneksi dari main method gagal.");
    //     }
    // }
}