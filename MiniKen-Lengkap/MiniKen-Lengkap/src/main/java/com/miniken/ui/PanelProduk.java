package com.miniken.ui;

import com.miniken.dao.ProdukDAO;
import com.miniken.model.Produk;
import com.miniken.util.Rupiah;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Halaman Data Barang: cari, tambah, ubah, hapus. */
public class PanelProduk extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ProdukDAO dao = new ProdukDAO();
    private final List<Produk> tampil = new ArrayList<>();
    private final DefaultTableModel model = UiUtil.modelTabel("Kode", "Nama", "Kategori", "Harga (Rp)", "Stok");
    private final JTable tabel = new JTable(model);
    private final JTextField txtCari = new JTextField(22);
    private final JLabel lblJumlah = new JLabel(" ");
    private final Runnable setelahUbah;

    /** @param setelahUbah dipanggil setelah data barang berubah (mis. untuk menyegarkan halaman lain) */
    public PanelProduk(Runnable setelahUbah) {
        this.setelahUbah = setelahUbah;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JButton btnCari = UiUtil.tombol("Cari", UiUtil.BIRU);
        JButton btnSemua = UiUtil.tombol("Tampilkan Semua", UiUtil.BIRU_TUA);
        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        atas.add(new JLabel("Cari nama / kode:"));
        atas.add(txtCari);
        atas.add(btnCari);
        atas.add(btnSemua);
        add(atas, BorderLayout.NORTH);

        UiUtil.rapikanTabel(tabel, 3, 4);
        tabel.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        // Stok menipis ditandai kuning
        DefaultTableCellRenderer stokRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean fok, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, fok, r, c);
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (!sel) {
                    boolean menipis = r < tampil.size() && tampil.get(t.convertRowIndexToModel(r)).isStokMenipis();
                    comp.setBackground(menipis ? new Color(0xFFF1CC) : Color.WHITE);
                }
                return comp;
            }
        };
        tabel.getColumnModel().getColumn(4).setCellRenderer(stokRenderer);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JButton btnTambah = UiUtil.tombol("+ Tambah Barang", UiUtil.HIJAU);
        JButton btnUbah = UiUtil.tombol("Ubah", UiUtil.BIRU);
        JButton btnHapus = UiUtil.tombol("Hapus", UiUtil.MERAH);
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        bawah.add(btnTambah);
        bawah.add(btnUbah);
        bawah.add(btnHapus);
        bawah.add(lblJumlah);
        add(bawah, BorderLayout.SOUTH);

        btnCari.addActionListener(e -> muat());
        txtCari.addActionListener(e -> muat());
        btnSemua.addActionListener(e -> {
            txtCari.setText("");
            muat();
        });
        btnTambah.addActionListener(e -> bukaForm(null));
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        tabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ubah();
                }
            }
        });

        muat();
    }

    /** Memuat ulang data dari database sesuai kata kunci pencarian. */
    public void muat() {
        try {
            List<Produk> data = dao.semua(txtCari.getText());
            tampil.clear();
            tampil.addAll(data);
            model.setRowCount(0);
            for (Produk p : data) {
                model.addRow(new Object[]{p.getKode(), p.getNama(), p.getKategori(),
                    Rupiah.angka(p.getHarga()), p.getStok()});
            }
            lblJumlah.setText("   " + data.size() + " barang");
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }

    private Produk terpilih() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            UiUtil.kesalahan(this, "Pilih satu barang pada tabel terlebih dahulu.");
            return null;
        }
        return tampil.get(tabel.convertRowIndexToModel(baris));
    }

    private void ubah() {
        Produk p = terpilih();
        if (p != null) {
            bukaForm(p);
        }
    }

    private void bukaForm(Produk p) {
        DialogProduk dlg = new DialogProduk(SwingUtilities.getWindowAncestor(this), p);
        dlg.setVisible(true);
        if (dlg.isDisimpan()) {
            muat();
            setelahUbah.run();
        }
    }

    private void hapus() {
        Produk p = terpilih();
        if (p == null) {
            return;
        }
        int jawab = JOptionPane.showConfirmDialog(this, "Hapus barang '" + p.getNama() + "'?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.hapus(p.getId());
            muat();
            setelahUbah.run();
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }
}
