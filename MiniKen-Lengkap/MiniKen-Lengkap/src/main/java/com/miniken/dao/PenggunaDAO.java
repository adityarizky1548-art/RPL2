package com.miniken.dao;

import com.miniken.db.Koneksi;
import com.miniken.model.Pengguna;
import com.miniken.util.Keamanan;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Akses data tabel pengguna (login dan pengelolaan akun). */
public class PenggunaDAO {

    private Pengguna petakan(ResultSet rs) throws SQLException {
        Pengguna p = new Pengguna();
        p.setId(rs.getInt("id"));
        p.setUsername(rs.getString("username"));
        p.setNamaLengkap(rs.getString("nama_lengkap"));
        p.setPeran(rs.getString("peran"));
        p.setAktif(rs.getInt("aktif") == 1);
        return p;
    }

    /** Mengembalikan pengguna bila username/password benar dan akun aktif; selain itu null. */
    public Pengguna login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM pengguna WHERE username = ? AND aktif = 1";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && Keamanan.cocok(password, rs.getString("password_hash"))) {
                    return petakan(rs);
                }
            }
        }
        return null;
    }

    /** Memeriksa kata sandi pengguna yang sedang login (untuk ganti password). */
    public boolean passwordBenar(int id, String password) throws SQLException {
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT password_hash FROM pengguna WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && Keamanan.cocok(password, rs.getString(1));
            }
        }
    }

    public List<Pengguna> semua() throws SQLException {
        List<Pengguna> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM pengguna ORDER BY peran, username");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(petakan(rs));
            }
        }
        return hasil;
    }

    public boolean usernameSudahAda(String username, int idDikecualikan) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pengguna WHERE username = ? AND id <> ?";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setInt(2, idDikecualikan);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int jumlahAdminAktif() throws SQLException {
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) FROM pengguna WHERE peran = 'ADMIN' AND aktif = 1");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    public void tambah(Pengguna p, String passwordPolos) throws SQLException {
        String sql = "INSERT INTO pengguna (username, nama_lengkap, password_hash, peran, aktif) VALUES (?,?,?,?,?)";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getUsername());
            ps.setString(2, p.getNamaLengkap());
            ps.setString(3, Keamanan.hash(passwordPolos));
            ps.setString(4, p.getPeran());
            ps.setInt(5, p.isAktif() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    /** Mengubah nama, peran dan status aktif (username dan password tidak berubah). */
    public void ubah(Pengguna p) throws SQLException {
        String sql = "UPDATE pengguna SET nama_lengkap = ?, peran = ?, aktif = ? WHERE id = ?";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getNamaLengkap());
            ps.setString(2, p.getPeran());
            ps.setInt(3, p.isAktif() ? 1 : 0);
            ps.setInt(4, p.getId());
            ps.executeUpdate();
        }
    }

    public void ubahPassword(int id, String passwordPolos) throws SQLException {
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE pengguna SET password_hash = ? WHERE id = ?")) {
            ps.setString(1, Keamanan.hash(passwordPolos));
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void hapus(int id) throws SQLException {
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM pengguna WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
