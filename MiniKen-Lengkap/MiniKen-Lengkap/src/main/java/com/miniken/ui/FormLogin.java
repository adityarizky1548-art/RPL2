package com.miniken.ui;

import com.miniken.dao.PenggunaDAO;
import com.miniken.model.Pengguna;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/** Dialog login. Pakai {@link #getHasil()} setelah ditutup; null berarti pengguna membatalkan. */
public class FormLogin extends JDialog {

    private static final long serialVersionUID = 1L;

    private final PenggunaDAO dao = new PenggunaDAO();
    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JLabel lblPesan = new JLabel(" ");
    private Pengguna hasil;

    public FormLogin() {
        super((java.awt.Frame) null, "MiniKen - Masuk", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel logo = new JLabel("MiniKen", JLabel.CENTER);
        logo.setForeground(Color.WHITE);
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 30f));
        JLabel slogan = new JLabel("Belanja Mudah, Hemat Setiap Hari", JLabel.CENTER);
        slogan.setForeground(UiUtil.KUNING);
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiUtil.BIRU_TUA);
        header.setBorder(BorderFactory.createEmptyBorder(16, 16, 14, 16));
        header.add(logo, BorderLayout.CENTER);
        header.add(slogan, BorderLayout.SOUTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 4, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 4, 5, 4);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = 0;
        g.gridy = 0;
        form.add(new JLabel("Username"), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        form.add(txtUsername, g);
        g.gridx = 0;
        g.gridy = 1;
        g.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Password"), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        form.add(txtPassword, g);
        g.gridx = 0;
        g.gridy = 2;
        g.gridwidth = 2;
        lblPesan.setForeground(UiUtil.MERAH);
        form.add(lblPesan, g);

        JButton btnMasuk = UiUtil.tombol("Masuk", UiUtil.HIJAU);
        JButton btnKeluar = UiUtil.tombol("Keluar", UiUtil.MERAH);
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        tombol.add(btnMasuk);
        tombol.add(btnKeluar);

        JLabel petunjuk = new JLabel("Akun awal: admin / admin123  |  kasir / kasir123", JLabel.CENTER);
        petunjuk.setForeground(Color.GRAY);
        petunjuk.setFont(petunjuk.getFont().deriveFont(11f));
        JPanel bawah = new JPanel(new BorderLayout());
        bawah.add(tombol, BorderLayout.CENTER);
        bawah.add(petunjuk, BorderLayout.SOUTH);
        bawah.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);

        btnMasuk.addActionListener(e -> masuk());
        txtPassword.addActionListener(e -> masuk());
        txtUsername.addActionListener(e -> txtPassword.requestFocusInWindow());
        btnKeluar.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnMasuk);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void masuk() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            lblPesan.setText("Username dan password wajib diisi.");
            return;
        }
        try {
            Pengguna p = dao.login(username, password);
            if (p == null) {
                lblPesan.setText("Username/password salah atau akun nonaktif.");
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
                return;
            }
            hasil = p;
            dispose();
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }

    public Pengguna getHasil() {
        return hasil;
    }
}
