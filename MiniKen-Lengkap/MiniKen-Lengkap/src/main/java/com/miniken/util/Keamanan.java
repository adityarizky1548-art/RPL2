package com.miniken.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Penyimpanan kata sandi: PBKDF2-HMAC-SHA256 dengan salt acak.
 * Format tersimpan: pbkdf2$iterasi$salt(hex)$hash(hex)
 */
public final class Keamanan {

    private static final int ITERASI = 65000;
    private static final int PANJANG_KUNCI_BIT = 256;

    private Keamanan() {
    }

    public static String hash(String password) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return "pbkdf2$" + ITERASI + "$" + hex(salt) + "$" + hex(turunkan(password, salt, ITERASI));
    }

    public static boolean cocok(String password, String tersimpan) {
        if (password == null || tersimpan == null) {
            return false;
        }
        String[] bagian = tersimpan.split("\\$");
        if (bagian.length != 4 || !"pbkdf2".equals(bagian[0])) {
            return false;
        }
        try {
            int iterasi = Integer.parseInt(bagian[1]);
            byte[] salt = dariHex(bagian[2]);
            byte[] harapan = dariHex(bagian[3]);
            byte[] hasil = turunkan(password, salt, iterasi);
            return MessageDigest.isEqual(harapan, hasil);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] turunkan(String password, byte[] salt, int iterasi) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterasi, PANJANG_KUNCI_BIT);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 tidak tersedia di JDK ini", e);
        }
    }

    private static String hex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] dariHex(String s) {
        if (s.length() % 2 != 0) {
            throw new IllegalArgumentException("hex tidak valid");
        }
        byte[] out = new byte[s.length() / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return out;
    }
}
