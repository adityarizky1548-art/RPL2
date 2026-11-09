package com.miniken;

import com.miniken.db.Koneksi;
import com.miniken.model.Pengguna;
import com.miniken.ui.FormLogin;
import com.miniken.ui.FormUtama;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // tampilan bawaan dipakai bila Nimbus tidak tersedia
        }

        try {
            Koneksi.inisialisasi();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Tidak bisa terhubung ke database MySQL.\n\n"
                    + "1. Pastikan MySQL/XAMPP sudah berjalan (tombol Start pada MySQL).\n"
                    + "2. Cek pengaturan di kelas db.Koneksi (localhost:3306, user root, password kosong).\n\n"
                    + "Detail: " + e.getMessage(),
                    "MiniKen - Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(Main::tampilkanLogin);
    }

    /** Menampilkan login; bila berhasil membuka jendela utama, bila dibatalkan aplikasi selesai. */
    private static void tampilkanLogin() {
        FormLogin login = new FormLogin();
        login.setVisible(true);
        Pengguna p = login.getHasil();
        if (p == null) {
            System.exit(0);
            return;
        }
        Sesi.masuk(p);
        new FormUtama(() -> SwingUtilities.invokeLater(Main::tampilkanLogin)).setVisible(true);
    }
}
