package com.miniken.util;

import com.miniken.model.DetailTransaksi;
import com.miniken.model.Transaksi;
import java.time.format.DateTimeFormatter;

/** Menyusun teks struk lebar 32 karakter (huruf monospace). */
public final class StrukBuilder {

    private static final int LEBAR = 32;
    private static final DateTimeFormatter WAKTU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private StrukBuilder() {
    }

    public static String buat(Transaksi t) {
        StringBuilder sb = new StringBuilder();
        sb.append(tengah("MINIKEN")).append('\n');
        sb.append(tengah("Belanja Mudah, Hemat Setiap Hari")).append('\n');
        sb.append(garis());
        sb.append(kiriKanan("No", t.getNoTransaksi()));
        sb.append(kiriKanan("Waktu", t.getWaktu().format(WAKTU)));
        if (t.getKasir() != null && !t.getKasir().isEmpty()) {
            sb.append(kiriKanan("Kasir", t.getKasir()));
        }
        sb.append(garis());
        for (DetailTransaksi d : t.getItems()) {
            sb.append(potong(d.getNamaProduk(), LEBAR)).append('\n');
            sb.append(kiriKanan("  " + d.getQty() + " x " + Rupiah.angka(d.getHarga()),
                    Rupiah.angka(d.getSubtotal())));
        }
        sb.append(garis());
        sb.append(kiriKanan("TOTAL", Rupiah.angka(t.getTotal())));
        sb.append(kiriKanan("BAYAR", Rupiah.angka(t.getBayar())));
        sb.append(kiriKanan("KEMBALI", Rupiah.angka(t.getKembali())));
        sb.append(garis());
        sb.append(tengah("Terima kasih telah berbelanja!")).append('\n');
        return sb.toString();
    }

    private static String garis() {
        return "-".repeat(LEBAR) + "\n";
    }

    private static String tengah(String teks) {
        String t = potong(teks, LEBAR);
        int spasi = (LEBAR - t.length()) / 2;
        return " ".repeat(Math.max(0, spasi)) + t;
    }

    private static String kiriKanan(String kiri, String kanan) {
        int spasi = LEBAR - kiri.length() - kanan.length();
        if (spasi < 1) {
            kiri = potong(kiri, Math.max(1, LEBAR - kanan.length() - 1));
            spasi = Math.max(1, LEBAR - kiri.length() - kanan.length());
        }
        return kiri + " ".repeat(spasi) + kanan + "\n";
    }

    private static String potong(String teks, int maks) {
        return teks.length() <= maks ? teks : teks.substring(0, maks);
    }
}
