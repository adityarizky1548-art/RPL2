package com.miniken.ui;

import com.miniken.dao.LaporanDAO;
import com.miniken.util.Rupiah;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Laporan penjualan per rentang tanggal: ringkasan, penjualan harian, barang terlaris, ekspor CSV. */
public class PanelLaporan extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LaporanDAO dao = new LaporanDAO();
    private final List<Object[]> baris = new ArrayList<>();

    private final JTextField txtDari = new JTextField(9);
    private final JTextField txtSampai = new JTextField(9);
    private final JLabel lblTransaksi = nilai();
    private final JLabel lblOmzet = nilai();
    private final JLabel lblBarang = nilai();
    private final DefaultTableModel modelHarian = UiUtil.modelTabel("Tanggal", "Jumlah Transaksi", "Omzet (Rp)");
    private final DefaultTableModel modelTerlaris = UiUtil.modelTabel("Barang", "Terjual", "Omzet (Rp)");

    public PanelLaporan() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        LocalDate hariIni = LocalDate.now();
        txtDari.setText(hariIni.withDayOfMonth(1).toString());
        txtSampai.setText(hariIni.toString());

        JButton btnTampil = UiUtil.tombol("Tampilkan", UiUtil.BIRU);
        JButton btnHariIni = UiUtil.tombol("Hari Ini", UiUtil.BIRU_TUA);
        JButton btnBulanIni = UiUtil.tombol("Bulan Ini", UiUtil.BIRU_TUA);
        JButton btnCsv = UiUtil.tombol("Ekspor CSV", UiUtil.HIJAU);
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        filter.add(new JLabel("Dari (yyyy-MM-dd):"));
        filter.add(txtDari);
        filter.add(new JLabel("Sampai:"));
        filter.add(txtSampai);
        filter.add(btnTampil);
        filter.add(btnHariIni);
        filter.add(btnBulanIni);
        filter.add(btnCsv);

        JPanel ringkas = new JPanel(new GridLayout(1, 3, 10, 0));
        ringkas.add(kartu("Jumlah Transaksi", lblTransaksi));
        ringkas.add(kartu("Total Omzet", lblOmzet));
        ringkas.add(kartu("Barang Terjual", lblBarang));

        JPanel atas = new JPanel(new BorderLayout(0, 8));
        atas.add(filter, BorderLayout.NORTH);
        atas.add(ringkas, BorderLayout.CENTER);
        add(atas, BorderLayout.NORTH);

        JTable tabelHarian = new JTable(modelHarian);
        UiUtil.rapikanTabel(tabelHarian, 1, 2);
        JScrollPane spHarian = new JScrollPane(tabelHarian);
        spHarian.setBorder(BorderFactory.createTitledBorder("Penjualan Harian"));

        JTable tabelTerlaris = new JTable(modelTerlaris);
        UiUtil.rapikanTabel(tabelTerlaris, 1, 2);
        JScrollPane spTerlaris = new JScrollPane(tabelTerlaris);
        spTerlaris.setBorder(BorderFactory.createTitledBorder("10 Barang Terlaris"));

        JPanel tengah = new JPanel(new GridLayout(1, 2, 10, 0));
        tengah.add(spHarian);
        tengah.add(spTerlaris);
        add(tengah, BorderLayout.CENTER);

        btnTampil.addActionListener(e -> muat());
        btnHariIni.addActionListener(e -> {
            txtDari.setText(LocalDate.now().toString());
            txtSampai.setText(LocalDate.now().toString());
            muat();
        });
        btnBulanIni.addActionListener(e -> {
            txtDari.setText(LocalDate.now().withDayOfMonth(1).toString());
            txtSampai.setText(LocalDate.now().toString());
            muat();
        });
        btnCsv.addActionListener(e -> ekspor());

        muat();
    }

    private static JLabel nilai() {
        JLabel l = new JLabel("-", JLabel.CENTER);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 22f));
        return l;
    }

    private static JPanel kartu(String judul, JLabel isi) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(0xD9DEE8)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        JLabel j = new JLabel(judul, JLabel.CENTER);
        j.setForeground(java.awt.Color.GRAY);
        p.add(j, BorderLayout.NORTH);
        p.add(isi, BorderLayout.CENTER);
        return p;
    }

    /** Membaca rentang tanggal dari isian; null bila tidak valid (pesan sudah ditampilkan). */
    private LocalDate[] bacaRentang() {
        try {
            LocalDate dari = LocalDate.parse(txtDari.getText().trim());
            LocalDate sampai = LocalDate.parse(txtSampai.getText().trim());
            if (dari.isAfter(sampai)) {
                UiUtil.kesalahan(this, "Tanggal 'Dari' tidak boleh setelah tanggal 'Sampai'.");
                return null;
            }
            return new LocalDate[]{dari, sampai};
        } catch (DateTimeParseException ex) {
            UiUtil.kesalahan(this, "Format tanggal harus yyyy-MM-dd, contoh 2026-10-26.");
            return null;
        }
    }

    public void muat() {
        LocalDate[] r = bacaRentang();
        if (r == null) {
            return;
        }
        try {
            long[] ringkas = dao.ringkasan(r[0], r[1]);
            lblTransaksi.setText(String.valueOf(ringkas[0]));
            lblOmzet.setText(Rupiah.format(ringkas[1]));
            lblBarang.setText(String.valueOf(ringkas[2]));

            baris.clear();
            baris.addAll(dao.penjualanHarian(r[0], r[1]));
            modelHarian.setRowCount(0);
            for (Object[] b : baris) {
                modelHarian.addRow(new Object[]{((LocalDate) b[0]).format(TANGGAL), b[1], Rupiah.angka((Long) b[2])});
            }

            modelTerlaris.setRowCount(0);
            for (Object[] b : dao.terlaris(r[0], r[1])) {
                modelTerlaris.addRow(new Object[]{b[0], b[1], Rupiah.angka((Long) b[2])});
            }
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }

    private void ekspor() {
        if (baris.isEmpty()) {
            UiUtil.kesalahan(this, "Tidak ada data penjualan harian untuk diekspor. Klik Tampilkan dulu.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("penjualan-harian.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File berkas = fc.getSelectedFile();
        try (BufferedWriter w = Files.newBufferedWriter(berkas.toPath(), StandardCharsets.UTF_8)) {
            w.write("Tanggal,Jumlah Transaksi,Omzet");
            w.newLine();
            for (Object[] b : baris) {
                w.write(b[0] + "," + b[1] + "," + b[2]);
                w.newLine();
            }
            UiUtil.info(this, "Laporan disimpan di:\n" + berkas.getAbsolutePath());
        } catch (IOException ex) {
            UiUtil.kesalahan(this, "Gagal menyimpan berkas: " + ex.getMessage());
        }
    }
}
