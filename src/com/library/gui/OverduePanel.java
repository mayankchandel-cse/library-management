package com.library.gui;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * OverduePanel — Library Management System (Part C: GUI).
 *
 * <p>Screen 7: overdue books and fines. Lists every record past its due
 * date with days late and fine (Rs 5/day). A background reminder thread
 * (daemon) re-checks the overdue list every 30 seconds and updates the
 * status label — it never blocks the Swing event-dispatch thread, and
 * all table updates go through {@link javax.swing.SwingUtilities#invokeLater}.</p>
 */
public class OverduePanel extends JPanel {

    private final MainFrame parent;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Issue ID", "Book", "Member", "Due Date",
                    "Days Late", "Fine (Rs)"}, 0);
    private final JTable table = new JTable(tableModel);
    private final JLabel statusLabel = Theme.label(
            "Reminder thread: running (checks every 30 s)",
            Theme.BODY_FONT, Theme.SUCCESS);
    private final JLabel totalLabel = Theme.label("Total fines due: Rs 0",
            Theme.SUBHEADING_FONT, Theme.ACCENT);

    public OverduePanel(MainFrame parent) {
        super(new BorderLayout(10, 10));
        this.parent = parent;
        setBackground(Theme.BG);

        add(Theme.label("Overdue Books & Fines", Theme.HEADING_FONT, Theme.ACCENT),
                BorderLayout.NORTH);
        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = Theme.panel();
        bottom.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton refreshButton = Theme.styledButton("Refresh");
        refreshButton.setPreferredSize(new java.awt.Dimension(110, 36));
        refreshButton.addActionListener(e -> refresh());
        bottom.add(refreshButton);
        bottom.add(totalLabel);
        bottom.add(statusLabel);
        bottom.add(Theme.label("Fine rule: Rs 5 per day late.",
                Theme.BODY_FONT, Theme.MUTED));
        add(bottom, BorderLayout.SOUTH);

        startReminderThread();
        refresh();
    }

    /** Daemon thread: re-checks overdue records every 30 s (EDT-safe). */
    private void startReminderThread() {
        Thread reminder = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(30_000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                javax.swing.SwingUtilities.invokeLater(() -> {
                    refresh();
                    statusLabel.setText("Reminder thread: running — last check "
                            + java.time.LocalTime.now().withNano(0));
                });
            }
        }, "OverdueReminder");
        reminder.setDaemon(true);
        reminder.start();
    }

    /** Rebuild the overdue table and the total-fines label. */
    public void refresh() {
        tableModel.setRowCount(0);
        LocalDate today = LocalDate.now();
        for (IssueRecord r : parent.getOverdueRecords()) {
            Book b = SampleData.findBookById(parent.getBooks(), r.getBookId());
            Member m = SampleData.findMemberById(parent.getMembers(), r.getMemberId());
            long daysLate = ChronoUnit.DAYS.between(r.getDueDate(), today);
            tableModel.addRow(new Object[]{
                    r.getId(),
                    b != null ? b.getTitle() : ("Book #" + r.getBookId()),
                    m != null ? m.getName() : ("Member #" + r.getMemberId()),
                    r.getDueDate(), daysLate,
                    String.format("%.0f", r.getFine())});
        }
        totalLabel.setText("Total fines due: Rs "
                + String.format("%.0f", parent.getTotalFines()));
        if (tableModel.getRowCount() == 0) {
            statusLabel.setText("Reminder thread: running — no overdue books right now");
        }
    }
}
