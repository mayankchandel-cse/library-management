package com.library.gui;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

/**
 * ReportsPanel — Library Management System (Part C: GUI).
 *
 * <p>Screen 8: reports. "Generate Report" builds a text summary of the
 * library (books, members, issues, overdue, fines) and writes it to
 * {@code data/books_report.txt} with a {@link FileWriter} (file handling).
 * "Export CSV" writes the book catalogue to {@code data/books_export.csv}.
 * The report is also shown in the text area on screen.</p>
 */
public class ReportsPanel extends JPanel {

    private final MainFrame parent;
    private final JTextArea reportArea = new JTextArea();

    public ReportsPanel(MainFrame parent) {
        super(new BorderLayout(10, 10));
        this.parent = parent;
        setBackground(Theme.BG);

        add(Theme.label("Reports", Theme.HEADING_FONT, Theme.ACCENT),
                BorderLayout.NORTH);

        reportArea.setFont(Theme.BODY_FONT);
        reportArea.setBackground(Theme.PANEL);
        reportArea.setForeground(Theme.TEXT);
        reportArea.setEditable(false);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JPanel buttons = Theme.panel();
        buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton reportButton = Theme.styledButton("Generate Report");
        reportButton.addActionListener(e -> generateReport());
        JButton csvButton = Theme.secondaryButton("Export Books CSV");
        csvButton.addActionListener(e -> exportCsv());
        buttons.add(reportButton);
        buttons.add(csvButton);
        buttons.add(Theme.label("Files are written to the data/ folder.",
                Theme.BODY_FONT, Theme.MUTED));
        add(buttons, BorderLayout.SOUTH);
        refresh();
    }

    /** Build the report text shown on screen. */
    public String buildReportText() {
        StringBuilder sb = new StringBuilder();
        sb.append("LIBRARY MANAGEMENT SYSTEM — REPORT\n");
        sb.append("Generated: ").append(LocalDate.now()).append("\n");
        sb.append("========================================\n\n");
        sb.append("Books: ").append(parent.getBooks().size()).append("\n");
        int copies = 0, available = 0;
        for (Book b : parent.getBooks()) {
            copies += b.getCopies();
            available += b.getAvailableCopies();
        }
        sb.append("Total copies: ").append(copies)
                .append("  •  Available now: ").append(available).append("\n");
        sb.append("Members: ").append(parent.getMembers().size()).append("\n");
        int active = 0, returned = 0;
        for (IssueRecord r : parent.getIssues()) {
            if ("Returned".equals(r.getStatus())) {
                returned++;
            } else {
                active++;
            }
        }
        sb.append("Issues — active: ").append(active)
                .append("  •  returned: ").append(returned).append("\n");
        sb.append("Overdue now: ").append(parent.getOverdueRecords().size())
                .append("  •  Total fines due: Rs ")
                .append(String.format("%.0f", parent.getTotalFines())).append("\n\n");
        sb.append("BOOK CATALOGUE\n--------------\n");
        for (Book b : parent.getBooks()) {
            sb.append(String.format("#%-3d %-42s %-28s %d  (%d/%d available)%n",
                    b.getId(), b.getTitle(), b.getAuthor(), b.getYear(),
                    b.getAvailableCopies(), b.getCopies()));
        }
        sb.append("\nMEMBERS\n-------\n");
        for (Member m : parent.getMembers()) {
            sb.append(String.format("#%-3d %-22s %-26s %s%n",
                    m.getId(), m.getName(), m.getEmail(), m.getMembershipType()));
        }
        return sb.toString();
    }

    /** Generate the report and write it to data/books_report.txt. */
    private void generateReport() {
        String text = buildReportText();
        reportArea.setText(text);
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (PrintWriter out = new PrintWriter(
                new FileWriter(new File(dir, "books_report.txt")))) {
            out.print(text);
            JOptionPane.showMessageDialog(this,
                    "Report written to data/books_report.txt",
                    "Report", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not write the report: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Export the book catalogue as data/books_export.csv. */
    private void exportCsv() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (PrintWriter out = new PrintWriter(
                new FileWriter(new File(dir, "books_export.csv")))) {
            out.println("id,title,author,isbn,category,year,copies,available");
            for (Book b : parent.getBooks()) {
                out.printf("%d,\"%s\",\"%s\",%s,%s,%d,%d,%d%n",
                        b.getId(), b.getTitle().replace("\"", "\"\""),
                        b.getAuthor().replace("\"", "\"\""),
                        b.getIsbn(), b.getCategory(), b.getYear(),
                        b.getCopies(), b.getAvailableCopies());
            }
            JOptionPane.showMessageDialog(this,
                    "CSV written to data/books_export.csv",
                    "Export", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not write the CSV: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Show the current report text on screen. */
    public void refresh() {
        reportArea.setText(buildReportText());
    }
}
