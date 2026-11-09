package com.miniken.model;

public class Produk {

    public static final int BATAS_STOK_MENIPIS = 5;

    private int id;
    private String kode;
    private String nama;
    private String kategori;
    private long harga;
    private int stok;

    public boolean isStokMenipis() {
        return stok <= BATAS_STOK_MENIPIS;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }
    public long getHarga() { return harga; }
    public void setHarga(long harga) { this.harga = harga; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
}
