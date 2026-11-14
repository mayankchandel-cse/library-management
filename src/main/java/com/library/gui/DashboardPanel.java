package com.library.gui;

import com.library.service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Map;

/**
 * DashboardPanel - the first screen of the application.
 *
 * <p>Shows live stat cards from {@link LibraryService#getStats()} and a
 * short "how to use" guide listing the five sidebar sections. It shows
 * only real data from the database - no fake or sample numbers.</p>
 */
public class DashboardPanel extends JPanel {

    private final LibraryService service;
    private final JPanel cardsPanel;

    public DashboardPanel(LibraryService service) {
        this.service = service;
        setBackground(Theme.PANEL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(Theme.label("Dashboard", Theme.TITLE_FONT, Theme.ACCENT), BorderLayout.NORTH);

        cardsPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        cardsPanel.setBackground(Theme.PANEL);

        JPanel centre = new JPanel();
        centre.setBackground(Theme.PANEL);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.add(cardsPanel);

        JTextArea howTo = new JTextArea(
                "How to use this system\n\n"
                + "1. Books - search books by title or author, add a new book,\n"
                + "   edit a book, or delete a book.\n"
                + "2. Members - register a new member and click a member row\n"
                + "   to see that member's borrowing history.\n"
                + "3. Issue / Return - issue a book to a member (due in 14 days)\n"
                + "   or return a book by its issue ID and see the fine, if any.\n"
                + "4. Overdue & Fines - see all overdue books, days late and\n"
                + "   the fine for each one. Fine rule: Rs 5 per day late.\n"
                + "5. Dashboard - this screen. It shows the live counts from\n"
                + "   the database. Use Refresh in each section after changes.");
        howTo.setFont(Theme.BODY_FONT);
        howTo.setForeground(Theme.TEXT);
        howTo.setBackground(Theme.CARD);
        howTo.setEditable(false);
        howTo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel howToWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        howToWrap.setBackground(Theme.PANEL);
        howToWrap.add(new JScrollPane(howTo));
        centre.add(howToWrap);

        add(centre, BorderLayout.CENTER);

        refresh();
    }

    /** Reloads the stat cards from the database. */
    public void refresh() {
        cardsPanel.removeAll();
        Map<String, Object> stats = service.getStats();
        cardsPanel.add(Theme.statCard("Total Books", value(stats, "totalBooks")));
        cardsPanel.add(Theme.statCard("Total Members", value(stats, "totalMembers")));
        cardsPanel.add(Theme.statCard("Issued Now", value(stats, "issued")));
        cardsPanel.add(Theme.statCard("Overdue", value(stats, "overdue")));
        cardsPanel.add(Theme.statCard("Fines Due Rs", value(stats, "totalFines")));
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private static String value(Map<String, Object> stats, String key) {
        if (stats == null || stats.get(key) == null) {
            return "0";
        }
        return String.valueOf(stats.get(key));
    }
}
