package com.miniken.dao;

import com.miniken.db.Koneksi;
import com.miniken.model.Produk;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Akses data tabel produk memakai JDBC (PreparedStatement). */
public class ProdukDAO {

    private Produk petakan(ResultSet rs) throws SQLException {
        Produk p = new Produk();
        p.setId(rs.getInt("id"));
        p.setKode(rs.getString("kode"));
        p.setNama(rs.getString("nama"));
        p.setKategori(rs.getString("kategori"));
        p.setHarga(rs.getLong("harga"));
        p.setStok(rs.getInt("stok"));
        return p;
    }

    /** Semua barang, atau yang cocok dengan kata kunci pada nama/kode bila kata kunci diisi. */
    public List<Produk> semua(String kataKunci) throws SQLException {
        boolean pakaiFilter = kataKunci != null && !kataKunci.isBlank();
        String sql = "SELECT * FROM produk"
                + (pakaiFilter ? " WHERE nama LIKE ? OR kode LIKE ?" : "")
                + " ORDER BY nama";
        List<Produk> hasil = new ArrayList<>();
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (pakaiFilter) {
                String like = "%" + kataKunci.trim() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(petakan(rs));
                }
            }
        }
        return hasil;
    }

    public Produk cariByKode(String kode) throws SQLException {
        String sql = "SELECT * FROM produk WHERE kode = ?";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? petakan(rs) : null;
            }
        }
    }

    public Produk cariById(int id) throws SQLException {
        String sql = "SELECT * FROM produk WHERE id = ?";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? petakan(rs) : null;
            }
        }
    }

    /** True bila kode sudah dipakai barang lain (idDikecualikan = 0 untuk barang baru). */
    public boolean kodeSudahDipakai(String kode, int idDikecualikan) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produk WHERE kode = ? AND id <> ?";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.setInt(2, idDikecualikan);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void tambah(Produk p) throws SQLException {
        String sql = "INSERT INTO produk (kode, nama, kategori, harga, stok) VALUES (?,?,?,?,?)";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getKode());
            ps.setString(2, p.getNama());
            ps.setString(3, p.getKategori());
            ps.setLong(4, p.getHarga());
            ps.setInt(5, p.getStok());
            ps.executeUpdate();
        }
    }

    public void ubah(Produk p) throws SQLException {
        String sql = "UPDATE produk SET kode=?, nama=?, kategori=?, harga=?, stok=? WHERE id=?";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getKode());
            ps.setString(2, p.getNama());
            ps.setString(3, p.getKategori());
            ps.setLong(4, p.getHarga());
            ps.setInt(5, p.getStok());
            ps.setInt(6, p.getId());
            ps.executeUpdate();
        }
    }

    public void hapus(int id) throws SQLException {
        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM produk WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
