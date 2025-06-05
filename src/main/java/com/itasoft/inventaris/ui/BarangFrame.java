package com.itasoft.inventaris.ui;

import com.itasoft.inventaris.dao.BarangDAO;
import com.itasoft.inventaris.model.Barang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
// import javax.swing.table.TableRowSorter; // Jika ingin sorting dengan klik header
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

public class BarangFrame extends JFrame {
    private BarangDAO barangDAO;
    private JTable barangTable;
    private DefaultTableModel tableModel;
    // private TableRowSorter<DefaultTableModel> sorter; // Jika ingin sorting

    // Komponen Form CRUD
    private JTextField txtId;
    private JTextField txtKodeBarang;
    private JTextField txtNamaBarang;
    private JTextField txtKategori;
    private JTextField txtSatuan;
    private JTextField txtStok; // Untuk input stok awal / display
    private JTextField txtHargaBeli;
    private JTextField txtHargaJual;

    private JButton btnTambah;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    // Komponen Pencarian dan Filter
    private JTextField txtSearch;
    private JButton btnSearch; // Atau bisa dihapus jika pencarian otomatis/on-enter
    private JComboBox<String> cmbCategoryFilter;
    private JButton btnReset;


    public BarangFrame(JFrame parent) {
        barangDAO = new BarangDAO();

        setTitle("Manajemen Data Barang");
        setSize(1100, 700);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Penting agar tidak exit aplikasi utama

        // --- Panel Filter dan Pencarian ---
        JPanel filterSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterSearchPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        filterSearchPanel.add(new JLabel("Cari Barang:"));
        txtSearch = new JTextField(25);
        filterSearchPanel.add(txtSearch);

        // btnSearch = new JButton("Cari"); // Tombol cari bisa opsional jika ada on-enter
        // filterSearchPanel.add(btnSearch);

        filterSearchPanel.add(new JLabel("   Filter Kategori:"));
        cmbCategoryFilter = new JComboBox<>();
        cmbCategoryFilter.setPreferredSize(new Dimension(200, txtSearch.getPreferredSize().height));
        populateCategoryFilter();
        filterSearchPanel.add(cmbCategoryFilter);

        btnReset = new JButton("Reset");
        filterSearchPanel.add(btnReset);


        // --- Panel Form CRUD ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Data Barang"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST; // Label rata kiri
        txtId = new JTextField(5);

        int y = 0;
        gbc.gridx = 0;
        gbc.gridy = y;
        formPanel.add(new JLabel("Kode Barang:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtKodeBarang = new JTextField(20);
        formPanel.add(txtKodeBarang, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Nama Barang:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNamaBarang = new JTextField(20);
        formPanel.add(txtNamaBarang, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtKategori = new JTextField(20);
        formPanel.add(txtKategori, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Satuan:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtSatuan = new JTextField(20);
        formPanel.add(txtSatuan, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Stok Awal:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtStok = new JTextField(20);
        txtStok.setText("0");
        formPanel.add(txtStok, gbc);
        // txtStok.setToolTipText("Stok hanya bisa diubah melalui transaksi barang masuk/keluar setelah barang dibuat.");
        // txtStok.setEditable(false); // Nonaktifkan jika stok hanya diupdate via transaksi

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Harga Beli:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtHargaBeli = new JTextField(20);
        formPanel.add(txtHargaBeli, gbc);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Harga Jual:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = y++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtHargaJual = new JTextField(20);
        formPanel.add(txtHargaJual, gbc);

        // Panel Tombol Form
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnTambah = new JButton("Tambah");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Hapus");
        btnClear = new JButton("Clear Form");
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        // --- Tabel Data Barang ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Kode", "Nama Barang", "Kategori", "Satuan", "Stok", "Harga Beli", "Harga Jual"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        barangTable = new JTable(tableModel);
        JScrollPane scrollPaneTable = new JScrollPane(barangTable);
        // Sembunyikan kolom ID dari tampilan
        barangTable.getColumnModel().getColumn(0).setMinWidth(0);
        barangTable.getColumnModel().getColumn(0).setMaxWidth(0);
        barangTable.getColumnModel().getColumn(0).setWidth(0);

        // --- Layout Utama Frame ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(formPanel), scrollPaneTable);
        splitPane.setDividerLocation(390);

        setLayout(new BorderLayout(0, 5)); // Jarak vertikal antar komponen BorderLayout
        add(filterSearchPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        loadBarangToTable(); // Muat semua data awal

        btnTambah.addActionListener(e -> addBarangAction());
        btnUpdate.addActionListener(e -> updateBarangAction());
        btnDelete.addActionListener(e -> deleteBarangAction());
        btnClear.addActionListener(e -> clearFormAction());

        // if (btnSearch != null) { // Jika tombol Cari eksplisit ada
        //     btnSearch.addActionListener(e -> loadBarangToTable());
        // }
        btnReset.addActionListener(e -> resetFilterSearchAction());

        cmbCategoryFilter.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadBarangToTable();
            }
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadBarangToTable();
                }
            }
        });

        barangTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = barangTable.getSelectedRow();
                if (selectedRow >= 0) {
                    // Jika menggunakan sorter, konversi index view ke model
                    // int modelRow = barangTable.convertRowIndexToModel(selectedRow);
                    // populateFormFromTable(modelRow);
                    populateFormFromTable(selectedRow); // Langsung jika tidak ada sorter kompleks
                }
            }
        });
    }

    private void populateCategoryFilter() {
        // Simpan item yang sedang dipilih (jika ada)
        Object selectedItem = cmbCategoryFilter.getSelectedItem();

        cmbCategoryFilter.removeAllItems();
        cmbCategoryFilter.addItem("Semua Kategori");
        List<String> categories = barangDAO.getUniqueCategories();
        for (String category : categories) {
            cmbCategoryFilter.addItem(category);
        }
        // Kembalikan pilihan sebelumnya jika masih ada di list baru
        if (selectedItem != null) {
            cmbCategoryFilter.setSelectedItem(selectedItem);
        }
    }

    private void loadBarangToTable() {
        String searchTerm = txtSearch.getText();
        String selectedCategory = null;
        if (cmbCategoryFilter.getSelectedItem() != null) {
            selectedCategory = cmbCategoryFilter.getSelectedItem().toString();
        }

        tableModel.setRowCount(0);
        List<Barang> barangs = barangDAO.getBarangFiltered(searchTerm, selectedCategory);
        for (Barang barang : barangs) {
            Vector<Object> row = new Vector<>();
            row.add(barang.getId());
            row.add(barang.getKodeBarang());
            row.add(barang.getNamaBarang());
            row.add(barang.getKategori());
            row.add(barang.getSatuan());
            row.add(barang.getStokSaatIni());
            row.add(barang.getHargaBeli() != null ? barang.getHargaBeli().toPlainString() : "0.00");
            row.add(barang.getHargaJual() != null ? barang.getHargaJual().toPlainString() : "0.00");
            tableModel.addRow(row);
        }
    }

    private void populateFormFromTable(int modelRowIndex) {
        txtId.setText(tableModel.getValueAt(modelRowIndex, 0).toString());
        txtKodeBarang.setText(tableModel.getValueAt(modelRowIndex, 1).toString());
        txtNamaBarang.setText(tableModel.getValueAt(modelRowIndex, 2).toString());
        txtKategori.setText(tableModel.getValueAt(modelRowIndex, 3) != null ? tableModel.getValueAt(modelRowIndex, 3).toString() : "");
        txtSatuan.setText(tableModel.getValueAt(modelRowIndex, 4) != null ? tableModel.getValueAt(modelRowIndex, 4).toString() : "");
        txtStok.setText(tableModel.getValueAt(modelRowIndex, 5).toString());
        txtHargaBeli.setText(tableModel.getValueAt(modelRowIndex, 6).toString());
        txtHargaJual.setText(tableModel.getValueAt(modelRowIndex, 7).toString());

        txtKodeBarang.setEditable(false); // Kode barang tidak boleh diubah
        txtStok.setEditable(false); // Stok juga tidak boleh diubah dari form master (hanya dari transaksi)
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
        btnTambah.setEnabled(false);
    }

    private void resetFilterSearchAction() {
        txtSearch.setText("");
        cmbCategoryFilter.setSelectedItem("Semua Kategori");
        loadBarangToTable();
        clearFormAction();
    }

    private boolean validateInput(boolean isAdding) {
        if (txtKodeBarang.getText().trim().isEmpty() && isAdding) {
            JOptionPane.showMessageDialog(this, "Kode Barang tidak boleh kosong!", "Input Error", JOptionPane.ERROR_MESSAGE);
            txtKodeBarang.requestFocus();
            return false;
        }
        if (txtNamaBarang.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Barang tidak boleh kosong!", "Input Error", JOptionPane.ERROR_MESSAGE);
            txtNamaBarang.requestFocus();
            return false;
        }
        if (isAdding && barangDAO.getBarangByKode(txtKodeBarang.getText().trim()) != null) {
            JOptionPane.showMessageDialog(this, "Kode Barang sudah ada!", "Input Error", JOptionPane.ERROR_MESSAGE);
            txtKodeBarang.requestFocus();
            return false;
        }
        try {
            // Validasi hanya jika field tidak kosong, dan bukan hanya tanda minus (untuk angka negatif jika diizinkan)
            if (!txtStok.getText().trim().isEmpty() && !txtStok.getText().trim().equals("-"))
                Integer.parseInt(txtStok.getText().trim());
            if (!txtHargaBeli.getText().trim().isEmpty() && !txtHargaBeli.getText().trim().equals("-"))
                new BigDecimal(txtHargaBeli.getText().trim());
            if (!txtHargaJual.getText().trim().isEmpty() && !txtHargaJual.getText().trim().equals("-"))
                new BigDecimal(txtHargaJual.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Stok, Harga Beli, atau Harga Jual harus angka yang valid (atau kosong).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private Barang getBarangFromForm(boolean isAdding) {
        Barang barang = new Barang();
        if (!isAdding && !txtId.getText().isEmpty()) {
            barang.setId(Integer.parseInt(txtId.getText()));
        }
        barang.setKodeBarang(txtKodeBarang.getText().trim());
        barang.setNamaBarang(txtNamaBarang.getText().trim());
        barang.setKategori(txtKategori.getText().trim());
        barang.setSatuan(txtSatuan.getText().trim());

        // Untuk stok, saat menambah, ambil dari field. Saat update, stok tidak diubah dari form ini.
        if (isAdding) {
            barang.setStokSaatIni(txtStok.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtStok.getText().trim()));
        } else {
            // Saat update, ambil stok yang ada dari DB (sudah ada di objek barang yang diambil dari tabel)
            // atau biarkan tidak di-set jika updateBarang di DAO tidak mengubah stok.
            // Untuk konsistensi, kita tidak set stok dari form saat update.
            // Jika ingin set stok saat update dari form ini (misal untuk koreksi), maka ambil dari txtStok
            barang.setStokSaatIni(Integer.parseInt(txtStok.getText().trim())); // Jika txtStok diupdate
        }

        barang.setHargaBeli(txtHargaBeli.getText().trim().isEmpty() ? null : new BigDecimal(txtHargaBeli.getText().trim()));
        barang.setHargaJual(txtHargaJual.getText().trim().isEmpty() ? null : new BigDecimal(txtHargaJual.getText().trim()));
        return barang;
    }

    private void addBarangAction() {
        txtStok.setEditable(true); // Stok bisa diisi saat barang baru pertama kali ditambah
        if (!validateInput(true)) return;
        Barang barang = getBarangFromForm(true);
        if (barangDAO.addBarang(barang)) {
            JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan!");
            loadBarangToTable();
            populateCategoryFilter();
            clearFormAction();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan barang.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBarangAction() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih barang dari tabel untuk diupdate.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        txtStok.setEditable(false); // Stok tidak diedit dari sini saat update
        if (!validateInput(false)) return;

        Barang barang = getBarangFromForm(false);
        // Ambil stok asli dari tabel karena tidak diedit di form ini
        int selectedRow = barangTable.getSelectedRow();
        // int modelRow = barangTable.convertRowIndexToModel(selectedRow);
        // barang.setStokSaatIni(Integer.parseInt(tableModel.getValueAt(modelRow, 5).toString()));
        barang.setStokSaatIni(Integer.parseInt(tableModel.getValueAt(selectedRow, 5).toString()));


        if (barangDAO.updateBarang(barang)) {
            JOptionPane.showMessageDialog(this, "Barang berhasil diupdate!");
            loadBarangToTable();
            populateCategoryFilter();
            clearFormAction();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate barang.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBarangAction() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih barang dari tabel untuk dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus barang '" + txtNamaBarang.getText() + "'?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (barangDAO.deleteBarang(Integer.parseInt(txtId.getText()))) {
                JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!");
                loadBarangToTable();
                populateCategoryFilter();
                clearFormAction();
            }
        }
    }

    private void clearFormAction() {
        txtId.setText("");
        txtKodeBarang.setText("");
        txtNamaBarang.setText("");
        txtKategori.setText("");
        txtSatuan.setText("");
        txtStok.setText("0");
        txtHargaBeli.setText("");
        txtHargaJual.setText("");

        txtKodeBarang.setEditable(true);
        txtStok.setEditable(true); // Kembalikan editable untuk penambahan baru
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnTambah.setEnabled(true);
        barangTable.clearSelection();
        txtKodeBarang.requestFocus();
    }
}