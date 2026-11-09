# MiniKen Lengkap – Panduan Menjalankan

Aplikasi kasir minimarket berbasis Java Swing + JDBC + MySQL, dengan login (Admin/Kasir),
transaksi kasir, data barang & stok, riwayat, laporan penjualan, dan pengelolaan pengguna.

## Yang perlu dipasang (sekali saja)

| Kebutuhan | Keterangan |
| --- | --- |
| JDK 11 atau lebih baru | Contoh: Temurin/OpenJDK 17 atau 21. Cek dengan `java -version` |
| Apache NetBeans | Versi 12 atau lebih baru (sudah termasuk Maven) |
| XAMPP (atau MySQL/MariaDB lain) | Dipakai untuk database. Cukup modul **MySQL** |
| Internet | Hanya saat pertama kali, agar NetBeans mengunduh driver `mysql-connector-j` |

## Langkah 1 – Jalankan MySQL

1. Buka **XAMPP Control Panel**.
2. Klik **Start** pada baris **MySQL** sampai berwarna hijau (port 3306).
3. Tidak perlu membuat database sendiri: aplikasi membuat database `miniken`, semua tabel,
   akun awal, dan 10 barang contoh secara **otomatis** saat pertama dijalankan.

> Ingin membuat database manual? Buka http://localhost/phpmyadmin, tab **Import**, pilih
> `database/miniken.sql` di dalam zip, lalu **Go**. Langkah ini opsional.

## Langkah 2 – Buka project di NetBeans

1. Ekstrak `MiniKen-Lengkap.zip` ke satu folder, misalnya `C:\Proyek\MiniKen-Lengkap`.
2. Di NetBeans: **File > Open Project…**, pilih folder `MiniKen-Lengkap` (ikon cangkir Maven), lalu **Open Project**.
3. Tunggu bilah proses di kanan bawah selesai. NetBeans mengunduh driver MySQL otomatis.
   Bila ada tanda seru merah pada `Dependencies`: klik kanan project > **Reload POM**
   (atau **Clean and Build**) dan pastikan internet aktif.
4. Pastikan JDK yang dipakai versi 11+: klik kanan project > **Properties > Build > Compile**.

## Langkah 3 – Jalankan aplikasi

1. Klik kanan project **MiniKenLengkap** > **Run** (atau tombol ▶ / F6).
2. Jendela login muncul. Masuk dengan salah satu akun awal:

| Peran | Username | Password | Bisa akses |
| --- | --- | --- | --- |
| Admin | `admin` | `admin123` | Kasir, Data Barang, Riwayat (semua), Laporan, Pengguna |
| Kasir | `kasir` | `kasir123` | Kasir dan Riwayat milik sendiri |

3. Segera ganti password lewat menu **Akun > Ganti Password…**.

## Cara pakai singkat

- **Kasir:** ketik kode barang (mis. `8999999001`), Enter. Isi *Uang diterima*, tekan **BAYAR (F9)**.
  F2 = kembali ke kolom kode barang. Struk muncul dan bisa dicetak.
- **Data Barang (admin):** cari, tambah, ubah (klik dua kali), hapus. Stok ≤ 5 berwarna kuning.
- **Riwayat:** klik dua kali pada transaksi untuk melihat/mencetak struk.
- **Laporan (admin):** pilih rentang tanggal `yyyy-MM-dd`, lihat omzet, penjualan harian, 10 barang terlaris, ekspor CSV.
- **Pengguna (admin):** tambah kasir, ubah peran/status, reset password, hapus akun.

## Membuat file .jar (opsional, untuk dibagikan)

1. Klik kanan project > **Clean and Build**.
2. Hasilnya `target/MiniKenLengkap-2.0.0.jar` (sudah berisi driver MySQL).
3. Jalankan dengan klik dua kali, atau dari terminal: `java -jar target/MiniKenLengkap-2.0.0.jar`.
   MySQL harus tetap menyala.

## Mengganti pengaturan database

Bawaan: `localhost:3306`, user `root`, password kosong (cocok dengan XAMPP baru).
Bila berbeda, ubah di `src/main/java/com/miniken/db/Koneksi.java`, atau di NetBeans:
**Project Properties > Run > VM Options**:

```
-Dminiken.db.user=root -Dminiken.db.password=SANDI_ANDA -Dminiken.db.url=jdbc:mysql://localhost:3306/miniken?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
```

## Masalah yang sering muncul

| Gejala | Penyebab & solusi |
| --- | --- |
| Pesan "Tidak bisa terhubung ke database MySQL" | MySQL belum Start di XAMPP, atau port 3306 dipakai program lain. Start MySQL lalu jalankan ulang. |
| "Access denied for user 'root'" | Password root Anda tidak kosong. Isi lewat VM Options di atas. |
| "No suitable driver" / `ClassNotFoundException` | Driver belum terunduh. Klik kanan project > **Reload POM**, atau jalankan lewat NetBeans (bukan `java` biasa tanpa classpath). |
| Tanda seru merah di Dependencies | Tidak ada internet saat pertama membuka. Sambungkan internet, **Reload POM**. |
| Lupa password admin | Di phpMyAdmin, pilih database `miniken`, jalankan `DROP TABLE pengguna;`, lalu jalankan ulang aplikasi. Akun `admin` dan `kasir` dibuat kembali dengan password awal. Data barang dan transaksi tidak terhapus. |
| Tulisan terpotong / tampilan aneh | Gunakan JDK 11+; tampilan memakai tema Nimbus. |

## Struktur project

```
MiniKen-Lengkap/
  pom.xml                      konfigurasi Maven (driver MySQL, pembuat jar)
  database/miniken.sql         skema + data awal (opsional diimpor)
  src/main/java/com/miniken/
    Main.java                  titik masuk: cek database lalu login
    Sesi.java                  pengguna yang sedang login
    db/Koneksi.java            koneksi JDBC, pembuatan tabel, data awal
    model/                     Pengguna, Produk, Transaksi, DetailTransaksi, ItemKeranjang
    dao/                       PenggunaDAO, ProdukDAO, TransaksiDAO, LaporanDAO (semua JDBC PreparedStatement)
    ui/                        FormLogin, FormUtama, PanelKasir, PanelProduk, PanelRiwayat,
                               PanelLaporan, PanelPengguna, dan dialog
    util/                      Rupiah, StrukBuilder, Keamanan (hash password PBKDF2)
```
