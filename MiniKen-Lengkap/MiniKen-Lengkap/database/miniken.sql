-- ============================================================
--  MiniKen - Skema database + data awal (MySQL / MariaDB)
--
--  Cara pakai (opsional, aplikasi sebenarnya membuat semua ini
--  otomatis saat pertama dijalankan):
--    phpMyAdmin > tab "Import" > pilih berkas ini > Go
--  atau lewat terminal:
--    mysql -u root < database/miniken.sql
--
--  Akun awal:
--    admin / admin123   (peran ADMIN)
--    kasir / kasir123   (peran KASIR)
--  Password disimpan sebagai hash PBKDF2, bukan teks asli.
--  Segera ganti password setelah login pertama (menu Akun).
-- ============================================================

CREATE DATABASE IF NOT EXISTS miniken CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE miniken;

-- ---------- Tabel ----------

CREATE TABLE IF NOT EXISTS pengguna (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(30)  NOT NULL UNIQUE,
    nama_lengkap   VARCHAR(100) NOT NULL,
    password_hash  VARCHAR(200) NOT NULL,
    peran          VARCHAR(10)  NOT NULL DEFAULT 'KASIR',   -- ADMIN atau KASIR
    aktif          TINYINT(1)   NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS produk (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    kode      VARCHAR(30)  NOT NULL UNIQUE,
    nama      VARCHAR(100) NOT NULL,
    kategori  VARCHAR(50),
    harga     BIGINT       NOT NULL DEFAULT 0,
    stok      INT          NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS transaksi (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    no_transaksi  VARCHAR(30) NOT NULL UNIQUE,
    waktu         DATETIME    NOT NULL,
    kasir         VARCHAR(30) NOT NULL DEFAULT 'admin',
    total         BIGINT      NOT NULL,
    bayar         BIGINT      NOT NULL,
    kembali       BIGINT      NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detail_transaksi (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    transaksi_id  INT          NOT NULL,
    kode_produk   VARCHAR(30)  NOT NULL,
    nama_produk   VARCHAR(100) NOT NULL,
    harga         BIGINT       NOT NULL,
    qty           INT          NOT NULL,
    subtotal      BIGINT       NOT NULL,
    CONSTRAINT fk_detail_transaksi FOREIGN KEY (transaksi_id)
        REFERENCES transaksi(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- Data awal ----------

INSERT INTO pengguna (username, nama_lengkap, password_hash, peran, aktif)
SELECT 'admin', 'Administrator',
       'pbkdf2$65000$83684964a516890202c3e60c8b5999fe$cdc8093390711484e0cee227a33d48f286d06c82525650e77553bc7474448409',
       'ADMIN', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM pengguna WHERE username = 'admin');

INSERT INTO pengguna (username, nama_lengkap, password_hash, peran, aktif)
SELECT 'kasir', 'Kasir 1',
       'pbkdf2$65000$ad995efafab09110c24f6f82dc2cc3b3$744e3be9c85655f7a1a3a51fad76ead3150d3354680d3889d3d18a38d726084c',
       'KASIR', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM pengguna WHERE username = 'kasir');

INSERT IGNORE INTO produk (kode, nama, kategori, harga, stok) VALUES
    ('8999999001', 'Indomie Goreng',         'Makanan',   3500, 120),
    ('8999999002', 'Aqua Botol 600 ml',      'Minuman',   4000,  80),
    ('8999999003', 'Teh Botol Sosro 450 ml', 'Minuman',   5500,  60),
    ('8999999004', 'Roti Tawar Sari Roti',   'Makanan',  16500,  25),
    ('8999999005', 'Susu UHT Ultra 250 ml',  'Minuman',   6500,  40),
    ('8999999006', 'Chitato Sapi Panggang',  'Snack',    11000,  30),
    ('8999999007', 'Gula Pasir 1 kg',        'Sembako',  17500,  20),
    ('8999999008', 'Minyak Goreng 1 L',      'Sembako',  19500,   4),
    ('8999999009', 'Sabun Mandi Lifebuoy',   'Perawatan', 4500,  50),
    ('8999999010', 'Kopi Kapal Api Sachet',  'Minuman',   1500, 200);
