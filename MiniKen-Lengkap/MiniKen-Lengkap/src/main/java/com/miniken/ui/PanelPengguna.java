package com.miniken.ui;

import com.miniken.Sesi;
import com.miniken.dao.PenggunaDAO;
import com.miniken.model.Pengguna;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/** Halaman Pengguna (khusus admin): tambah, ubah, reset password, hapus akun. */
public class PanelPengguna extends JPanel {

    private static final long serialVersionUID = 1L;

    private final PenggunaDAO dao = new PenggunaDAO();
    private final List<Pengguna> data = new ArrayList<>();
    private final DefaultTableModel model = UiUtil.modelTabel("Username", "Nama Lengkap", "Peran", "Status");
    private final JTable tabel = new JTable(model);

    public PanelPengguna() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel judul = new JLabel("Pengelolaan Pengguna");
        judul.setFont(judul.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        add(judul, BorderLayout.NORTH);

        UiUtil.rapikanTabel(tabel);
        tabel.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JButton btnTambah = UiUtil.tombol("+ Tambah Pengguna", UiUtil.HIJAU);
        JButton btnUbah = UiUtil.tombol("Ubah", UiUtil.BIRU);
        JButton btnReset = UiUtil.tombol("Reset Password", UiUtil.BIRU_TUA);
        JButton btnHapus = UiUtil.tombol("Hapus", UiUtil.MERAH);
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        bawah.add(btnTambah);
        bawah.add(btnUbah);
        bawah.add(btnReset);
        bawah.add(btnHapus);
        add(bawah, BorderLayout.SOUTH);

        btnTambah.addActionListener(e -> bukaForm(null));
        btnUbah.addActionListener(e -> {
            Pengguna p = terpilih();
            if (p != null) {
                bukaForm(p);
            }
        });
        btnReset.addActionListener(e -> resetPassword());
        btnHapus.addActionListener(e -> hapus());

        muat();
    }

    public void muat() {
        try {
            data.clear();
            data.addAll(dao.semua());
            model.setRowCount(0);
            for (Pengguna p : data) {
                model.addRow(new Object[]{p.getUsername(), p.getNamaLengkap(), p.getPeran(),
                    p.isAktif() ? "Aktif" : "Nonaktif"});
            }
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }

    private Pengguna terpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            UiUtil.kesalahan(this, "Pilih satu pengguna pada tabel terlebih dahulu.");
            return null;
        }
        return data.get(tabel.convertRowIndexToModel(baris));
    }

    private void bukaForm(Pengguna p) {
        DialogPengguna dlg = new DialogPengguna(SwingUtilities.getWindowAncestor(this), p);
        dlg.setVisible(true);
        if (dlg.isDisimpan()) {
            muat();
        }
    }

    private void resetPassword() {
        Pengguna p = terpilih();
        if (p == null) {
            return;
        }
        JPasswordField baru = new JPasswordField(18);
        Object[] isi = {"Password baru untuk '" + p.getUsername() + "' (min. 6 karakter):", baru};
        int jawab = JOptionPane.showConfirmDialog(this, isi, "Reset Password",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (jawab != JOptionPane.OK_OPTION) {
            return;
        }
        String password = new String(baru.getPassword());
        if (password.length() < 6) {
            UiUtil.kesalahan(this, "Password minimal 6 karakter.");
            return;
        }
        try {
            dao.ubahPassword(p.getId(), password);
            UiUtil.info(this, "Password '" + p.getUsername() + "' berhasil diubah.");
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }

    private void hapus() {
        Pengguna p = terpilih();
        if (p == null) {
            return;
        }
        if (p.getId() == Sesi.pengguna().getId()) {
            UiUtil.kesalahan(this, "Anda tidak bisa menghapus akun yang sedang dipakai.");
            return;
        }
        try {
            if (p.isAdmin() && p.isAktif() && dao.jumlahAdminAktif() <= 1) {
                UiUtil.kesalahan(this, "Tidak bisa: ini satu-satunya admin aktif.");
                return;
            }
            int jawab = JOptionPane.showConfirmDialog(this,
                    "Hapus pengguna '" + p.getUsername() + "'? Riwayat transaksinya tetap tersimpan.",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (jawab == JOptionPane.YES_OPTION) {
                dao.hapus(p.getId());
                muat();
            }
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }
}
