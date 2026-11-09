package com.miniken;

import com.miniken.model.Pengguna;

/** Menyimpan pengguna yang sedang login. */
public final class Sesi {

    private static Pengguna pengguna;

    private Sesi() {
    }

    public static void masuk(Pengguna p) {
        pengguna = p;
    }

    public static void keluar() {
        pengguna = null;
    }

    public static Pengguna pengguna() {
        return pengguna;
    }

    public static boolean isAdmin() {
        return pengguna != null && pengguna.isAdmin();
    }

    /** Username pengguna aktif, dipakai sebagai pencatat kasir pada transaksi. */
    public static String username() {
        return pengguna == null ? "-" : pengguna.getUsername();
    }
}
