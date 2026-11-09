package com.miniken.ui;

import com.miniken.dao.ProdukDAO;
import com.miniken.model.Produk;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Formulir tambah / ubah barang. Pakai {@link #isDisimpan()} setelah dialog ditutup. */
public class DialogProduk extends JDialog {

    private static final long serialVersionUID = 1L;

    private final ProdukDAO dao = new ProdukDAO();
    private final Produk produk;
    private boolean disimpan = false;

    private final JTextField txtKode = new JTextField(20);
    private final JTextField txtNama = new JTextField(20);
    private final JTextField txtKategori = new JTextField(20);
    private final JTextField txtHarga = new JTextField(20);
    private final JTextField txtStok = new JTextField(20);

    /** @param produk barang yang diubah, atau null untuk barang baru */
    public DialogProduk(Window induk, Produk produk) {
        super(induk, produk == null ? "Tambah Barang" : "Ubah Barang", ModalityType.APPLICATION_MODAL);
        this.produk = produk == null ? new Produk() : produk;

        if (produk != null) {
            txtKode.setText(produk.getKode());
            txtNama.setText(produk.getNama());
            txtKategori.setText(produk.getKategori());
            txtHarga.setText(String.valueOf(produk.getHarga()));
            txtStok.setText(String.valueOf(produk.getStok()));
        } else {
            txtHarga.setText("0");
            txtStok.setText("0");
        }

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        tambahBaris(form, 0, "Kode barang", txtKode);
        tambahBaris(form, 1, "Nama barang", txtNama);
        tambahBaris(form, 2, "Kategori", txtKategori);
        tambahBaris(form, 3, "Harga (Rp)", txtHarga);
        tambahBaris(form, 4, "Stok", txtStok);

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

    private void tambahBaris(JPanel panel, int baris, String label, JTextField isian) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridy = baris;
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
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        if (kode.isEmpty() || nama.isEmpty()) {
            UiUtil.kesalahan(this, "Kode dan nama barang wajib diisi.");
            return;
        }
        long harga;
        int stok;
        try {
            harga = Long.parseLong(txtHarga.getText().trim());
            stok = Integer.parseInt(txtStok.getText().trim());
        } catch (NumberFormatException ex) {
            UiUtil.kesalahan(this, "Harga dan stok harus berupa angka bulat.");
            return;
        }
        if (harga < 0 || stok < 0) {
            UiUtil.kesalahan(this, "Harga dan stok tidak boleh negatif.");
            return;
        }
        try {
            if (dao.kodeSudahDipakai(kode, produk.getId())) {
                UiUtil.kesalahan(this, "Kode barang sudah dipakai barang lain.");
                return;
            }
            produk.setKode(kode);
            produk.setNama(nama);
            produk.setKategori(txtKategori.getText().trim());
            produk.setHarga(harga);
            produk.setStok(stok);
            if (produk.getId() == 0) {
                dao.tambah(produk);
            } else {
                dao.ubah(produk);
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
