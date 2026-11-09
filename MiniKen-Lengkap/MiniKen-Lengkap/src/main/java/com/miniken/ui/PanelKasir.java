package com.miniken.ui;

import com.miniken.dao.ProdukDAO;
import com.miniken.dao.TransaksiDAO;
import com.miniken.model.ItemKeranjang;
import com.miniken.model.Produk;
import com.miniken.model.Transaksi;
import com.miniken.util.Rupiah;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/** Halaman Kasir: input kode barang, keranjang, pembayaran tunai, struk. */
public class PanelKasir extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ProdukDAO produkDAO = new ProdukDAO();
    private final TransaksiDAO transaksiDAO = new TransaksiDAO();
    private final Map<Integer, ItemKeranjang> keranjang = new LinkedHashMap<>();
    private final List<ItemKeranjang> urutan = new ArrayList<>();
    private final Runnable setelahTransaksi;

    private final JTextField txtKode = new JTextField(18);
    private final JSpinner spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final DefaultTableModel model = UiUtil.modelTabel("Kode", "Nama Barang", "Harga", "Jumlah", "Subtotal");
    private final JTable tabel = new JTable(model);
    private final JLabel lblTotal = new JLabel("Rp 0", JLabel.RIGHT);
    private final JTextField txtBayar = new JTextField();
    private final JLabel lblKembali = new JLabel("Rp 0", JLabel.RIGHT);
    private final JLabel lblJumlah = new JLabel(" ");

    /** @param setelahTransaksi dipanggil setelah pembayaran berhasil (stok berubah) */
    public PanelKasir(Runnable setelahTransaksi) {
        this.setelahTransaksi = setelahTransaksi;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buatBagianKiri(), BorderLayout.CENTER);
        add(buatBagianKanan(), BorderLayout.EAST);
        pasangPintasan();
        segarkan();
    }

    private JPanel buatBagianKiri() {
        JButton btnTambah = UiUtil.tombol("Tambah", UiUtil.BIRU);
        JPanel input = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        input.setBorder(BorderFactory.createTitledBorder("Input Barang  (F2)"));
        input.add(new JLabel("Kode barang:"));
        input.add(txtKode);
        input.add(new JLabel("Jumlah:"));
        input.add(spnQty);
        input.add(btnTambah);

        UiUtil.rapikanTabel(tabel, 2, 3, 4);
        tabel.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setBorder(BorderFactory.createTitledBorder("Keranjang"));

        JButton btnUbah = UiUtil.tombol("Ubah Jumlah", UiUtil.BIRU_TUA);
        JButton btnHapus = UiUtil.tombol("Hapus Item", UiUtil.MERAH);
        JPanel aksi = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        aksi.add(btnUbah);
        aksi.add(btnHapus);
        aksi.add(lblJumlah);

        JPanel kiri = new JPanel(new BorderLayout(0, 8));
        kiri.add(input, BorderLayout.NORTH);
        kiri.add(scroll, BorderLayout.CENTER);
        kiri.add(aksi, BorderLayout.SOUTH);

        btnTambah.addActionListener(e -> tambah());
        txtKode.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubahJumlah());
        btnHapus.addActionListener(e -> hapusItem());
        return kiri;
    }

    private JPanel buatBagianKanan() {
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 30f));
        lblTotal.setForeground(Color.WHITE);
        JLabel judulTotal = new JLabel("TOTAL BELANJA", JLabel.RIGHT);
        judulTotal.setForeground(new Color(0xDBE7FF));
        JPanel kotakTotal = new JPanel(new GridLayout(2, 1));
        kotakTotal.setBackground(UiUtil.BIRU_TUA);
        kotakTotal.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        kotakTotal.add(judulTotal);
        kotakTotal.add(lblTotal);

        txtBayar.setFont(txtBayar.getFont().deriveFont(Font.BOLD, 20f));
        txtBayar.setHorizontalAlignment(JTextField.RIGHT);
        lblKembali.setFont(lblKembali.getFont().deriveFont(Font.BOLD, 22f));
        lblKembali.setForeground(UiUtil.HIJAU);

        JPanel bayarPanel = new JPanel(new GridLayout(4, 1, 0, 4));
        bayarPanel.add(new JLabel("Uang diterima (Rp)"));
        bayarPanel.add(txtBayar);
        bayarPanel.add(new JLabel("Kembalian"));
        bayarPanel.add(lblKembali);

        JButton btnBayar = UiUtil.tombol("BAYAR  (F9)", UiUtil.HIJAU);
        btnBayar.setFont(btnBayar.getFont().deriveFont(Font.BOLD, 18f));
        btnBayar.setPreferredSize(new Dimension(100, 50));
        JButton btnBatal = UiUtil.tombol("Batalkan Transaksi", UiUtil.MERAH);
        JPanel tombol = new JPanel(new GridLayout(2, 1, 0, 8));
        tombol.add(btnBayar);
        tombol.add(btnBatal);

        JPanel kanan = new JPanel(new BorderLayout(0, 14));
        kanan.setPreferredSize(new Dimension(320, 100));
        kanan.add(kotakTotal, BorderLayout.NORTH);
        kanan.add(bayarPanel, BorderLayout.CENTER);
        kanan.add(tombol, BorderLayout.SOUTH);

        txtBayar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { hitungKembalian(); }
            @Override public void removeUpdate(DocumentEvent e) { hitungKembalian(); }
            @Override public void changedUpdate(DocumentEvent e) { hitungKembalian(); }
        });
        txtBayar.addActionListener(e -> bayar());
        btnBayar.addActionListener(e -> bayar());
        btnBatal.addActionListener(e -> batal());
        return kanan;
    }

    private void pasangPintasan() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F2"), "fokusKode");
        getActionMap().put("fokusKode", new javax.swing.AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                txtKode.requestFocusInWindow();
                txtKode.selectAll();
            }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F9"), "bayar");
        getActionMap().put("bayar", new javax.swing.AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                bayar();
            }
        });
    }

    /** Fokus ke kolom kode barang (dipanggil saat tab Kasir dibuka). */
    public void fokusKode() {
        SwingUtilities.invokeLater(() -> txtKode.requestFocusInWindow());
    }

    private void tambah() {
        String kode = txtKode.getText().trim();
        if (kode.isEmpty()) {
            UiUtil.kesalahan(this, "Kode barang belum diisi.");
            return;
        }
        int qty = (Integer) spnQty.getValue();
        try {
            Produk p = produkDAO.cariByKode(kode);
            if (p == null) {
                UiUtil.kesalahan(this, "Barang dengan kode '" + kode + "' tidak ditemukan.");
                txtKode.selectAll();
                return;
            }
            ItemKeranjang ada = keranjang.get(p.getId());
            int qtyBaru = (ada == null ? 0 : ada.getQty()) + qty;
            if (qtyBaru > p.getStok()) {
                UiUtil.kesalahan(this, "Stok " + p.getNama() + " tidak cukup (tersisa " + p.getStok() + ").");
                return;
            }
            if (ada == null) {
                keranjang.put(p.getId(), new ItemKeranjang(p, qty));
            } else {
                ada.setQty(qtyBaru);
            }
            txtKode.setText("");
            spnQty.setValue(1);
            segarkan();
            txtKode.requestFocusInWindow();
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }

    private ItemKeranjang terpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            UiUtil.kesalahan(this, "Pilih satu barang pada keranjang terlebih dahulu.");
            return null;
        }
        return urutan.get(tabel.convertRowIndexToModel(baris));
    }

    private void ubahJumlah() {
        ItemKeranjang item = terpilih();
        if (item == null) {
            return;
        }
        String masukan = JOptionPane.showInputDialog(this,
                "Jumlah baru untuk " + item.getNama() + " (0 untuk menghapus):", item.getQty());
        if (masukan == null) {
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(masukan.trim());
        } catch (NumberFormatException ex) {
            UiUtil.kesalahan(this, "Jumlah harus berupa angka bulat.");
            return;
        }
        if (qty < 1) {
            keranjang.remove(item.getProdukId());
        } else {
            try {
                Produk p = produkDAO.cariById(item.getProdukId());
                if (p == null || qty > p.getStok()) {
                    UiUtil.kesalahan(this, "Stok tidak cukup (tersisa " + (p == null ? 0 : p.getStok()) + ").");
                    return;
                }
                item.setQty(qty);
            } catch (SQLException ex) {
                UiUtil.kesalahanDatabase(this, ex);
                return;
            }
        }
        segarkan();
    }

    private void hapusItem() {
        ItemKeranjang item = terpilih();
        if (item != null) {
            keranjang.remove(item.getProdukId());
            segarkan();
        }
    }

    private void batal() {
        if (keranjang.isEmpty()) {
            return;
        }
        int jawab = JOptionPane.showConfirmDialog(this, "Batalkan transaksi dan kosongkan keranjang?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (jawab == JOptionPane.YES_OPTION) {
            kosongkan();
        }
    }

    private void kosongkan() {
        keranjang.clear();
        txtBayar.setText("");
        segarkan();
        txtKode.requestFocusInWindow();
    }

    private long total() {
        long total = 0;
        for (ItemKeranjang i : keranjang.values()) {
            total += i.getSubtotal();
        }
        return total;
    }

    private void segarkan() {
        urutan.clear();
        urutan.addAll(keranjang.values());
        model.setRowCount(0);
        int jumlah = 0;
        for (ItemKeranjang i : urutan) {
            jumlah += i.getQty();
            model.addRow(new Object[]{i.getKode(), i.getNama(), Rupiah.angka(i.getHarga()),
                i.getQty(), Rupiah.angka(i.getSubtotal())});
        }
        lblTotal.setText(Rupiah.format(total()));
        lblJumlah.setText("   " + jumlah + " barang");
        hitungKembalian();
    }

    private void hitungKembalian() {
        long bayar = Rupiah.baca(txtBayar.getText());
        long kembali = bayar - total();
        if (bayar < 0 || kembali < 0) {
            lblKembali.setText("-");
        } else {
            lblKembali.setText(Rupiah.format(kembali));
        }
    }

    private void bayar() {
        if (keranjang.isEmpty()) {
            UiUtil.kesalahan(this, "Keranjang masih kosong.");
            return;
        }
        long bayar = Rupiah.baca(txtBayar.getText());
        if (bayar < 0) {
            UiUtil.kesalahan(this, "Isi uang diterima dengan angka.");
            txtBayar.requestFocusInWindow();
            return;
        }
        if (bayar < total()) {
            UiUtil.kesalahan(this, "Uang diterima kurang dari total belanja.");
            txtBayar.requestFocusInWindow();
            return;
        }
        try {
            Transaksi t = transaksiDAO.simpan(new ArrayList<>(keranjang.values()), bayar, com.miniken.Sesi.username());
            kosongkan();
            setelahTransaksi.run();
            new DialogStruk(SwingUtilities.getWindowAncestor(this), t).setVisible(true);
        } catch (SQLException ex) {
            UiUtil.kesalahan(this, "Transaksi gagal: " + ex.getMessage());
        }
    }
}
