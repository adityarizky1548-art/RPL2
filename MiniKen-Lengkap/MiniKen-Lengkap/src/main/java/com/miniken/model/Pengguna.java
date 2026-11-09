package com.miniken.model;

public class Pengguna {

    public static final String ADMIN = "ADMIN";
    public static final String KASIR = "KASIR";

    private int id;
    private String username;
    private String namaLengkap;
    private String peran = KASIR;
    private boolean aktif = true;

    public boolean isAdmin() {
        return ADMIN.equals(peran);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public String getPeran() { return peran; }
    public void setPeran(String peran) { this.peran = peran; }
    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }
}
