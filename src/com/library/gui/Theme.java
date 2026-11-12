package com.library.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Theme — Library Management System (Part C: GUI).
 *
 * <p>Dark teal/blue library theme: deep navy background, warm amber accent
 * (the "library lamp" colour), white text. Every panel uses these helpers
 * so the whole application looks consistent.</p>
 */
public final class Theme {

    /** Deep navy background. */
    public static final Color BG = new Color(0x14, 0x21, 0x3D);       // #14213D
    /** Slightly lighter panel background. */
    public static final Color PANEL = new Color(0x1B, 0x2A, 0x4A);    // #1B2A4A
    /** Card background (a touch lighter still). */
    public static final Color CARD = new Color(0x23, 0x36, 0x5E);     // #23365E
    /** Amber accent — buttons, headings, highlights. */
    public static final Color ACCENT = new Color(0xFC, 0xA3, 0x11);   // #FCA311
    /** Main text colour. */
    public static final Color TEXT = Color.WHITE;
    /** Muted / secondary text. */
    public static final Color MUTED = new Color(0xC8, 0xD0, 0xE0);
    /** Danger (delete / overdue). */
    public static final Color DANGER = new Color(0xE5, 0x38, 0x3B);
    /** Success (returned / available). */
    public static final Color SUCCESS = new Color(0x2E, 0xCC, 0x71);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 26);
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font SUBHEADING_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font TABLE_FONT = new Font("SansSerif", Font.PLAIN, 13);

    private Theme() {
    }

    /** A dark panel with the standard background. */
    public static JPanel panel() {
        JPanel p = new JPanel();
        p.setBackground(PANEL);
        return p;
    }

    /** A label in the given font and colour (white by default). */
    public static JLabel label(String text) {
        return label(text, BODY_FONT, TEXT);
    }

    /** A label in the given font and colour. */
    public static JLabel label(String text, Font font, Color colour) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(colour);
        return l;
    }

    /** Amber primary button (dark text on amber). */
    public static JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(ACCENT);
        b.setForeground(BG);
        b.setFont(SUBHEADING_FONT);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 38));
        return b;
    }

    /** Grey secondary button (white text on card colour). */
    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(CARD);
        b.setForeground(TEXT);
        b.setFont(BODY_FONT);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 38));
        return b;
    }

    /** Dark text field with a light border. */
    public static JTextField styledTextField(int columns) {
        JTextField f = new JTextField(columns);
        f.setFont(BODY_FONT);
        f.setBackground(new Color(0x2A, 0x3D, 0x66));
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createLineBorder(ACCENT, 1));
        return f;
    }

    /** A table styled for the dark theme (amber header, dark rows). */
    public static void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setBackground(PANEL);
        table.setForeground(TEXT);
        table.setRowHeight(26);
        table.setGridColor(CARD);
        table.getTableHeader().setBackground(ACCENT);
        table.getTableHeader().setForeground(BG);
        table.getTableHeader().setFont(SUBHEADING_FONT);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(BG);
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setBackground(PANEL);
        r.setForeground(TEXT);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(r);
        }
    }

    /** A small stat card: amber title, big white value. */
    public static JPanel statCard(String title, String value) {
        JPanel card = new JPanel();
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createLineBorder(ACCENT, 1));
        card.setLayout(new java.awt.BorderLayout(4, 4));
        JLabel t = label(title, SUBHEADING_FONT, ACCENT);
        JLabel v = label(value, TITLE_FONT, TEXT);
        t.setHorizontalAlignment(JLabel.CENTER);
        v.setHorizontalAlignment(JLabel.CENTER);
        card.add(t, java.awt.BorderLayout.NORTH);
        card.add(v, java.awt.BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(170, 80));
        return card;
    }
}
