package com.miniken.ui;

import com.miniken.dao.TransaksiDAO;
import com.miniken.model.Transaksi;
import com.miniken.util.Rupiah;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/** Halaman Riwayat: 100 transaksi terbaru dan akses ke struk. */
public class PanelRiwayat extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter WAKTU = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final TransaksiDAO dao = new TransaksiDAO();
    private final List<Transaksi> data = new ArrayList<>();
    private final DefaultTableModel model = UiUtil.modelTabel(
            "No. Transaksi", "Waktu", "Kasir", "Total (Rp)", "Bayar (Rp)", "Kembali (Rp)");
    private final JTable tabel = new JTable(model);
    private final JLabel lblTotal = new JLabel(" ");
    private final String filterKasir;

    /** @param filterKasir username kasir untuk membatasi daftar, atau null untuk menampilkan semua */
    public PanelRiwayat(String filterKasir) {
        this.filterKasir = filterKasir;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel judul = new JLabel(filterKasir == null
                ? "Riwayat Transaksi (100 terbaru)" : "Riwayat Transaksi Saya (100 terbaru)");
        judul.setFont(judul.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        JPanel atas = new JPanel(new BorderLayout());
        atas.add(judul, BorderLayout.WEST);
        atas.add(lblTotal, BorderLayout.EAST);
        add(atas, BorderLayout.NORTH);

        UiUtil.rapikanTabel(tabel, 3, 4, 5);
        tabel.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JButton btnStruk = UiUtil.tombol("Lihat Struk", UiUtil.BIRU);
        JButton btnMuat = UiUtil.tombol("Segarkan", UiUtil.BIRU_TUA);
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        bawah.add(btnStruk);
        bawah.add(btnMuat);
        add(bawah, BorderLayout.SOUTH);

        btnStruk.addActionListener(e -> lihatStruk());
        btnMuat.addActionListener(e -> muat());
        tabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    lihatStruk();
                }
            }
        });

        muat();
    }

    public void muat() {
        try {
            List<Transaksi> hasil = dao.riwayat(filterKasir);
            data.clear();
            data.addAll(hasil);
            model.setRowCount(0);
            long total = 0;
            for (Transaksi t : hasil) {
                total += t.getTotal();
                model.addRow(new Object[]{t.getNoTransaksi(), t.getWaktu().format(WAKTU), t.getKasir(),
                    Rupiah.angka(t.getTotal()), Rupiah.angka(t.getBayar()), Rupiah.angka(t.getKembali())});
            }
            lblTotal.setText("Total: " + Rupiah.format(total));
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }

    private void lihatStruk() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            UiUtil.kesalahan(this, "Pilih satu transaksi pada tabel terlebih dahulu.");
            return;
        }
        Transaksi t = data.get(tabel.convertRowIndexToModel(baris));
        try {
            dao.muatDetail(t);
            new DialogStruk(SwingUtilities.getWindowAncestor(this), t).setVisible(true);
        } catch (SQLException ex) {
            UiUtil.kesalahanDatabase(this, ex);
        }
    }
}
