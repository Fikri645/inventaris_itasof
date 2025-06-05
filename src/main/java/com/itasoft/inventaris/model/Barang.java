package com.itasoft.inventaris.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Barang {
    private int id;
    private String kodeBarang;
    private String namaBarang;
    private String kategori;
    private String satuan;
    private int stokSaatIni;
    private BigDecimal hargaBeli;
    private BigDecimal hargaJual;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Barang() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public int getStokSaatIni() {
        return stokSaatIni;
    }

    public void setStokSaatIni(int stokSaatIni) {
        this.stokSaatIni = stokSaatIni;
    }

    public BigDecimal getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(BigDecimal hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public BigDecimal getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(BigDecimal hargaJual) {
        this.hargaJual = hargaJual;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return namaBarang + " (" + kodeBarang + ")";
    }
}