package com.itasoft.inventaris.util;

import com.itasoft.inventaris.model.Barang;
import com.itasoft.inventaris.dao.BarangDAO;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.FileWriter; // Masih dibutuhkan untuk metode CSV
import java.io.IOException;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

// Import untuk Apache POI
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportGenerator {

    // --- Metode Ekspor CSV yang sudah ada (bisa dipertahankan atau dihapus jika hanya mau Excel) ---
    public static void generateStockReportCSV(JFrame parentFrame) {
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
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan laporan CSV: " + e.getMessage(), "Error Laporan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String escapeCsv(String data) {
        if (data == null) return "";
        String escapedData = data.replaceAll("\\R", " ");
        if (escapedData.contains(",") || escapedData.contains("\"") || escapedData.contains("'")) {
            escapedData = escapedData.replace("\"", "\"\"");
            return "\"" + escapedData + "\"";
        }
        return escapedData;
    }

    // --- Metode Baru untuk Ekspor Excel ---
    public static void generateStockReportExcel(JFrame parentFrame) {
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

            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(fileToSave)) {

                XSSFSheet sheet = workbook.createSheet("Data Stok Barang");

                // Style untuk Header
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 12);
                headerStyle.setFont(headerFont);

                // Membuat Header Row
                String[] headers = {"ID Barang", "Kode Barang", "Nama Barang", "Kategori", "Satuan",
                        "Stok Saat Ini", "Harga Beli", "Harga Jual", "Dibuat Pada", "Diupdate Pada"};
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Mengisi Data Barang
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

                // Auto-size kolom agar pas dengan konten (bisa memakan waktu jika data banyak)
                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(fos);
                JOptionPane.showMessageDialog(parentFrame, "Laporan Stok (Excel) berhasil disimpan di:\n" + fileToSave.getAbsolutePath(), "Laporan Berhasil Disimpan", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                System.err.println("Error saat generate laporan Excel: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "Gagal menyimpan laporan Excel: " + e.getMessage(), "Error Laporan", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}