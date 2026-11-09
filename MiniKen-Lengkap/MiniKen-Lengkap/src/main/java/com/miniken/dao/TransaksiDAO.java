package com.miniken.dao;

import com.miniken.db.Koneksi;
import com.miniken.model.DetailTransaksi;
import com.miniken.model.ItemKeranjang;
import com.miniken.model.Transaksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Akses data transaksi memakai JDBC, termasuk transaksi database (commit/rollback). */
public class TransaksiDAO {

    private static final DateTimeFormatter FORMAT_NO = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Menyimpan transaksi dan mengurangi stok dalam SATU transaksi database.
     * Bila stok salah satu barang tidak cukup, semua perubahan dibatalkan (rollback).
     */
    public Transaksi simpan(Collection<ItemKeranjang> items, long bayar, String kasir) throws SQLException {
        long total = 0;
        for (ItemKeranjang i : items) {
            total += i.getSubtotal();
        }
        if (items.isEmpty()) {
            throw new SQLException("Keranjang masih kosong.");
        }
        if (bayar < total) {
            throw new SQLException("Uang bayar kurang dari total belanja.");
        }

        LocalDateTime sekarang = LocalDateTime.now();
        Connection c = Koneksi.getConnection();
        try {
            c.setAutoCommit(false);

            // 1. Kurangi stok; UPDATE hanya berhasil bila stok masih mencukupi
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE produk SET stok = stok - ? WHERE id = ? AND stok >= ?")) {
                for (ItemKeranjang i : items) {
                    ps.setInt(1, i.getQty());
                    ps.setInt(2, i.getProdukId());
                    ps.setInt(3, i.getQty());
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Stok " + i.getNama() + " tidak cukup.");
                    }
                }
            }

            // 2. Nomor transaksi
            int urutan;
            try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM transaksi")) {
                rs.next();
                urutan = rs.getInt(1) % 1000;
            }
            String noTransaksi = "MK" + sekarang.format(FORMAT_NO) + String.format("%03d", urutan);

            // 3. Header transaksi
            int idTransaksi;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO transaksi (no_transaksi, waktu, kasir, total, bayar, kembali) VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, noTransaksi);
                ps.setTimestamp(2, Timestamp.valueOf(sekarang));
                ps.setString(3, kasir);
                ps.setLong(4, total);
                ps.setLong(5, bayar);
                ps.setLong(6, bayar - total);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    idTransaksi = keys.getInt(1);
                }
            }

            // 4. Detail barang
            List<DetailTransaksi> detail = new ArrayList<>();
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO detail_transaksi (transaksi_id, kode_produk, nama_produk, harga, qty, subtotal) "
                    + "VALUES (?,?,?,?,?,?)")) {
                for (ItemKeranjang i : items) {
                    ps.setInt(1, idTransaksi);
                    ps.setString(2, i.getKode());
                    ps.setString(3, i.getNama());
                    ps.setLong(4, i.getHarga());
                    ps.setInt(5, i.getQty());
                    ps.setLong(6, i.getSubtotal());
                    ps.addBatch();

                    DetailTransaksi d = new DetailTransaksi();
                    d.setKodeProduk(i.getKode());
                    d.setNamaProduk(i.getNama());
                    d.setHarga(i.getHarga());
                    d.setQty(i.getQty());
                    d.setSubtotal(i.getSubtotal());
                    detail.add(d);
                }
                ps.executeBatch();
            }

            c.commit();

            Transaksi t = new Transaksi();
            t.setId(idTransaksi);
            t.setNoTransaksi(noTransaksi);
            t.setWaktu(sekarang);
            t.setKasir(kasir);
            t.setTotal(total);
            t.setBayar(bayar);
            t.setKembali(bayar - total);
            t.setItems(detail);
            return t;
        } catch (SQLException e) {
            c.rollback();
            throw e;
        } finally {
            try {
                c.setAutoCommit(true);
            } finally {
                c.close();
            }
        }
    }

    /** 100 transaksi terbaru; bila kasir diisi, hanya transaksi milik kasir itu (tanpa detail barang). */
    public List<Transaksi> riwayat(String kasir) throws SQLException {
        List<Transaksi> hasil = new ArrayList<>();
        String sql = "SELECT * FROM transaksi" + (kasir == null ? "" : " WHERE kasir = ?")
                + " ORDER BY waktu DESC, id DESC LIMIT 100";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (kasir != null) {
                ps.setString(1, kasir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaksi t = new Transaksi();
                    t.setId(rs.getInt("id"));
                    t.setNoTransaksi(rs.getString("no_transaksi"));
                    t.setWaktu(rs.getTimestamp("waktu").toLocalDateTime());
                    t.setKasir(rs.getString("kasir"));
                    t.setTotal(rs.getLong("total"));
                    t.setBayar(rs.getLong("bayar"));
                    t.setKembali(rs.getLong("kembali"));
                    hasil.add(t);
                }
            }
        }
        return hasil;
    }

    /** Mengisi daftar barang pada transaksi untuk keperluan struk. */
    public void muatDetail(Transaksi t) throws SQLException {
        List<DetailTransaksi> detail = new ArrayList<>();
        String sql = "SELECT * FROM detail_transaksi WHERE transaksi_id = ? ORDER BY id";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, t.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetailTransaksi d = new DetailTransaksi();
                    d.setId(rs.getInt("id"));
                    d.setKodeProduk(rs.getString("kode_produk"));
                    d.setNamaProduk(rs.getString("nama_produk"));
                    d.setHarga(rs.getLong("harga"));
                    d.setQty(rs.getInt("qty"));
                    d.setSubtotal(rs.getLong("subtotal"));
                    detail.add(d);
                }
            }
        }
        t.setItems(detail);
    }
}
