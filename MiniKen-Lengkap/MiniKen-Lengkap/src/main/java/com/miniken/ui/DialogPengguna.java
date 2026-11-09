package com.miniken.ui;

import com.miniken.dao.PenggunaDAO;
import com.miniken.model.Pengguna;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Formulir tambah / ubah pengguna. Pakai {@link #isDisimpan()} setelah dialog ditutup. */
public class DialogPengguna extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final int PASSWORD_MINIMAL = 6;

    private final PenggunaDAO dao = new PenggunaDAO();
    private final Pengguna pengguna;
    private final boolean baru;
    private boolean disimpan = false;

    private final JTextField txtUsername = new JTextField(20);
    private final JTextField txtNama = new JTextField(20);
    private final JComboBox<String> cmbPeran = new JComboBox<>(new String[]{Pengguna.KASIR, Pengguna.ADMIN});
    private final JCheckBox chkAktif = new JCheckBox("Akun aktif", true);
    private final JPasswordField txtPassword = new JPasswordField(20);

    /** @param pengguna akun yang diubah, atau null untuk akun baru */
    public DialogPengguna(Window induk, Pengguna pengguna) {
        super(induk, pengguna == null ? "Tambah Pengguna" : "Ubah Pengguna", ModalityType.APPLICATION_MODAL);
        this.baru = pengguna == null;
        this.pengguna = baru ? new Pengguna() : pengguna;

        if (!baru) {
            txtUsername.setText(pengguna.getUsername());
            txtUsername.setEditable(false);
            txtNama.setText(pengguna.getNamaLengkap());
            cmbPeran.setSelectedItem(pengguna.getPeran());
            chkAktif.setSelected(pengguna.isAktif());
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        baris(form, 0, "Username", txtUsername);
        baris(form, 1, "Nama lengkap", txtNama);
        baris(form, 2, "Peran", cmbPeran);
        baris(form, 3, "Status", chkAktif);
        if (baru) {
            baris(form, 4, "Password (min. " + PASSWORD_MINIMAL + ")", txtPassword);
        }

        JButton simpan = UiUtil.tombol("Simpan", UiUtil.HIJAU);
        simpan.addActionListener(e -> simpan());
        JButton batal = UiUtil.tombol("Batal", UiUtil.MERAH);
        batal.addActionListener(e -> dispose());
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        bawah.add(simpan);
        bawah.add(batal);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(simpan);
        pack();
        setLocationRelativeTo(induk);
    }

    private void baris(JPanel panel, int y, String label, JComponent isian) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = y;
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        panel.add(new JLabel(label), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        panel.add(isian, g);
    }

    private void simpan() {
        String username = txtUsername.getText().trim();
        String nama = txtNama.getText().trim();
        String peran = (String) cmbPeran.getSelectedItem();
        boolean aktif = chkAktif.isSelected();

        if (username.isEmpty() || nama.isEmpty()) {
            UiUtil.kesalahan(this, "Username dan nama lengkap wajib diisi.");
            return;
        }
        if (!username.matches("[A-Za-z0-9_.-]{3,30}")) {
            UiUtil.kesalahan(this, "Username 3-30 karakter: huruf, angka, titik, garis bawah, atau strip.");
            return;
        }
        String password = new String(txtPassword.getPassword());
        if (baru && password.length() < PASSWORD_MINIMAL) {
            UiUtil.kesalahan(this, "Password minimal " + PASSWORD_MINIMAL + " karakter.");
            return;
        }
        try {
            if (dao.usernameSudahAda(username, pengguna.getId())) {
                UiUtil.kesalahan(this, "Username sudah dipakai pengguna lain.");
                return;
            }
            boolean adminAktifSebelumnya = !baru && pengguna.isAdmin() && pengguna.isAktif();
            boolean adminAktifSesudahnya = Pengguna.ADMIN.equals(peran) && aktif;
            if (adminAktifSebelumnya && !adminAktifSesudahnya && dao.jumlahAdminAktif() <= 1) {
                UiUtil.kesalahan(this, "Tidak bisa: ini satu-satunya admin aktif.");
                return;
            }
            if (!baru && pengguna.getId() == com.miniken.Sesi.pengguna().getId() && !aktif) {
                UiUtil.kesalahan(this, "Anda tidak bisa menonaktifkan akun yang sedang dipakai.");
                return;
            }
            pengguna.setUsername(username);
            pengguna.setNamaLengkap(nama);
            pengguna.setPeran(peran);
            pengguna.setAktif(aktif);
            if (baru) {
                dao.tambah(pengguna, password);
            } else {
                dao.ubah(pengguna);
            }
            disimpan = true;
            dispose();
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }

    public boolean isDisimpan() {
        return disimpan;
    }
}
