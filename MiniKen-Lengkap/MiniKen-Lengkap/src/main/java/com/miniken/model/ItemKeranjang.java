package com.miniken.model;

public class ItemKeranjang {

    private final int produkId;
    private final String kode;
    private final String nama;
    private final long harga;
    private int qty;

    public ItemKeranjang(Produk p, int qty) {
        this.produkId = p.getId();
        this.kode = p.getKode();
        this.nama = p.getNama();
        this.harga = p.getHarga();
        this.qty = qty;
    }

    public long getSubtotal() {
        return harga * qty;
    }

    public int getProdukId() { return produkId; }
    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public long getHarga() { return harga; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}
