package com.itasoft.inventaris.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane; // Untuk error GUI

public class DatabaseConnection {
    private static final String URL = "jdbc:mariadb://localhost:3306/db_inventaris_sederhana";
    private static final String USER = "root"; // Ganti dengan username Anda
    private static final String PASSWORD = "root"; // Ganti dengan password Anda
    private static Connection connection = null;

    public static Connection getConnection() {
        // if (connection == null) { // Ini akan membuat koneksi singleton, bisa jadi masalah jika tidak di-manage dgn baik
        try {
            // Jika koneksi sudah ada dan masih valid, kembalikan.
            // Jika tidak, buat koneksi baru.
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            // Daftarkan driver MariaDB (tidak wajib untuk JDBC 4.0+ jika JAR ada di classpath)
            // Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            // System.out.println("Koneksi ke database berhasil!"); // Kurangi log console di produksi
        } catch (SQLException e) {
            System.err.println("Koneksi ke database gagal: " + e.getMessage());
            e.printStackTrace();
            // Tampilkan pesan error ke pengguna jika ini aplikasi GUI
            // JOptionPane.showMessageDialog(null, "Koneksi Database Gagal: " + e.getMessage(), "Error Koneksi", JOptionPane.ERROR_MESSAGE);
            connection = null; // Pastikan connection null jika gagal
        }
        // }
        return connection;
    }

    // Metode ini mungkin tidak ideal untuk koneksi global,
    // lebih baik ditutup setelah selesai operasi besar atau saat aplikasi exit.
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

    // Untuk pengujian
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