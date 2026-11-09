package com.miniken.model;

public class DetailTransaksi {

    private int id;
    private String kodeProduk;
    private String namaProduk;
    private long harga;
    private int qty;
    private long subtotal;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getKodeProduk() { return kodeProduk; }
    public void setKodeProduk(String kodeProduk) { this.kodeProduk = kodeProduk; }
    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }
    public long getHarga() { return harga; }
    public void setHarga(long harga) { this.harga = harga; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public long getSubtotal() { return subtotal; }
    public void setSubtotal(long subtotal) { this.subtotal = subtotal; }
}
