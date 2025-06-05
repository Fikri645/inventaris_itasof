package com.itasoft.inventaris.model;

import java.sql.Timestamp;

public class Transaksi {
    private int id;
    private int itemId;
    private String kodeBarang; // <-- Tambahkan ini
    private String namaBarang;
    private int userId;
    private String usernamePelaku;
    private String tipeTransaksi; // "MASUK" atau "KELUAR"
    private int jumlah;
    private Timestamp tanggalTransaksi;
    private String keterangan;

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

    public String getKodeBarang() { // <-- Tambahkan ini
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) { // <-- Tambahkan ini
        this.kodeBarang = kodeBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsernamePelaku() {
        return usernamePelaku;
    }

    public void setUsernamePelaku(String usernamePelaku) {
        this.usernamePelaku = usernamePelaku;
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
}