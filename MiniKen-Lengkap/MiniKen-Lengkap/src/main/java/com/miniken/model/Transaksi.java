package com.miniken.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Transaksi {

    private int id;
    private String noTransaksi;
    private LocalDateTime waktu;
    private String kasir;
    private long total;
    private long bayar;
    private long kembali;
    private List<DetailTransaksi> items = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNoTransaksi() { return noTransaksi; }
    public void setNoTransaksi(String noTransaksi) { this.noTransaksi = noTransaksi; }
    public LocalDateTime getWaktu() { return waktu; }
    public void setWaktu(LocalDateTime waktu) { this.waktu = waktu; }
    public String getKasir() { return kasir; }
    public void setKasir(String kasir) { this.kasir = kasir; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public long getBayar() { return bayar; }
    public void setBayar(long bayar) { this.bayar = bayar; }
    public long getKembali() { return kembali; }
    public void setKembali(long kembali) { this.kembali = kembali; }
    public List<DetailTransaksi> getItems() { return items; }
    public void setItems(List<DetailTransaksi> items) { this.items = items; }
}
