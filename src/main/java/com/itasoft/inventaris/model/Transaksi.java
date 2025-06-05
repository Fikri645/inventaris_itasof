package com.itasoft.inventaris.model;

import java.sql.Timestamp; // atau java.time.LocalDateTime jika Anda mengonversi

public class Transaksi {
    private int id;
    private int itemId; // atau barangId
    private int userId;
    private String tipeTransaksi; // "MASUK" atau "KELUAR"
    private int jumlah;
    private Timestamp tanggalTransaksi;
    private String keterangan;

    // Opsional: untuk menampilkan nama barang dan user di tabel laporan transaksi
    private String namaBarang;
    private String usernamePelaku;


    public Transaksi() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTipeTransaksi() {
        return tipeTransaksi;
    }

    public void setTipeTransaksi(String tipeTransaksi) {
        this.tipeTransaksi = tipeTransaksi;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public Timestamp getTanggalTransaksi() {
        return tanggalTransaksi;
    }

    public void setTanggalTransaksi(Timestamp tanggalTransaksi) {
        this.tanggalTransaksi = tanggalTransaksi;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getUsernamePelaku() {
        return usernamePelaku;
    }

    public void setUsernamePelaku(String usernamePelaku) {
        this.usernamePelaku = usernamePelaku;
    }
}