package com.miniken.ui;

import com.miniken.model.Transaksi;
import com.miniken.util.StrukBuilder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.print.PrinterException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Menampilkan struk; bisa dicetak lewat dialog cetak sistem. */
public class DialogStruk extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JTextArea area = new JTextArea();

    public DialogStruk(Window induk, Transaksi t) {
        super(induk, "Struk " + t.getNoTransaksi(), ModalityType.APPLICATION_MODAL);

        area.setText(StrukBuilder.buat(t));
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBackground(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        area.setCaretPosition(0);

        JButton cetak = UiUtil.tombol("Cetak", UiUtil.BIRU);
        cetak.addActionListener(e -> cetak());
        JButton tutup = UiUtil.tombol("Tutup", UiUtil.HIJAU);
        tutup.addActionListener(e -> dispose());

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        bawah.add(cetak);
        bawah.add(tutup);

        setLayout(new BorderLayout());
        add(new JScrollPane(area), BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);
        setSize(380, 520);
        setLocationRelativeTo(induk);
        getRootPane().setDefaultButton(tutup);
    }

    private void cetak() {
        try {
            area.print();
        } catch (PrinterException ex) {
            UiUtil.kesalahan(this, "Struk gagal dicetak: " + ex.getMessage());
        }
    }
}
