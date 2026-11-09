package com.miniken.db;

import com.miniken.util.Keamanan;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Pengelola koneksi JDBC ke MySQL/MariaDB.
 * Ubah nilai bawaan di bawah, atau berikan lewat opsi JVM, contoh:
 * -Dminiken.db.url=jdbc:mysql://localhost:3306/miniken -Dminiken.db.user=root -Dminiken.db.password=rahasia
 */
public final class Koneksi {

    private static final String URL = System.getProperty("miniken.db.url",
            "jdbc:mysql://localhost:3306/miniken?createDatabaseIfNotExist=true"
            + "&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta");
    private static final String USER = System.getProperty("miniken.db.user", "root");
    private static final String PASSWORD = System.getProperty("miniken.db.password", "");

    private Koneksi() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Membuat database/tabel bila belum ada, menambah kolom baru pada database versi lama,
     * lalu mengisi akun awal dan contoh barang bila masih kosong.
     */
    public static void inisialisasi() throws SQLException {
        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS pengguna ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(30) NOT NULL UNIQUE,"
                    + "nama_lengkap VARCHAR(100) NOT NULL,"
                    + "password_hash VARCHAR(200) NOT NULL,"
                    + "peran VARCHAR(10) NOT NULL DEFAULT 'KASIR',"
                    + "aktif TINYINT(1) NOT NULL DEFAULT 1) ENGINE=InnoDB");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS produk ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "kode VARCHAR(30) NOT NULL UNIQUE,"
                    + "nama VARCHAR(100) NOT NULL,"
                    + "kategori VARCHAR(50),"
                    + "harga BIGINT NOT NULL DEFAULT 0,"
                    + "stok INT NOT NULL DEFAULT 0) ENGINE=InnoDB");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS transaksi ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "no_transaksi VARCHAR(30) NOT NULL UNIQUE,"
                    + "waktu DATETIME NOT NULL,"
                    + "kasir VARCHAR(30) NOT NULL DEFAULT 'admin',"
                    + "total BIGINT NOT NULL,"
                    + "bayar BIGINT NOT NULL,"
                    + "kembali BIGINT NOT NULL) ENGINE=InnoDB");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS detail_transaksi ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "transaksi_id INT NOT NULL,"
                    + "kode_produk VARCHAR(30) NOT NULL,"
                    + "nama_produk VARCHAR(100) NOT NULL,"
                    + "harga BIGINT NOT NULL,"
                    + "qty INT NOT NULL,"
                    + "subtotal BIGINT NOT NULL,"
                    + "CONSTRAINT fk_detail_transaksi FOREIGN KEY (transaksi_id) "
                    + "REFERENCES transaksi(id) ON DELETE CASCADE) ENGINE=InnoDB");

            tambahKolomKasirBilaPerlu(c, st);

            if (kosong(st, "pengguna")) {
                isiPenggunaAwal(c);
            }
            if (kosong(st, "produk")) {
                isiDataContoh(c);
            }
        }
    }

    private static void tambahKolomKasirBilaPerlu(Connection c, Statement st) throws SQLException {
        DatabaseMetaData md = c.getMetaData();
        try (ResultSet rs = md.getColumns(c.getCatalog(), null, "transaksi", "kasir")) {
            if (!rs.next()) {
                st.executeUpdate("ALTER TABLE transaksi ADD COLUMN kasir VARCHAR(30) NOT NULL DEFAULT 'admin'");
            }
        }
    }

    private static boolean kosong(Statement st, String tabel) throws SQLException {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabel)) {
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    private static void isiPenggunaAwal(Connection c) throws SQLException {
        String sql = "INSERT INTO pengguna (username, nama_lengkap, password_hash, peran, aktif) VALUES (?,?,?,?,1)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "admin");
            ps.setString(2, "Administrator");
            ps.setString(3, Keamanan.hash("admin123"));
            ps.setString(4, "ADMIN");
            ps.addBatch();
            ps.setString(1, "kasir");
            ps.setString(2, "Kasir 1");
            ps.setString(3, Keamanan.hash("kasir123"));
            ps.setString(4, "KASIR");
            ps.addBatch();
            ps.executeBatch();
        }
    }

    private static void isiDataContoh(Connection c) throws SQLException {
        String[][] data = {
            {"8999999001", "Indomie Goreng", "Makanan", "3500", "120"},
            {"8999999002", "Aqua Botol 600 ml", "Minuman", "4000", "80"},
            {"8999999003", "Teh Botol Sosro 450 ml", "Minuman", "5500", "60"},
            {"8999999004", "Roti Tawar Sari Roti", "Makanan", "16500", "25"},
            {"8999999005", "Susu UHT Ultra 250 ml", "Minuman", "6500", "40"},
            {"8999999006", "Chitato Sapi Panggang", "Snack", "11000", "30"},
            {"8999999007", "Gula Pasir 1 kg", "Sembako", "17500", "20"},
            {"8999999008", "Minyak Goreng 1 L", "Sembako", "19500", "4"},
            {"8999999009", "Sabun Mandi Lifebuoy", "Perawatan", "4500", "50"},
            {"8999999010", "Kopi Kapal Api Sachet", "Minuman", "1500", "200"}
        };
        String sql = "INSERT INTO produk (kode, nama, kategori, harga, stok) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (String[] d : data) {
                ps.setString(1, d[0]);
                ps.setString(2, d[1]);
                ps.setString(3, d[2]);
                ps.setLong(4, Long.parseLong(d[3]));
                ps.setInt(5, Integer.parseInt(d[4]));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
