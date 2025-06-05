package com.itasoft.inventaris.dao;

import com.itasoft.inventaris.model.Barang;
import com.itasoft.inventaris.util.DatabaseConnection;

import javax.swing.*; // Untuk JOptionPane pada error delete
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class BarangDAO {

    public boolean addBarang(Barang barang) {
        String sql = "INSERT INTO items (kode_barang, nama_barang, kategori, satuan, stok_saat_ini, harga_beli, harga_jual) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Koneksi database null saat addBarang.");
                return false;
            }
            pstmt.setString(1, barang.getKodeBarang());
            pstmt.setString(2, barang.getNamaBarang());
            pstmt.setString(3, barang.getKategori());
            pstmt.setString(4, barang.getSatuan());
            pstmt.setInt(5, barang.getStokSaatIni());
            pstmt.setBigDecimal(6, barang.getHargaBeli());
            pstmt.setBigDecimal(7, barang.getHargaJual());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding barang: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBarang(Barang barang) {
        // Stok tidak diupdate langsung dari sini, melainkan melalui transaksi.
        // Jika ingin tetap bisa edit stok dari form master barang (misal, untuk koreksi stok awal),
        // kolom stok_saat_ini bisa dimasukkan ke query update di bawah ini.
        // Namun, untuk konsistensi, lebih baik stok hanya berubah karena transaksi.
        // Jika field stok di form master barang di-enable, query berikut bisa dipakai:
        // String sql = "UPDATE items SET nama_barang = ?, kategori = ?, satuan = ?, stok_saat_ini = ?, harga_beli = ?, harga_jual = ? WHERE id = ?";
        String sql = "UPDATE items SET nama_barang = ?, kategori = ?, satuan = ?, harga_beli = ?, harga_jual = ? WHERE id = ?";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Koneksi database null saat updateBarang.");
                return false;
            }
            pstmt.setString(1, barang.getNamaBarang());
            pstmt.setString(2, barang.getKategori());
            pstmt.setString(3, barang.getSatuan());
            // Jika stok diupdate dari form master (sesuaikan dengan query di atas):
            // pstmt.setInt(4, barang.getStokSaatIni());
            // pstmt.setBigDecimal(5, barang.getHargaBeli());
            // pstmt.setBigDecimal(6, barang.getHargaJual());
            // pstmt.setInt(7, barang.getId());
            pstmt.setBigDecimal(4, barang.getHargaBeli());
            pstmt.setBigDecimal(5, barang.getHargaJual());
            pstmt.setInt(6, barang.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating barang: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBarang(int barangId) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Koneksi database null saat deleteBarang.");
                return false;
            }
            pstmt.setInt(1, barangId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting barang: " + e.getMessage());
            if (e.getMessage().toLowerCase().contains("foreign key constraint fails")) {
                JOptionPane.showMessageDialog(null, "Tidak bisa menghapus barang karena sudah ada transaksi terkait.", "Error Hapus", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    public Barang getBarangById(int barangId) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Koneksi database null saat getBarangById.");
                return null;
            }
            pstmt.setInt(1, barangId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBarang(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching barang by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Barang getBarangByKode(String kodeBarang) {
        String sql = "SELECT * FROM items WHERE kode_barang = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                System.err.println("Koneksi database null saat getBarangByKode.");
                return null;
            }
            pstmt.setString(1, kodeBarang);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBarang(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching barang by Kode: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Metode untuk mengambil semua kategori unik
    public List<String> getUniqueCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT kategori FROM items WHERE kategori IS NOT NULL AND kategori != '' ORDER BY kategori ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (conn == null) {
                System.err.println("Koneksi database null saat getUniqueCategories.");
                return categories;
            }

            while (rs.next()) {
                categories.add(rs.getString("kategori"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching unique categories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    // Metode untuk mengambil barang dengan filter dan pencarian
    public List<Barang> getBarangFiltered(String searchTerm, String category) {
        List<Barang> barangs = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM items");
        List<Object> params = new ArrayList<>();
        boolean firstCondition = true;

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(firstCondition ? " WHERE (" : " AND (");
            sqlBuilder.append("kode_barang LIKE ? OR nama_barang LIKE ?)");
            params.add("%" + searchTerm.trim() + "%");
            params.add("%" + searchTerm.trim() + "%");
            firstCondition = false;
        }

        if (category != null && !category.trim().isEmpty() && !"Semua Kategori".equalsIgnoreCase(category)) {
            sqlBuilder.append(firstCondition ? " WHERE " : " AND ");
            sqlBuilder.append("kategori = ?");
            params.add(category.trim());
            // firstCondition = false; // Tidak perlu diubah lagi jika hanya ada 2 kondisi
        }

        sqlBuilder.append(" ORDER BY nama_barang ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            if (conn == null) {
                System.err.println("Koneksi database null saat getBarangFiltered.");
                return barangs;
            }

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    barangs.add(mapResultSetToBarang(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching filtered/searched barangs: " + e.getMessage());
            e.printStackTrace();
        }
        return barangs;
    }

    // getAllBarang sekarang menggunakan getBarangFiltered
    public List<Barang> getAllBarang() {
        return getBarangFiltered(null, "Semua Kategori"); // Default: tampilkan semua
    }

    private Barang mapResultSetToBarang(ResultSet rs) throws SQLException {
        Barang barang = new Barang();
        barang.setId(rs.getInt("id"));
        barang.setKodeBarang(rs.getString("kode_barang"));
        barang.setNamaBarang(rs.getString("nama_barang"));
        barang.setKategori(rs.getString("kategori"));
        barang.setSatuan(rs.getString("satuan"));
        barang.setStokSaatIni(rs.getInt("stok_saat_ini"));
        barang.setHargaBeli(rs.getBigDecimal("harga_beli"));
        barang.setHargaJual(rs.getBigDecimal("harga_jual"));
        barang.setCreatedAt(rs.getTimestamp("created_at"));
        barang.setUpdatedAt(rs.getTimestamp("updated_at"));
        return barang;
    }

    // Metode ini akan digunakan oleh TransaksiDAO
    // Pastikan visibility-nya sesuai (default/package-private atau protected)
    // atau public jika TransaksiDAO ada di paket berbeda dan memerlukan akses langsung.
    // Untuk saat ini, kita asumsikan TransaksiDAO ada di paket yang sama atau akan
    // memanggil metode ini dengan cara yang sesuai.
    protected boolean updateStock(int barangId, int quantityChange, Connection conn) throws SQLException {
        String sqlCheckStock = "SELECT stok_saat_ini FROM items WHERE id = ?";
        String sqlUpdate = "UPDATE items SET stok_saat_ini = stok_saat_ini + ? WHERE id = ?";

        if (quantityChange < 0) { // Barang keluar, cek stok
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheckStock)) {
                pstmtCheck.setInt(1, barangId);
                try(ResultSet rs = pstmtCheck.executeQuery()) {
                    if (rs.next()) {
                        int currentStock = rs.getInt("stok_saat_ini");
                        if (currentStock + quantityChange < 0) {
                            throw new SQLException("Stok barang tidak mencukupi. Stok saat ini: " + currentStock + ", diminta keluar: " + (-quantityChange));
                        }
                    } else {
                        throw new SQLException("Barang dengan ID " + barangId + " tidak ditemukan untuk pengecekan stok.");
                    }
                }
            }
        }

        try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
            pstmtUpdate.setInt(1, quantityChange);
            pstmtUpdate.setInt(2, barangId);
            int affectedRows = pstmtUpdate.executeUpdate();
            return affectedRows > 0;
        }
    }
}