package com.miniken.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class Rupiah {

    private static final DecimalFormat FORMAT;

    static {
        DecimalFormatSymbols simbol = new DecimalFormatSymbols(new Locale("id", "ID"));
        simbol.setGroupingSeparator('.');
        FORMAT = new DecimalFormat("#,##0", simbol);
    }

    private Rupiah() {
    }

    /** 12500 menjadi "12.500". */
    public static String angka(long nilai) {
        synchronized (FORMAT) {
            return FORMAT.format(nilai);
        }
    }

    /** 12500 menjadi "Rp 12.500". */
    public static String format(long nilai) {
        return "Rp " + angka(nilai);
    }

    /** Mengubah teks seperti "50.000" atau "50,000" menjadi 50000; -1 bila tidak valid. */
    public static long baca(String teks) {
        if (teks == null) {
            return -1;
        }
        String bersih = teks.replace(".", "").replace(",", "").replace("Rp", "").trim();
        if (bersih.isEmpty() || !bersih.matches("\\d{1,15}")) {
            return -1;
        }
        return Long.parseLong(bersih);
    }
}
