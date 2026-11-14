package com.library.gui;

import com.library.service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * MainFrame - the main window of the Library Management System.
 *
 * <p>Left sidebar with five sections (Dashboard, Books, Members,
 * Issue / Return, Overdue &amp; Fines) plus Exit, switched with a
 * CardLayout. There is no login screen - the application opens
 * straight into the Dashboard. The GUI never creates a
 * {@link LibraryService} itself; it receives the Spring bean from
 * {@code LibraryApplication}.</p>
 */
public class MainFrame extends JFrame {

    private final CardLayout cards;
    private final JPanel cardPanel;
    private final DashboardPanel dashboardPanel;
    private final BooksPanel booksPanel;
    private final MembersPanel membersPanel;
    private final IssuePanel issuePanel;
    private final OverduePanel overduePanel;

    public MainFrame(LibraryService service) {
        super("Library Management System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- Cards (created first: the top bar and sidebar buttons use them) ---
        cards = new CardLayout();
        cardPanel = new JPanel(cards);
        cardPanel.setBackground(Theme.PANEL);
        dashboardPanel = new DashboardPanel(service);
        booksPanel = new BooksPanel(service);
        membersPanel = new MembersPanel(service);
        issuePanel = new IssuePanel(service);
        overduePanel = new OverduePanel(service);
        cardPanel.add(dashboardPanel, "DASHBOARD");
        cardPanel.add(booksPanel, "BOOKS");
        cardPanel.add(membersPanel, "MEMBERS");
        cardPanel.add(issuePanel, "ISSUE");
        cardPanel.add(overduePanel, "OVERDUE");

        // --- Top bar ---
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setBackground(Theme.BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel welcome = Theme.label("Welcome  -  Library Management System",
                Theme.HEADING_FONT, Theme.ACCENT);
        topBar.add(welcome, BorderLayout.WEST);
        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchWrap.setBackground(Theme.BG);
        searchWrap.add(Theme.label("Search books: "));
        JTextField searchField = Theme.styledTextField(20);
        searchWrap.add(searchField);
        JButton searchButton = Theme.styledButton("Search");
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            booksPanel.search(keyword);
            show("BOOKS");
        });
        searchWrap.add(searchButton);
        topBar.add(searchWrap, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // --- Sidebar ---
        JPanel sidebar = new JPanel(new GridLayout(6, 1, 0, 8));
        sidebar.setBackground(Theme.BG);
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 12, 16, 12));
        JButton dashboardButton = Theme.styledButton("Dashboard");
        JButton booksButton = Theme.styledButton("Books");
        JButton membersButton = Theme.styledButton("Members");
        JButton issueButton = Theme.styledButton("Issue / Return");
        JButton overdueButton = Theme.styledButton("Overdue & Fines");
        JButton exitButton = Theme.secondaryButton("Exit");
        dashboardButton.addActionListener(e -> {
            dashboardPanel.refresh();
            show("DASHBOARD");
        });
        booksButton.addActionListener(e -> {
            booksPanel.refresh();
            show("BOOKS");
        });
        membersButton.addActionListener(e -> {
            membersPanel.refresh();
            show("MEMBERS");
        });
        issueButton.addActionListener(e -> show("ISSUE"));
        overdueButton.addActionListener(e -> {
            overduePanel.refresh();
            show("OVERDUE");
        });
        exitButton.addActionListener(e -> System.exit(0));
        sidebar.add(dashboardButton);
        sidebar.add(booksButton);
        sidebar.add(membersButton);
        sidebar.add(issueButton);
        sidebar.add(overdueButton);
        sidebar.add(exitButton);
        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        cards.show(cardPanel, "DASHBOARD");
    }

    private void show(String name) {
        cards.show(cardPanel, name);
    }
}
