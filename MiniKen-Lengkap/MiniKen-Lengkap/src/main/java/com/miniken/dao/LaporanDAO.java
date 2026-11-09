package com.miniken.dao;

import com.miniken.db.Koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Query laporan penjualan. Rentang tanggal bersifat inklusif (dari 00:00 hari pertama
 * sampai akhir hari terakhir). Hasil berupa baris Object[] agar ringkas.
 */
public class LaporanDAO {

    private static Timestamp awal(LocalDate dari) {
        return Timestamp.valueOf(dari.atStartOfDay());
    }

    private static Timestamp akhir(LocalDate sampai) {
        return Timestamp.valueOf(sampai.plusDays(1).atStartOfDay());
    }

    /** {jumlah transaksi (Long), omzet (Long), jumlah barang terjual (Long)} */
    public long[] ringkasan(LocalDate dari, LocalDate sampai) throws SQLException {
        long[] hasil = new long[3];
        String sqlTransaksi = "SELECT COUNT(*), COALESCE(SUM(total), 0) FROM transaksi WHERE waktu >= ? AND waktu < ?";
        String sqlBarang = "SELECT COALESCE(SUM(d.qty), 0) FROM detail_transaksi d "
                + "JOIN transaksi t ON t.id = d.transaksi_id WHERE t.waktu >= ? AND t.waktu < ?";
        try (Connection c = Koneksi.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(sqlTransaksi)) {
                ps.setTimestamp(1, awal(dari));
                ps.setTimestamp(2, akhir(sampai));
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    hasil[0] = rs.getLong(1);
                    hasil[1] = rs.getLong(2);
                }
            }
            try (PreparedStatement ps = c.prepareStatement(sqlBarang)) {
                ps.setTimestamp(1, awal(dari));
                ps.setTimestamp(2, akhir(sampai));
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    hasil[2] = rs.getLong(1);
                }
            }
        }
        return hasil;
    }

    /** Baris: {tanggal (LocalDate), jumlah transaksi (Long), omzet (Long)}, tanggal terbaru dulu. */
    public List<Object[]> penjualanHarian(LocalDate dari, LocalDate sampai) throws SQLException {
        List<Object[]> hasil = new ArrayList<>();
        String sql = "SELECT DATE(waktu) AS tgl, COUNT(*) AS jumlah, SUM(total) AS omzet FROM transaksi "
                + "WHERE waktu >= ? AND waktu < ? GROUP BY DATE(waktu) ORDER BY tgl DESC";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, awal(dari));
            ps.setTimestamp(2, akhir(sampai));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(new Object[]{rs.getDate("tgl").toLocalDate(), rs.getLong("jumlah"), rs.getLong("omzet")});
                }
            }
        }
        return hasil;
    }

    /** Baris: {nama barang (String), jumlah terjual (Long), omzet (Long)}, 10 terlaris. */
    public List<Object[]> terlaris(LocalDate dari, LocalDate sampai) throws SQLException {
        List<Object[]> hasil = new ArrayList<>();
        String sql = "SELECT d.nama_produk AS nama, SUM(d.qty) AS qty, SUM(d.subtotal) AS omzet "
                + "FROM detail_transaksi d JOIN transaksi t ON t.id = d.transaksi_id "
                + "WHERE t.waktu >= ? AND t.waktu < ? GROUP BY d.nama_produk ORDER BY qty DESC, omzet DESC LIMIT 10";
        try (Connection c = Koneksi.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, awal(dari));
            ps.setTimestamp(2, akhir(sampai));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(new Object[]{rs.getString("nama"), rs.getLong("qty"), rs.getLong("omzet")});
                }
            }
        }
        return hasil;
    }
}
