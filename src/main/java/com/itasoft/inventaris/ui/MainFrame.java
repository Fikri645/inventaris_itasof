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
    private JButton btnEksporLaporan; // Nama tombol digeneralisasi
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

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("User: " + currentUser.getNamaLengkap() + " (" + currentUser.getRole() + ")");
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Selamat Datang di Sistem Inventaris!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        mainPanel.add(welcomePanel, BorderLayout.NORTH);

        JPanel menuButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.ipadx = 20;
        gbc.ipady = 30;
        Font buttonFont = new Font("Arial", Font.BOLD, 15);

        btnManajemenBarang = new JButton("Manajemen Barang");
        btnManajemenBarang.setFont(buttonFont);
        gbc.gridx = 0; gbc.gridy = 0; menuButtonPanel.add(btnManajemenBarang, gbc);

        btnTransaksiMasuk = new JButton("Transaksi Barang Masuk");
        btnTransaksiMasuk.setFont(buttonFont);
        gbc.gridx = 1; gbc.gridy = 0; menuButtonPanel.add(btnTransaksiMasuk, gbc);

        btnTransaksiKeluar = new JButton("Transaksi Barang Keluar");
        btnTransaksiKeluar.setFont(buttonFont);
        gbc.gridx = 0; gbc.gridy = 1; menuButtonPanel.add(btnTransaksiKeluar, gbc);

        btnEksporLaporan = new JButton("Ekspor Laporan"); // Teks tombol diubah
        btnEksporLaporan.setFont(buttonFont);
        gbc.gridx = 1; gbc.gridy = 1; menuButtonPanel.add(btnEksporLaporan, gbc);

        btnLogout = new JButton("Logout");
        btnLogout.setFont(buttonFont);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        menuButtonPanel.add(btnLogout, gbc);

        mainPanel.add(menuButtonPanel, BorderLayout.CENTER);

        if (currentUser != null && currentUser.getRole() != null) {
            if (!"admin".equalsIgnoreCase(currentUser.getRole())) {
                btnManajemenBarang.setEnabled(false);
            }
        } else {
            btnManajemenBarang.setEnabled(false);
        }

        btnManajemenBarang.addActionListener(e -> openManajemenBarang());
        btnTransaksiMasuk.addActionListener(e -> openTransaksi("MASUK"));
        btnTransaksiKeluar.addActionListener(e -> openTransaksi("KELUAR"));
        btnEksporLaporan.addActionListener(e -> showExportOptionsDialog());
        btnLogout.addActionListener(e -> logout());
    }

    private void openManajemenBarang() {
        BarangFrame barangFrame = new BarangFrame(this);
        barangFrame.setVisible(true);
    }

    private void openTransaksi(String tipeTransaksi) {
        TransaksiFrame transaksiFrame = new TransaksiFrame(this, tipeTransaksi, currentUser);
        transaksiFrame.setVisible(true);
    }

    private void showExportOptionsDialog() {
        // Pilihan diperbanyak
        String[] options = {
                "Laporan Stok (CSV)",
                "Laporan Stok (Excel)",
                "Laporan Transaksi (CSV)",
                "Laporan Transaksi (Excel)",
                "Batal"
        };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Pilih jenis laporan dan format ekspor:",
                "Pilih Format Ekspor Laporan",
                JOptionPane.DEFAULT_OPTION, // Menggunakan DEFAULT_OPTION agar tidak ada ikon spesifik yes/no/cancel
                JOptionPane.PLAIN_MESSAGE,  // Menggunakan PLAIN_MESSAGE agar tidak ada ikon question/info/warning
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0: // Laporan Stok (CSV)
                ReportGenerator.generateStockReportCSV(this);
                break;
            case 1: // Laporan Stok (Excel)
                ReportGenerator.generateStockReportExcel(this);
                break;
            case 2: // Laporan Transaksi (CSV)
                ReportGenerator.generateTransactionReportCSV(this);
                break;
            case 3: // Laporan Transaksi (Excel)
                ReportGenerator.generateTransactionReportExcel(this);
                break;
            // case 4 atau JOptionPane.CLOSED_OPTION: // Batal atau menutup dialog
            default:
                // Tidak melakukan apa-apa
                break;
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