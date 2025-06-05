package com.itasoft.inventaris.ui;

import com.itasoft.inventaris.model.User;
import com.itasoft.inventaris.util.ReportGenerator;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;
    private JLabel statusLabel;

    private JButton btnManajemenBarang;
    private JButton btnTransaksiMasuk;
    private JButton btnTransaksiKeluar;
    private JButton btnEksporLaporanStok; // Tombol laporan tunggal
    private JButton btnLogout;

    public MainFrame(User user) {
        this.currentUser = user;

        setTitle("Sistem Inventaris Sederhana - Menu Utama");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // --- Status Bar (SOUTH) ---
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("User: " + currentUser.getNamaLengkap() + " (" + currentUser.getRole() + ")");
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // --- Welcome Label (NORTH) ---
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Selamat Datang di Sistem Inventaris!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        mainPanel.add(welcomePanel, BorderLayout.NORTH);

        // --- Menu Buttons (CENTER menggunakan GridBagLayout) ---
        JPanel menuButtonPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.ipadx = 20;
        gbc.ipady = 30;

        Font buttonFont = new Font("Arial", Font.BOLD, 15);

        // Tombol Baris 1
        btnManajemenBarang = new JButton("Manajemen Barang");
        btnManajemenBarang.setFont(buttonFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        menuButtonPanel.add(btnManajemenBarang, gbc);

        btnTransaksiMasuk = new JButton("Transaksi Barang Masuk");
        btnTransaksiMasuk.setFont(buttonFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        menuButtonPanel.add(btnTransaksiMasuk, gbc);

        // Tombol Baris 2
        btnTransaksiKeluar = new JButton("Transaksi Barang Keluar");
        btnTransaksiKeluar.setFont(buttonFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        menuButtonPanel.add(btnTransaksiKeluar, gbc);

        btnEksporLaporanStok = new JButton("Ekspor Laporan Stok");
        btnEksporLaporanStok.setFont(buttonFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        menuButtonPanel.add(btnEksporLaporanStok, gbc);

        // Tombol Baris 3 (Logout)
        btnLogout = new JButton("Logout");
        btnLogout.setFont(buttonFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        menuButtonPanel.add(btnLogout, gbc);

        mainPanel.add(menuButtonPanel, BorderLayout.CENTER);

        // --- Logika Hak Akses Tombol ---
        // Cek role pengguna dan atur enabled/disabled tombol Manajemen Barang
        if (currentUser != null && currentUser.getRole() != null) {
            if ("admin".equalsIgnoreCase(currentUser.getRole())) {
                btnManajemenBarang.setEnabled(true);
            } else if ("staff".equalsIgnoreCase(currentUser.getRole())) {
                btnManajemenBarang.setEnabled(false);
            } else {
                // Default jika role tidak dikenali (misalnya, nonaktifkan untuk keamanan)
                btnManajemenBarang.setEnabled(false);
            }
        } else {
            // Jika user atau role null, nonaktifkan sebagai tindakan pencegahan
            btnManajemenBarang.setEnabled(false);
        }


        // Action Listeners
        btnManajemenBarang.addActionListener(e -> openManajemenBarang());
        btnTransaksiMasuk.addActionListener(e -> openTransaksi("MASUK"));
        btnTransaksiKeluar.addActionListener(e -> openTransaksi("KELUAR"));
        btnEksporLaporanStok.addActionListener(e -> showExportOptionsDialog());
        btnLogout.addActionListener(e -> logout());
    }

    private void openManajemenBarang() {
        // Tambahan: Cek lagi di sini jika tombol bisa di-enable/disable secara dinamis di tempat lain
        // Namun, karena sudah diatur saat inisialisasi frame, ini mungkin tidak perlu.
        // if (!btnManajemenBarang.isEnabled()) {
        //     JOptionPane.showMessageDialog(this, "Anda tidak memiliki hak akses untuk fitur ini.", "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
        //     return;
        // }
        BarangFrame barangFrame = new BarangFrame(this);
        barangFrame.setVisible(true);
    }

    private void openTransaksi(String tipeTransaksi) {
        TransaksiFrame transaksiFrame = new TransaksiFrame(this, tipeTransaksi, currentUser);
        transaksiFrame.setVisible(true);
    }

    private void showExportOptionsDialog() {
        String[] options = {"Export ke CSV", "Export ke Excel", "Batal"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Pilih format ekspor untuk laporan stok:",
                "Pilih Format Ekspor",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            ReportGenerator.generateStockReportCSV(this);
        } else if (choice == 1) {
            ReportGenerator.generateStockReportExcel(this);
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}