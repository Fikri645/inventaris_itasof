package com.itasoft.inventaris.ui;

import com.itasoft.inventaris.dao.BarangDAO;
import com.itasoft.inventaris.dao.TransaksiDAO;
import com.itasoft.inventaris.model.Barang;
import com.itasoft.inventaris.model.Transaksi;
import com.itasoft.inventaris.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

public class TransaksiFrame extends JFrame {
    private String tipeTransaksi; // "MASUK" atau "KELUAR"
    private User currentUser;
    private TransaksiDAO transaksiDAO;
    private BarangDAO barangDAO;

    private JComboBox<Barang> cmbBarang;
    private JTextField txtJumlah;
    private JTextArea txtKeterangan;
    private JButton btnSimpanTransaksi;
    private JTable historyTable; // Tabel untuk histori transaksi
    private DefaultTableModel historyTableModel;

    public TransaksiFrame(JFrame parent, String tipeTransaksi, User currentUser) {
        this.tipeTransaksi = tipeTransaksi;
        this.currentUser = currentUser;
        this.transaksiDAO = new TransaksiDAO();
        this.barangDAO = new BarangDAO();

        setTitle("Transaksi Barang " + (tipeTransaksi.equalsIgnoreCase("MASUK") ? "Masuk" : "Keluar"));
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        // Panel Form Transaksi
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y=0;
        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Pilih Barang:"), gbc);
        gbc.gridx = 1; gbc.gridy = y++; cmbBarang = new JComboBox<>(); formPanel.add(cmbBarang, gbc);
        loadBarangToComboBox();

        gbc.gridx = 0; gbc.gridy = y; formPanel.add(new JLabel("Jumlah:"), gbc);
        gbc.gridx = 1; gbc.gridy = y++; txtJumlah = new JTextField(10); formPanel.add(txtJumlah, gbc);

        gbc.gridx = 0; gbc.gridy = y; gbc.anchor = GridBagConstraints.NORTHWEST; formPanel.add(new JLabel("Keterangan:"), gbc);
        gbc.gridx = 1; gbc.gridy = y++;
        txtKeterangan = new JTextArea(3, 20);
        JScrollPane keteranganScrollPane = new JScrollPane(txtKeterangan);
        formPanel.add(keteranganScrollPane, gbc);

        gbc.gridx = 1; gbc.gridy = y; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        btnSimpanTransaksi = new JButton("Simpan Transaksi " + (tipeTransaksi.equalsIgnoreCase("MASUK") ? "Masuk" : "Keluar"));
        formPanel.add(btnSimpanTransaksi, gbc);

        // Panel Histori Transaksi
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Histori Transaksi Terkini"));
        historyTableModel = new DefaultTableModel(
                new String[]{"ID", "Tgl Transaksi", "Barang", "Tipe", "Jumlah", "Oleh", "Keterangan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyTableModel);
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        loadTransaksiHistory();


        add(formPanel, BorderLayout.NORTH);
        add(historyPanel, BorderLayout.CENTER);

        btnSimpanTransaksi.addActionListener(e -> simpanTransaksiAction());
    }

    private void loadBarangToComboBox() {
        List<Barang> barangs = barangDAO.getAllBarang();
        for (Barang barang : barangs) {
            cmbBarang.addItem(barang);
        }
    }

    private void loadTransaksiHistory() {
        historyTableModel.setRowCount(0); // Clear table
        List<Transaksi> transaksis = transaksiDAO.getAllTransaksi(); // Ambil semua transaksi
        for (Transaksi trx : transaksis) {
            Vector<Object> row = new Vector<>();
            row.add(trx.getId());
            row.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(trx.getTanggalTransaksi()));
            row.add(trx.getNamaBarang()); // Sudah di-populate di DAO
            row.add(trx.getTipeTransaksi());
            row.add(trx.getJumlah());
            row.add(trx.getUsernamePelaku()); // Sudah di-populate di DAO
            row.add(trx.getKeterangan());
            historyTableModel.addRow(row);
        }
    }


    private void simpanTransaksiAction() {
        Barang selectedBarang = (Barang) cmbBarang.getSelectedItem();
        if (selectedBarang == null) {
            JOptionPane.showMessageDialog(this, "Pilih barang terlebih dahulu.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String jumlahStr = txtJumlah.getText().trim();
        if (jumlahStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jumlah tidak boleh kosong.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Transaksi transaksi = new Transaksi();
        transaksi.setItemId(selectedBarang.getId());
        transaksi.setUserId(currentUser.getId());
        transaksi.setTipeTransaksi(this.tipeTransaksi);
        transaksi.setJumlah(jumlah);
        transaksi.setTanggalTransaksi(new Timestamp(System.currentTimeMillis()));
        transaksi.setKeterangan(txtKeterangan.getText().trim());

        if (transaksiDAO.addTransaksi(transaksi)) {
            JOptionPane.showMessageDialog(this, "Transaksi " + tipeTransaksi.toLowerCase() + " barang berhasil disimpan!");
            // Refresh ComboBox (stoknya berubah)
            // loadBarangToComboBox(); // Ini akan refresh item tapi tidak otomatis pilih yang sama
            // Alternatif: update stok barang yang dipilih di ComboBox secara manual jika perlu
            // atau cukup reload data di tabel barang jika user kembali ke sana.

            // Refresh histori transaksi
            loadTransaksiHistory();

            // Kosongkan form
            txtJumlah.setText("");
            txtKeterangan.setText("");
            cmbBarang.setSelectedIndex(0); // Atau -1 jika tidak ingin ada yang terpilih

        } else {
            // Pesan error spesifik (misal stok tidak cukup) sudah ditangani di TransaksiDAO
            // JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}