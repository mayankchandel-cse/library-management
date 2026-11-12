package com.library.gui;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;
import com.library.model.User;
import com.library.service.LibraryService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MainFrame — Library Management System (Part C: GUI).
 *
 * <p>Screen 2: the main shell. A {@link CardLayout} holds one panel per
 * screen and a left navigation sidebar switches between them:
 * Dashboard, Books, Members, Issue/Return, Overdue &amp; Fines, Reports.</p>
 *
 * <p>This frame is also the shared-state holder for the session: the book
 * list, member list and issue list live here, so every panel sees the
 * same data. The service layer ({@link LibraryService}) is used when it
 * is available; every service call is guarded in try-catch and falls back
 * to the in-memory lists (see {@link SampleData}), so the GUI never goes
 * blank because of a storage failure.</p>
 *
 * <p>Business rules (shared by all panels): issue period is 14 days,
 * fine is Rs 5 per day late (see {@link SampleData}).</p>
 */
public class MainFrame extends JFrame {

    /** Card names used by the CardLayout. */
    public static final String DASHBOARD = "Dashboard";
    public static final String BOOKS = "Books";
    public static final String MEMBERS = "Members";
    public static final String ISSUE = "IssueReturn";
    public static final String OVERDUE = "Overdue";
    public static final String REPORTS = "Reports";

    private final User currentUser;
    private final LibraryService libraryService;

    private final List<Book> books = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();
    private final List<IssueRecord> issues = new ArrayList<>();
    private int nextIssueId = 100;

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JTextField searchField = Theme.styledTextField(22);

    private DashboardPanel dashboardPanel;
    private BooksPanel booksPanel;
    private MembersPanel membersPanel;
    private IssuePanel issuePanel;
    private OverduePanel overduePanel;
    private ReportsPanel reportsPanel;

    public MainFrame(User user) {
        super("Library Management System — " + user.getName()
                + " (" + user.getRole() + ")");
        this.currentUser = user;
        LibraryService service;
        try {
            service = new LibraryService();
        } catch (Exception e) {
            service = null;
        }
        this.libraryService = service;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        loadData();
        buildPanels();
        add(buildSidebar(), BorderLayout.WEST);
        add(buildTopBar(), BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        showCard(DASHBOARD);
    }

    // ---- shared state -------------------------------------------------------

    public User getCurrentUser() {
        return currentUser;
    }

    public LibraryService getLibraryService() {
        return libraryService;
    }

    /** Live book list (callers must not replace it; sort a copy). */
    public List<Book> getBooks() {
        return books;
    }

    public List<Member> getMembers() {
        return members;
    }

    public List<IssueRecord> getIssues() {
        return issues;
    }

    // ---- data loading: service first, SampleData fallback ------------------

    private void loadData() {
        List<Book> loadedBooks = null;
        List<Member> loadedMembers = null;
        if (libraryService != null) {
            try {
                loadedBooks = libraryService.getAllBooks();
            } catch (Exception e) {
                loadedBooks = null;
            }
            try {
                loadedMembers = libraryService.getAllMembers();
            } catch (Exception e) {
                loadedMembers = null;
            }
        }
        if (loadedBooks == null || loadedBooks.isEmpty()) {
            loadedBooks = SampleData.getBooks();
        }
        if (loadedMembers == null || loadedMembers.isEmpty()) {
            loadedMembers = SampleData.getMembers();
        }
        books.clear();
        books.addAll(loadedBooks);
        members.clear();
        members.addAll(loadedMembers);
        List<IssueRecord> loadedIssues = null;
        if (libraryService != null) {
            try {
                loadedIssues = libraryService.getAllIssues();
            } catch (Exception e) {
                loadedIssues = null;
            }
        }
        if (loadedIssues == null || loadedIssues.isEmpty()) {
            loadedIssues = SampleData.getIssues();
        }
        issues.clear();
        issues.addAll(loadedIssues);
        // Keep issue ids unique for new issues created in this session.
        for (IssueRecord r : issues) {
            nextIssueId = Math.max(nextIssueId, r.getId() + 1);
        }
    }

    /** Refresh every panel that shows live data. */
    public void refreshAll() {
        dashboardPanel.refresh();
        booksPanel.refresh();
        membersPanel.refresh();
        issuePanel.refresh();
        overduePanel.refresh();
        reportsPanel.refresh();
    }

    // ---- layout ------------------------------------------------------------

    private void buildPanels() {
        content.setBackground(Theme.BG);
        dashboardPanel = new DashboardPanel(this);
        booksPanel = new BooksPanel(this);
        membersPanel = new MembersPanel(this);
        issuePanel = new IssuePanel(this);
        overduePanel = new OverduePanel(this);
        reportsPanel = new ReportsPanel(this);
        content.add(dashboardPanel, DASHBOARD);
        content.add(booksPanel, BOOKS);
        content.add(membersPanel, MEMBERS);
        content.add(issuePanel, ISSUE);
        content.add(overduePanel, OVERDUE);
        content.add(reportsPanel, REPORTS);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 0, 6));
        sidebar.setBackground(Theme.PANEL);
        sidebar.setPreferredSize(new Dimension(190, 0));

        JLabel logo = Theme.label("LIBRARY", Theme.HEADING_FONT, Theme.ACCENT);
        logo.setHorizontalAlignment(JLabel.CENTER);
        sidebar.add(logo);
        sidebar.add(navButton("Dashboard", DASHBOARD));
        sidebar.add(navButton("Books", BOOKS));
        sidebar.add(navButton("Members", MEMBERS));
        sidebar.add(navButton("Issue / Return", ISSUE));
        sidebar.add(navButton("Overdue & Fines", OVERDUE));
        sidebar.add(navButton("Reports", REPORTS));

        JButton logoutButton = Theme.secondaryButton("Logout");
        logoutButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });
        sidebar.add(logoutButton);
        return sidebar;
    }

    private JButton navButton(String text, String card) {
        JButton button = Theme.secondaryButton(text);
        button.addActionListener(e -> showCard(card));
        return button;
    }

    private JPanel buildTopBar() {
        JPanel top = Theme.panel();
        top.setLayout(new BorderLayout(10, 10));
        JLabel welcome = Theme.label("Welcome, " + currentUser.getName()
                + "  •  " + currentUser.getRole()
                + "  •  " + books.size() + " books in the library",
                Theme.SUBHEADING_FONT, Theme.TEXT);
        top.add(welcome, BorderLayout.WEST);

        JPanel searchBox = Theme.panel();
        searchBox.add(Theme.label("Search books:"));
        searchBox.add(searchField);
        JButton go = Theme.styledButton("Go");
        go.setPreferredSize(new Dimension(70, 36));
        go.addActionListener(e -> {
            booksPanel.setQuery(searchField.getText().trim());
            showCard(BOOKS);
        });
        searchField.addActionListener(e -> go.doClick());
        searchBox.add(go);
        top.add(searchBox, BorderLayout.EAST);
        return top;
    }

    /** Switches screen; panels refresh themselves on display. */
    public void showCard(String card) {
        refreshAll();
        cards.show(content, card);
    }

    // ---- book / member helpers (shared by panels) --------------------------

    /** Add a book (service first, in-memory fallback). */
    public void addBook(Book book) {
        if (libraryService != null) {
            try {
                libraryService.addBook(book);
            } catch (Exception ignored) {
                // Fall through to the in-memory list.
            }
        }
        books.add(book);
        refreshAll();
    }

    /**
     * Update a book already in the list (matched by id). The service has
     * no update method, so it is remove + add there (see LibraryService:
     * {@code removeBook} / {@code addBook}).
     */
    public void updateBook(Book book) {
        if (libraryService != null) {
            try {
                if (libraryService.getBookById(book.getId()) != null) {
                    libraryService.removeBook(book.getId());
                    libraryService.addBook(book);
                }
            } catch (Exception ignored) {
                // Fall through to the in-memory list.
            }
        }
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == book.getId()) {
                books.set(i, book);
                break;
            }
        }
        refreshAll();
    }

    /** Delete a book by id. Returns false if the book is currently issued. */
    public boolean deleteBook(int bookId) {
        for (IssueRecord r : issues) {
            if (r.getBookId() == bookId && "Issued".equals(r.getStatus())) {
                return false; // cannot delete a book that is out on loan
            }
        }
        if (libraryService != null) {
            try {
                libraryService.removeBook(bookId);
            } catch (Exception ignored) {
                // Fall through to the in-memory list.
            }
        }
        books.removeIf(b -> b.getId() == bookId);
        refreshAll();
        return true;
    }

    /** Add a member (service first, in-memory fallback). */
    public void addMember(Member member) {
        if (libraryService != null) {
            try {
                libraryService.registerMember(member);
            } catch (Exception ignored) {
                // Fall through to the in-memory list.
            }
        }
        members.add(member);
        refreshAll();
    }

    /** Next free book id (max id + 1). */
    public int nextBookId() {
        int max = 0;
        for (Book b : books) {
            max = Math.max(max, b.getId());
        }
        return max + 1;
    }

    /** Next free member id (max id + 1). */
    public int nextMemberId() {
        int max = 0;
        for (Member m : members) {
            max = Math.max(max, m.getId());
        }
        return max + 1;
    }

    // ---- issue / return (the core business logic) --------------------------

    /**
     * Issue a book to a member. Due date is today + 14 days. One copy is
     * taken off the available count. Returns the new IssueRecord, or null
     * with a message dialog if the issue is not possible.
     */
    public IssueRecord issueBook(int bookId, int memberId) {
        Book book = SampleData.findBookById(books, bookId);
        Member member = SampleData.findMemberById(members, memberId);
        if (book == null) {
            JOptionPane.showMessageDialog(this, "No book with id " + bookId + ".",
                    "Issue failed", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (member == null) {
            JOptionPane.showMessageDialog(this, "No member with id " + memberId + ".",
                    "Issue failed", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (book.getAvailableCopies() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "\"" + book.getTitle() + "\" has no copies available.",
                    "Issue failed", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        // Service path: the service owns the rules (copy available, member
        // limit, no duplicate issue) and mutates the shared Book object
        // itself, so on success we must NOT decrement the copy again.
        // If the service catalogue does not hold this book (it fell back
        // to SampleData locally), use the local path instead.
        if (libraryService != null
                && libraryService.getBookById(bookId) != null
                && libraryService.getMemberById(memberId) != null) {
            try {
                IssueRecord record = libraryService.issueBook(bookId, memberId);
                issues.add(record);
                refreshAll();
                return record;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Issue failed: " + e.getMessage(),
                        "Issue failed", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        // Local path (no service, or service catalogue out of sync).
        LocalDate today = LocalDate.now();
        IssueRecord record = new IssueRecord(nextIssueId++, bookId, memberId,
                today, today.plusDays(SampleData.ISSUE_DAYS),
                null, 0.0, "Issued");
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        issues.add(record);
        refreshAll();
        return record;
    }

    /**
     * Return a book. Fine = Rs 5 per day late (0 if on time). The copy
     * goes back on the shelf. Returns the fine, or -1 if the record
     * was not found / already returned.
     */
    public double returnBook(int issueId) {
        IssueRecord record = null;
        for (IssueRecord r : issues) {
            if (r.getId() == issueId) {
                record = r;
                break;
            }
        }
        if (record == null || !"Issued".equals(record.getStatus())
                && !"Overdue".equals(record.getStatus())) {
            return -1;
        }
        // Service path: returnBook() already marks the record returned,
        // sets the fine and puts the copy back on the shelf (same shared
        // Book object), so on success we must NOT do any of that again.
        // If the service does not hold this record, use the local path.
        if (libraryService != null
                && libraryService.getIssueById(issueId) != null) {
            try {
                double fine = libraryService.returnBook(issueId);
                refreshAll();
                return fine;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Return failed: " + e.getMessage(),
                        "Return failed", JOptionPane.WARNING_MESSAGE);
                return -1;
            }
        }
        // Local path (no service, or service out of sync).
        double fine = computeFine(record);
        record.setReturnDate(LocalDate.now());
        record.setFine(fine);
        record.setStatus("Returned");
        Book book = SampleData.findBookById(books, record.getBookId());
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
        }
        refreshAll();
        return fine;
    }

    /** Fine for a record: Rs 5 per day past the due date (0 if not late). */
    public static double computeFine(IssueRecord record) {
        LocalDate due = record.getDueDate();
        LocalDate end = record.getReturnDate() != null
                ? record.getReturnDate() : LocalDate.now();
        if (due == null || !end.isAfter(due)) {
            return 0.0;
        }
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(due, end);
        return daysLate * SampleData.FINE_PER_DAY;
    }

    /** All records that are overdue right now (not returned, past due date). */
    public List<IssueRecord> getOverdueRecords() {
        List<IssueRecord> overdue = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (IssueRecord r : issues) {
            // returnDate != null means already returned — the service marks
            // a late return "Overdue", so status alone is not enough.
            if (r.getReturnDate() == null
                    && ("Issued".equals(r.getStatus()) || "Overdue".equals(r.getStatus()))
                    && r.getDueDate() != null && r.getDueDate().isBefore(today)) {
                r.setStatus("Overdue");
                r.setFine(computeFine(r));
                overdue.add(r);
            }
        }
        return overdue;
    }

    /** Total fines outstanding on overdue records. */
    public double getTotalFines() {
        double total = 0.0;
        for (IssueRecord r : getOverdueRecords()) {
            total += r.getFine();
        }
        return total;
    }
}
