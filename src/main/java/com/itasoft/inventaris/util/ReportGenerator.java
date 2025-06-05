package com.itasoft.inventaris.util;

import com.itasoft.inventaris.model.Barang;
import com.itasoft.inventaris.dao.BarangDAO;
import com.itasoft.inventaris.model.Transaksi; // <-- Tambahkan import
import com.itasoft.inventaris.dao.TransaksiDAO; // <-- Tambahkan import

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportGenerator {

    // --- Metode Laporan Stok (CSV dan Excel sudah ada) ---
    public static void generateStockReportCSV(JFrame parentFrame) {
        // ... (kode yang sudah ada, tidak perlu diubah) ...
        BarangDAO barangDAO = new BarangDAO();
        List<Barang> barangs = barangDAO.getAllBarang();

        if (barangs.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Tidak ada data barang untuk dilaporkan.", "Laporan Stok (CSV)", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Stok Barang (CSV)");
        SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFilename = "LaporanStok_" + dateFormatFile.format(new Date()) + ".csv";
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));
        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                fileToSave = new java.io.File(filePath + ".csv");
            }
            try (FileWriter csvWriter = new FileWriter(fileToSave)) {
                csvWriter.append("ID Barang,Kode Barang,Nama Barang,Kategori,Satuan,Stok Saat Ini,Harga Beli,Harga Jual,Dibuat Pada,Diupdate Pada\n");
                SimpleDateFormat dateFormatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Barang barang : barangs) {
                    csvWriter.append(String.valueOf(barang.getId())).append(",");
                    csvWriter.append("\"").append(escapeCsv(barang.getKodeBarang())).append("\"").append(",");
                    csvWriter.append("\"").append(escapeCsv(barang.getNamaBarang())).append("\"").append(",");
                    csvWriter.append("\"").append(escapeCsv(barang.getKategori())).append("\"").append(",");
                    csvWriter.append("\"").append(escapeCsv(barang.getSatuan())).append("\"").append(",");
                    csvWriter.append(String.valueOf(barang.getStokSaatIni())).append(",");
                    csvWriter.append(barang.getHargaBeli() != null ? barang.getHargaBeli().toPlainString() : "0.00").append(",");
                    csvWriter.append(barang.getHargaJual() != null ? barang.getHargaJual().toPlainString() : "0.00").append(",");
                    csvWriter.append(barang.getCreatedAt() != null ? dateFormatData.format(barang.getCreatedAt()) : "").append(",");
                    csvWriter.append(barang.getUpdatedAt() != null ? dateFormatData.format(barang.getUpdatedAt()) : "").append("\n");
                }
                csvWriter.flush();
                JOptionPane.showMessageDialog(parentFrame, "Laporan Stok (CSV) berhasil disimpan di:\n" + fileToSave.getAbsolutePath(), "Laporan Berhasil Disimpan", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                System.err.println("Error saat generate laporan CSV: " + e.getMessage());
                JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan laporan CSV: " + e.getMessage(), "Error Laporan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void generateStockReportExcel(JFrame parentFrame) {
        // ... (kode yang sudah ada, tidak perlu diubah) ...
        BarangDAO barangDAO = new BarangDAO();
        List<Barang> barangs = barangDAO.getAllBarang();
        if (barangs.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Tidak ada data barang untuk dilaporkan.", "Laporan Stok (Excel)", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Stok Barang (Excel)");
        SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFilename = "LaporanStok_" + dateFormatFile.format(new Date()) + ".xlsx";
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                fileToSave = new java.io.File(filePath + ".xlsx");
            }
            try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(fileToSave)) {
                XSSFSheet sheet = workbook.createSheet("Data Stok Barang");
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 12);
                headerStyle.setFont(headerFont);
                String[] headers = {"ID Barang", "Kode Barang", "Nama Barang", "Kategori", "Satuan", "Stok Saat Ini", "Harga Beli", "Harga Jual", "Dibuat Pada", "Diupdate Pada"};
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }
                int rowNum = 1;
                SimpleDateFormat dateFormatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Barang barang : barangs) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(barang.getId());
                    row.createCell(1).setCellValue(barang.getKodeBarang());
                    row.createCell(2).setCellValue(barang.getNamaBarang());
                    row.createCell(3).setCellValue(barang.getKategori());
                    row.createCell(4).setCellValue(barang.getSatuan());
                    row.createCell(5).setCellValue(barang.getStokSaatIni());
                    row.createCell(6).setCellValue(barang.getHargaBeli() != null ? barang.getHargaBeli().doubleValue() : 0.00);
                    row.createCell(7).setCellValue(barang.getHargaJual() != null ? barang.getHargaJual().doubleValue() : 0.00);
                    row.createCell(8).setCellValue(barang.getCreatedAt() != null ? dateFormatData.format(barang.getCreatedAt()) : "");
                    row.createCell(9).setCellValue(barang.getUpdatedAt() != null ? dateFormatData.format(barang.getUpdatedAt()) : "");
                }
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }
                workbook.write(fos);
                JOptionPane.showMessageDialog(parentFrame, "Laporan Stok (Excel) berhasil disimpan di:\n" + fileToSave.getAbsolutePath(), "Laporan Berhasil Disimpan", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                System.err.println("Error saat generate laporan Excel: " + e.getMessage());
                JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan laporan Excel: " + e.getMessage(), "Error Laporan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private static String escapeCsv(String data) { // Helper CSV
        if (data == null) return "";
        String escapedData = data.replaceAll("\\R", " ");
        if (escapedData.contains(",") || escapedData.contains("\"") || escapedData.contains("'")) {
            escapedData = escapedData.replace("\"", "\"\"");
            return "\"" + escapedData + "\"";
        }
        return escapedData;
    }

    // --- Metode Baru untuk Laporan Transaksi (CSV) ---
    public static void generateTransactionReportCSV(JFrame parentFrame) {
        TransaksiDAO transaksiDAO = new TransaksiDAO();
        List<Transaksi> transaksis = transaksiDAO.getAllTransaksi();

        if (transaksis.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Tidak ada data transaksi untuk dilaporkan.", "Laporan Transaksi (CSV)", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Transaksi (CSV)");
        SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFilename = "LaporanTransaksi_" + dateFormatFile.format(new Date()) + ".csv";
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));

        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                fileToSave = new java.io.File(filePath + ".csv");
            }

            try (FileWriter csvWriter = new FileWriter(fileToSave)) {
                // Header CSV
                csvWriter.append("ID Transaksi,Tanggal,Kode Barang,Nama Barang,Tipe,Jumlah,User Pelaku,Keterangan\n");
                SimpleDateFormat dateFormatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                for (Transaksi trx : transaksis) {
                    csvWriter.append(String.valueOf(trx.getId())).append(",");
                    csvWriter.append("\"").append(dateFormatData.format(trx.getTanggalTransaksi())).append("\"").append(",");
                    csvWriter.append("\"").append(escapeCsv(trx.getKodeBarang())).append("\"").append(",");
                    csvWriter.append("\"").append(escapeCsv(trx.getNamaBarang())).append("\"").append(",");
                    csvWriter.append("\"").append(escapeCsv(trx.getTipeTransaksi())).append("\"").append(",");
                    csvWriter.append(String.valueOf(trx.getJumlah())).append(",");
                    csvWriter.append("\"").append(escapeCsv(trx.getUsernamePelaku())).append("\"").append(",");
                    csvWriter.append("\"").append(escapeCsv(trx.getKeterangan())).append("\"").append("\n");
                }
                csvWriter.flush();
                JOptionPane.showMessageDialog(parentFrame, "Laporan Transaksi (CSV) berhasil disimpan di:\n" + fileToSave.getAbsolutePath(), "Laporan Berhasil Disimpan", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                System.err.println("Error saat generate laporan transaksi CSV: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan laporan transaksi CSV: " + e.getMessage(), "Error Laporan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Metode Baru untuk Laporan Transaksi (Excel) ---
    public static void generateTransactionReportExcel(JFrame parentFrame) {
        TransaksiDAO transaksiDAO = new TransaksiDAO();
        List<Transaksi> transaksis = transaksiDAO.getAllTransaksi();

        if (transaksis.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Tidak ada data transaksi untuk dilaporkan.", "Laporan Transaksi (Excel)", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Transaksi (Excel)");
        SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFilename = "LaporanTransaksi_" + dateFormatFile.format(new Date()) + ".xlsx";
        fileChooser.setSelectedFile(new java.io.File(defaultFilename));

        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                fileToSave = new java.io.File(filePath + ".xlsx");
            }

            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(fileToSave)) {

                XSSFSheet sheet = workbook.createSheet("Data Transaksi");

                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 12);
                headerStyle.setFont(headerFont);

                String[] headers = {"ID Transaksi", "Tanggal", "Kode Barang", "Nama Barang", "Tipe", "Jumlah", "User Pelaku", "Keterangan"};
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowNum = 1;
                SimpleDateFormat dateFormatData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Transaksi trx : transaksis) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(trx.getId());
                    row.createCell(1).setCellValue(dateFormatData.format(trx.getTanggalTransaksi()));
                    row.createCell(2).setCellValue(trx.getKodeBarang());
                    row.createCell(3).setCellValue(trx.getNamaBarang());
                    row.createCell(4).setCellValue(trx.getTipeTransaksi());
                    row.createCell(5).setCellValue(trx.getJumlah());
                    row.createCell(6).setCellValue(trx.getUsernamePelaku());
                    row.createCell(7).setCellValue(trx.getKeterangan());
                }

                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(fos);
                JOptionPane.showMessageDialog(parentFrame, "Laporan Transaksi (Excel) berhasil disimpan di:\n" + fileToSave.getAbsolutePath(), "Laporan Berhasil Disimpan", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                System.err.println("Error saat generate laporan transaksi Excel: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan laporan transaksi Excel: " + e.getMessage(), "Error Laporan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}