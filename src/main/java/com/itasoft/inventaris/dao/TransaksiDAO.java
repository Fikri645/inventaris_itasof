package com.itasoft.inventaris.dao;

import com.itasoft.inventaris.model.Transaksi;
import com.itasoft.inventaris.util.DatabaseConnection;
import com.itasoft.inventaris.model.Barang; // Untuk mengambil nama barang

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransaksiDAO {

    private BarangDAO barangDAO; // Untuk mengupdate stok di tabel items

    public TransaksiDAO() {
        this.barangDAO = new BarangDAO();
    }

    public boolean addTransaksi(Transaksi transaksi) {
        String sqlInsertTransaksi = "INSERT INTO stock_transactions (item_id, user_id, tipe_transaksi, jumlah, tanggal_transaksi, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return false;

            conn.setAutoCommit(false); // Mulai transaksi database

            // 1. Insert ke stock_transactions
            try (PreparedStatement pstmtTransaksi = conn.prepareStatement(sqlInsertTransaksi, Statement.RETURN_GENERATED_KEYS)) {
                pstmtTransaksi.setInt(1, transaksi.getItemId());
                pstmtTransaksi.setInt(2, transaksi.getUserId());
                pstmtTransaksi.setString(3, transaksi.getTipeTransaksi()); // "MASUK" atau "KELUAR"
                pstmtTransaksi.setInt(4, transaksi.getJumlah());
                pstmtTransaksi.setTimestamp(5, transaksi.getTanggalTransaksi() != null ? transaksi.getTanggalTransaksi() : new Timestamp(System.currentTimeMillis()));
                pstmtTransaksi.setString(6, transaksi.getKeterangan());

                int affectedRows = pstmtTransaksi.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Gagal menambahkan transaksi, tidak ada baris yang terpengaruh.");
                }
                // Ambil ID transaksi yang baru dibuat (opsional, jika diperlukan)
                // try (ResultSet generatedKeys = pstmtTransaksi.getGeneratedKeys()) {
                // if (generatedKeys.next()) {
                // transaksi.setId(generatedKeys.getInt(1));
                // }
                // }
            }

            // 2. Update stok di tabel items
            int quantityChange = transaksi.getTipeTransaksi().equalsIgnoreCase("MASUK") ? transaksi.getJumlah() : -transaksi.getJumlah();
            boolean stockUpdated = barangDAO.updateStock(transaksi.getItemId(), quantityChange, conn); // Gunakan koneksi yang sama

            if (!stockUpdated) {
                throw new SQLException("Gagal mengupdate stok barang.");
            }

            conn.commit(); // Commit transaksi jika semua berhasil
            success = true;

        } catch (SQLException e) {
            System.err.println("Error saat menambah transaksi: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback jika terjadi error
                    System.err.println("Transaksi di-rollback.");
                } catch (SQLException ex) {
                    System.err.println("Error saat rollback: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            // Rethrow atau tampilkan pesan ke user
            if (e.getMessage().startsWith("Stok barang tidak mencukupi")) {
                javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "Error Transaksi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Kembalikan ke mode auto-commit
                    // conn.close(); // Jangan tutup koneksi global di sini jika masih dipakai
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return success;
    }

    public List<Transaksi> getAllTransaksi() {
        List<Transaksi> transaksis = new ArrayList<>();
        // Query ini mengambil juga nama barang dan username pelaku transaksi
        String sql = "SELECT st.id, st.item_id, i.nama_barang, st.user_id, u.username AS username_pelaku, " +
                "st.tipe_transaksi, st.jumlah, st.tanggal_transaksi, st.keterangan " +
                "FROM stock_transactions st " +
                "JOIN items i ON st.item_id = i.id " +
                "JOIN users u ON st.user_id = u.id " +
                "ORDER BY st.tanggal_transaksi DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (conn == null) return transaksis;

            while (rs.next()) {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(rs.getInt("id"));
                transaksi.setItemId(rs.getInt("item_id"));
                transaksi.setNamaBarang(rs.getString("nama_barang")); // dari join
                transaksi.setUserId(rs.getInt("user_id"));
                transaksi.setUsernamePelaku(rs.getString("username_pelaku")); // dari join
                transaksi.setTipeTransaksi(rs.getString("tipe_transaksi"));
                transaksi.setJumlah(rs.getInt("jumlah"));
                transaksi.setTanggalTransaksi(rs.getTimestamp("tanggal_transaksi"));
                transaksi.setKeterangan(rs.getString("keterangan"));
                transaksis.add(transaksi);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all transaksis: " + e.getMessage());
            e.printStackTrace();
        }
        return transaksis;
    }
}