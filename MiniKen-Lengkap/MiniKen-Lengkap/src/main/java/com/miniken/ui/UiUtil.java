package com.miniken.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Kumpulan bantuan tampilan yang dipakai bersama oleh semua panel. */
final class UiUtil {

    static final Color BIRU = new Color(0x0B5ED7);
    static final Color BIRU_TUA = new Color(0x083D8C);
    static final Color KUNING = new Color(0xFFC107);
    static final Color HIJAU = new Color(0x198754);
    static final Color MERAH = new Color(0xDC3545);
    static final Color ABU = new Color(0xF3F5F9);

    private UiUtil() {
    }

    static DefaultTableModel modelTabel(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    static void rapikanTabel(JTable tabel, int... kolomAngka) {
        tabel.setRowHeight(26);
        tabel.setFillsViewportHeight(true);
        tabel.getTableHeader().setFont(tabel.getTableHeader().getFont().deriveFont(Font.BOLD));
        tabel.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer kanan = new DefaultTableCellRenderer();
        kanan.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int k : kolomAngka) {
            tabel.getColumnModel().getColumn(k).setCellRenderer(kanan);
        }
    }

    static JButton tombol(String teks, Color warna) {
        JButton b = new JButton(teks);
        b.setBackground(warna);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        return b;
    }

    static void kesalahan(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "MiniKen", JOptionPane.WARNING_MESSAGE);
    }

    static void kesalahanDatabase(Component induk, Exception e) {
        JOptionPane.showMessageDialog(induk, "Terjadi kesalahan database:\n" + e.getMessage(),
                "MiniKen", JOptionPane.ERROR_MESSAGE);
    }

    static void info(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "MiniKen", JOptionPane.INFORMATION_MESSAGE);
    }
}
