package com.library.gui;

import com.library.model.IssueRecord;
import com.library.service.LibraryService;

import javax.swing.BorderFactory;
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
import java.util.List;

/**
 * OverduePanel - all overdue issues with days late and fine.
 *
 * <p>Reads {@link LibraryService#getOverdueRecords()}. Days late is the
 * number of days between the due date and today, and the fine is
 * Rs 5 per day late. No background thread and no reminders.</p>
 */
public class OverduePanel extends JPanel {

    private static final String[] COLUMNS = {
            "Issue ID", "Book", "Member", "Due Date", "Days Late", "Fine Rs"
    };

    private final LibraryService service;
    private final DefaultTableModel model;
    private final JTable table;

    public OverduePanel(LibraryService service) {
        this.service = service;
        setBackground(Theme.PANEL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(Theme.label("Overdue & Fines", Theme.TITLE_FONT, Theme.ACCENT), BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        Theme.styleTable(table);

        JPanel centre = new JPanel(new BorderLayout(10, 10));
        centre.setBackground(Theme.PANEL);
        centre.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Theme.PANEL);
        JLabel footer = Theme.label("Fine rule: Rs 5 per day late.",
                Theme.SUBHEADING_FONT, Theme.ACCENT);
        bottom.add(footer, BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Theme.PANEL);
        JButton refreshButton = Theme.styledButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        buttons.add(refreshButton);
        bottom.add(buttons, BorderLayout.EAST);
        centre.add(bottom, BorderLayout.SOUTH);

        add(centre, BorderLayout.CENTER);
        refresh();
    }

    /** Reloads the overdue list from the database. */
    public void refresh() {
        model.setRowCount(0);
        LocalDate today = LocalDate.now();
        List<IssueRecord> overdue = service.getOverdueRecords();
        for (IssueRecord r : overdue) {
            long daysLate = 0;
            if (r.getDueDate() != null) {
                daysLate = ChronoUnit.DAYS.between(r.getDueDate(), today);
                if (daysLate < 0) {
                    daysLate = 0;
                }
            }
            model.addRow(new Object[]{
                    r.getId(), r.getBookTitle(), r.getMemberName(),
                    r.getDueDate(), daysLate, r.getFine()
            });
        }
    }
}
