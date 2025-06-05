package com.itasoft.inventaris.dao;

import com.itasoft.inventaris.model.User;
import com.itasoft.inventaris.util.DatabaseConnection;
import com.itasoft.inventaris.util.PasswordUtil; // <-- Tambahkan import ini

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User validateLogin(String username, String plainTextPasswordInput) { // Ubah nama parameter
        User user = null;
        // Ambil hash password dari database berdasarkan username
        String sql = "SELECT id, username, password AS hashedPassword, nama_lengkap, role FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Tidak dapat membuat koneksi ke database untuk validasi login.");
                return null;
            }

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPasswordFromDb = rs.getString("hashedPassword");

                    // Verifikasi password yang diinput dengan hash dari database
                    if (PasswordUtil.checkPassword(plainTextPasswordInput, hashedPasswordFromDb)) {
                        user = new User();
                        user.setId(rs.getInt("id"));
                        user.setUsername(rs.getString("username"));
                        // Jangan set password plain text ke objek User setelah validasi
                        user.setNamaLengkap(rs.getString("nama_lengkap"));
                        user.setRole(rs.getString("role"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat validasi login: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    // Jika Anda membuat fitur registrasi atau ganti password, gunakan PasswordUtil.hashPassword()
    public boolean registerUser(String username, String plainTextPassword, String namaLengkap, String role) {
        String hashedPassword = PasswordUtil.hashPassword(plainTextPassword);
        if (hashedPassword == null) {
            System.err.println("Gagal melakukan hashing password untuk user baru.");
            return false;
        }

        String sql = "INSERT INTO users (username, password, nama_lengkap, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return false;
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, namaLengkap);
            pstmt.setString(4, role);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}