package com.library.gui;

import com.library.model.IssueRecord;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * DashboardPanel — Library Management System (Part C: GUI).
 *
 * <p>Screen 3: dashboard. Stat cards (total books, members, books issued,
 * overdue, total fines) plus a table of the most recent issues.</p>
 */
public class DashboardPanel extends JPanel {

    private final MainFrame parent;
    private final JPanel statsRow = new JPanel(new GridLayout(1, 5, 8, 8));
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Issue ID", "Book", "Member", "Issue Date", "Due Date", "Status"}, 0);
    private final JTable table = new JTable(tableModel);

    public DashboardPanel(MainFrame parent) {
        super(new BorderLayout(10, 10));
        this.parent = parent;
        setBackground(Theme.BG);

        add(Theme.label("Dashboard", Theme.HEADING_FONT, Theme.ACCENT),
                BorderLayout.NORTH);
        statsRow.setBackground(Theme.BG);
        add(statsRow, BorderLayout.CENTER);

        JPanel bottom = Theme.panel();
        bottom.setLayout(new BorderLayout(8, 8));
        bottom.add(Theme.label("Recent Issues", Theme.SUBHEADING_FONT, Theme.TEXT),
                BorderLayout.NORTH);
        Theme.styleTable(table);
        bottom.add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        refresh();
    }

    /** Recompute the stats cards and the recent-issues table. */
    public void refresh() {
        statsRow.removeAll();
        int totalBooks = parent.getBooks().size();
        int totalCopies = 0;
        for (com.library.model.Book b : parent.getBooks()) {
            totalCopies += b.getCopies();
        }
        int issued = 0;
        for (IssueRecord r : parent.getIssues()) {
            // returnDate != null means already returned (a late return is
            // marked "Overdue" by the service, so status alone is not enough).
            if (r.getReturnDate() == null
                    && ("Issued".equals(r.getStatus()) || "Overdue".equals(r.getStatus()))) {
                issued++;
            }
        }
        int overdue = parent.getOverdueRecords().size();
        statsRow.add(Theme.statCard("Total Books", String.valueOf(totalBooks)));
        statsRow.add(Theme.statCard("Total Copies", String.valueOf(totalCopies)));
        statsRow.add(Theme.statCard("Members", String.valueOf(parent.getMembers().size())));
        statsRow.add(Theme.statCard("Issued Now", String.valueOf(issued)));
        statsRow.add(Theme.statCard("Overdue", String.valueOf(overdue)));
        statsRow.add(Theme.statCard("Fines Due",
                "Rs " + String.format("%.0f", parent.getTotalFines())));
        statsRow.revalidate();
        statsRow.repaint();

        tableModel.setRowCount(0);
        List<IssueRecord> issues = parent.getIssues();
        int start = Math.max(0, issues.size() - 5);
        for (int i = issues.size() - 1; i >= start; i--) {
            IssueRecord r = issues.get(i);
            com.library.model.Book b = SampleData.findBookById(parent.getBooks(), r.getBookId());
            com.library.model.Member m = SampleData.findMemberById(parent.getMembers(), r.getMemberId());
            tableModel.addRow(new Object[]{
                    r.getId(),
                    b != null ? b.getTitle() : ("Book #" + r.getBookId()),
                    m != null ? m.getName() : ("Member #" + r.getMemberId()),
                    r.getIssueDate(), r.getDueDate(), r.getStatus()});
        }
    }
}
