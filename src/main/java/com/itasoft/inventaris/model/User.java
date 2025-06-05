package com.itasoft.inventaris.model;

public class User {
    private int id;
    private String username;
    private String password; // Sebaiknya tidak menyimpan password plain text di model setelah validasi
    private String namaLengkap;
    private String role;
    // private java.sql.Timestamp createdAt; // Jika ada di tabel users

    public User() {
    }

    public User(int id, String username, String namaLengkap, String role) {
        this.id = id;
        this.username = username;
        this.namaLengkap = namaLengkap;
        this.role = role;
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // public java.sql.Timestamp getCreatedAt() {
    //     return createdAt;
    // }

    // public void setCreatedAt(java.sql.Timestamp createdAt) {
    //     this.createdAt = createdAt;
    // }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", namaLengkap='" + namaLengkap + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}