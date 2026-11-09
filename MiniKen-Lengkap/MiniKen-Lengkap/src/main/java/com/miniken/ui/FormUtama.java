package com.miniken.ui;

import com.miniken.Sesi;
import com.miniken.dao.PenggunaDAO;
import com.miniken.model.Pengguna;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;

/**
 * Jendela utama. Admin melihat semua tab; kasir hanya Kasir dan Riwayat miliknya.
 */
public class FormUtama extends JFrame {

    private static final long serialVersionUID = 1L;

    private final PanelKasir panelKasir;
    private final PanelProduk panelProduk;
    private final PanelRiwayat panelRiwayat;
    private final PanelLaporan panelLaporan;
    private final Runnable setelahKeluar;

    /** @param setelahKeluar dipanggil setelah pengguna memilih Akun > Ganti Pengguna (kembali ke login) */
    public FormUtama(Runnable setelahKeluar) {
        super("MiniKen - Kasir Minimarket");
        this.setelahKeluar = setelahKeluar;
        Pengguna user = Sesi.pengguna();
        boolean admin = Sesi.isAdmin();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 600));
        setSize(1100, 680);
        setLocationRelativeTo(null);

        panelRiwayat = new PanelRiwayat(admin ? null : user.getUsername());
        panelLaporan = admin ? new PanelLaporan() : null;
        panelProduk = admin ? new PanelProduk(() -> { }) : null;
        panelKasir = new PanelKasir(() -> {
            panelRiwayat.muat();
            if (panelProduk != null) {
                panelProduk.muat();
            }
        });

        JTabbedPane tab = new JTabbedPane();
        tab.setFont(tab.getFont().deriveFont(Font.BOLD, 14f));
        tab.addTab("Kasir", panelKasir);
        if (admin) {
            tab.addTab("Data Barang", panelProduk);
        }
        tab.addTab("Riwayat", panelRiwayat);
        if (admin) {
            tab.addTab("Laporan", panelLaporan);
            tab.addTab("Pengguna", new PanelPengguna());
        }
        tab.addChangeListener(e -> {
            java.awt.Component terpilih = tab.getSelectedComponent();
            if (terpilih == panelKasir) {
                panelKasir.fokusKode();
            } else if (terpilih == panelProduk) {
                panelProduk.muat();
            } else if (terpilih == panelRiwayat) {
                panelRiwayat.muat();
            } else if (terpilih == panelLaporan) {
                panelLaporan.muat();
            }
        });

        setJMenuBar(buatMenu());
        setLayout(new BorderLayout());
        add(buatHeader(user), BorderLayout.NORTH);
        add(tab, BorderLayout.CENTER);
    }

    private JPanel buatHeader(Pengguna user) {
        JLabel logo = new JLabel("MiniKen");
        logo.setForeground(Color.WHITE);
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 24f));
        JLabel slogan = new JLabel("  Belanja Mudah, Hemat Setiap Hari");
        slogan.setForeground(UiUtil.KUNING);
        JLabel info = new JLabel(user.getNamaLengkap() + "  (" + user.getPeran() + ")");
        info.setForeground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtil.BIRU_TUA);
        header.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        header.add(logo, BorderLayout.WEST);
        header.add(slogan, BorderLayout.CENTER);
        header.add(info, BorderLayout.EAST);
        return header;
    }

    private JMenuBar buatMenu() {
        JMenu akun = new JMenu("Akun");
        JMenuItem ganti = new JMenuItem("Ganti Password...");
        JMenuItem keluar = new JMenuItem("Ganti Pengguna (Keluar)");
        JMenuItem tutup = new JMenuItem("Tutup Aplikasi");
        ganti.addActionListener(e -> gantiPassword());
        keluar.addActionListener(e -> {
            Sesi.keluar();
            dispose();
            setelahKeluar.run();
        });
        tutup.addActionListener(e -> System.exit(0));
        akun.add(ganti);
        akun.add(keluar);
        akun.addSeparator();
        akun.add(tutup);
        JMenuBar bar = new JMenuBar();
        bar.add(akun);
        return bar;
    }

    private void gantiPassword() {
        JPasswordField lama = new JPasswordField(18);
        JPasswordField baru = new JPasswordField(18);
        JPasswordField ulang = new JPasswordField(18);
        Object[] isi = {"Password lama:", lama, "Password baru (min. 6 karakter):", baru, "Ulangi password baru:", ulang};
        int jawab = JOptionPane.showConfirmDialog(this, isi, "Ganti Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (jawab != JOptionPane.OK_OPTION) {
            return;
        }
        String pLama = new String(lama.getPassword());
        String pBaru = new String(baru.getPassword());
        if (pBaru.length() < 6) {
            UiUtil.kesalahan(this, "Password baru minimal 6 karakter.");
            return;
        }
        if (!pBaru.equals(new String(ulang.getPassword()))) {
            UiUtil.kesalahan(this, "Pengulangan password baru tidak sama.");
            return;
        }
        try {
            PenggunaDAO dao = new PenggunaDAO();
            int id = Sesi.pengguna().getId();
            if (!dao.passwordBenar(id, pLama)) {
                UiUtil.kesalahan(this, "Password lama salah.");
                return;
            }
            dao.ubahPassword(id, pBaru);
            UiUtil.info(this, "Password berhasil diganti.");
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }
}
