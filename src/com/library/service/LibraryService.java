package com.library.service;

import com.library.exceptions.InvalidInputException;
import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;
import com.library.storage.BookDAO;
import com.library.storage.IssueDAO;
import com.library.storage.MemberDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * LibraryService — core business logic of the Library Management System.
 *
 * <p>All public methods are {@code synchronized} so that two threads (for
 * example the GUI thread and the {@link ReminderService} thread) can never
 * issue the same last copy of a book at the same time. The in-memory lists
 * are the source of truth; every change is also persisted through the DAO
 * layer (best effort — a DAO failure is swallowed so the demo still works
 * without a storage file).</p>
 *
 * <p>Rules enforced here:</p>
 * <ul>
 *   <li>A book can only be issued if {@code availableCopies > 0}.</li>
 *   <li>A member may not hold more active issues than {@code maxBooks}
 *       (Student 3, Staff 4, Faculty 5 — derived from member type).</li>
 *   <li>A member may not issue the same book twice at the same time.</li>
 *   <li>Due date = issue date + 14 days; late fine = Rs 5 per day
 *       (see {@link FineCalculator}).</li>
 * </ul>
 *
 * <p>Note on the model API (from Part A): {@link IssueRecord} stores its
 * dates as {@code java.time.LocalDate} and its status as
 * {@code "Issued"} / {@code "Returned"} / {@code "Overdue"}.</p>
 */
public class LibraryService {

    /** Loan period in days (matches IssueRecord.LOAN_DAYS). */
    public static final int LOAN_DAYS = IssueRecord.LOAN_DAYS;

    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final IssueDAO issueDAO;

    // In-memory working copies (source of truth for the demo).
    private final List<Book> books = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();
    private final List<IssueRecord> issues = new ArrayList<>();
    private int nextIssueId = 1;

    /** Creates a service wired to the DAO layer. */
    public LibraryService() {
        this(new BookDAO(), new MemberDAO(), new IssueDAO());
    }

    /** Creates a service with explicit DAOs (useful for tests). */
    public LibraryService(BookDAO bookDAO, MemberDAO memberDAO, IssueDAO issueDAO) {
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
        this.issueDAO = issueDAO;
        loadFromStorage();
    }

    /** Loads whatever the DAOs already hold; failures are ignored. */
    private void loadFromStorage() {
        try {
            List<Book> loaded = bookDAO.getAll();
            if (loaded != null) books.addAll(loaded);
        } catch (Exception ignored) { /* storage empty / unavailable */ }
        try {
            List<Member> loaded = memberDAO.getAll();
            if (loaded != null) members.addAll(loaded);
        } catch (Exception ignored) { /* storage empty / unavailable */ }
        try {
            List<IssueRecord> loaded = issueDAO.getAll();
            if (loaded != null) {
                issues.addAll(loaded);
                for (IssueRecord r : loaded) {
                    nextIssueId = Math.max(nextIssueId, r.getId() + 1);
                }
            }
        } catch (Exception ignored) { /* storage empty / unavailable */ }
    }

    /**
     * True when an issue is still out. Part A marks a returned-late book
     * "Overdue" too, so status alone is not enough: an issue is active
     * only while it has no return date.
     */
    private static boolean isActive(IssueRecord r) {
        return r.getReturnDate() == null
                && !"Returned".equalsIgnoreCase(r.getStatus());
    }

    // ---- Books -------------------------------------------------------------

    /** Adds a book after validation. Returns the stored book. */
    public synchronized Book addBook(Book book) throws InvalidInputException {
        if (book == null) throw new InvalidInputException("Book cannot be null");
        if (book.getTitle() == null || book.getTitle().trim().isEmpty())
            throw new InvalidInputException("Book title cannot be empty");
        if (book.getTotalCopies() < 0)
            throw new InvalidInputException("Total copies cannot be negative");
        if (book.getAvailableCopies() > book.getTotalCopies())
            book.setAvailableCopies(book.getTotalCopies());
        books.add(book);
        try { bookDAO.add(book); } catch (Exception ignored) { /* demo mode */ }
        return book;
    }

    /**
     * Updates a book already in the catalogue (matched by id).
     * Alias used by the GUI ({@code MainFrame.updateBook}).
     */
    public synchronized void updateBook(Book book) throws InvalidInputException {
        if (book == null) throw new InvalidInputException("Book cannot be null");
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == book.getId()) {
                books.set(i, book);
                try { bookDAO.update(book); } catch (Exception ignored) { /* demo */ }
                return;
            }
        }
        throw new InvalidInputException("No book with id " + book.getId());
    }

    /** Alias for {@link #removeBook(int)}, used by the GUI. */
    public synchronized boolean deleteBook(int bookId) {
        return removeBook(bookId);
    }

    /** Removes a book by id. Returns true if a book was removed. */
    public synchronized boolean removeBook(int bookId) {
        boolean removed = books.removeIf(b -> b.getId() == bookId);
        if (removed) {
            try { bookDAO.delete(bookId); } catch (Exception ignored) { /* demo */ }
        }
        return removed;
    }

    /** All books in the catalogue (a copy, so callers cannot corrupt state). */
    public synchronized List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    /** Finds a book by id, or null if there is none. */
    public synchronized Book getBookById(int bookId) {
        for (Book b : books) {
            if (b.getId() == bookId) return b;
        }
        return null;
    }

    // ---- Members -----------------------------------------------------------

    /** Registers a member after validation. Returns the stored member. */
    public synchronized Member registerMember(Member member) throws InvalidInputException {
        if (member == null) throw new InvalidInputException("Member cannot be null");
        if (member.getName() == null || member.getName().trim().isEmpty())
            throw new InvalidInputException("Member name cannot be empty");
        members.add(member);
        try { memberDAO.add(member); } catch (Exception ignored) { /* demo */ }
        return member;
    }

    /** All registered members (a copy). */
    public synchronized List<Member> getAllMembers() {
        return new ArrayList<>(members);
    }

    /** Finds a member by id, or null if there is none. */
    public synchronized Member getMemberById(int memberId) {
        for (Member m : members) {
            if (m.getId() == memberId) return m;
        }
        return null;
    }

    // ---- Issue / return ----------------------------------------------------

    /**
     * Issues a book to a member.
     *
     * @return the new {@link IssueRecord}, due in {@link #LOAN_DAYS} days
     * @throws InvalidInputException if the book/member does not exist, the
     *         book has no free copy, the member is at their limit, or the
     *         member already holds this book
     */
    public synchronized IssueRecord issueBook(int bookId, int memberId)
            throws InvalidInputException {
        Book book = getBookById(bookId);
        if (book == null) throw new InvalidInputException("No book with id " + bookId);
        Member member = getMemberById(memberId);
        if (member == null) throw new InvalidInputException("No member with id " + memberId);
        if (book.getAvailableCopies() <= 0)
            throw new InvalidInputException("No copies of '" + book.getTitle() + "' are available");
        if (activeIssuesFor(memberId) >= member.getMaxBooks())
            throw new InvalidInputException("Member '" + member.getName()
                    + "' has reached the limit of " + member.getMaxBooks() + " books");
        for (IssueRecord r : issues) {
            if (r.getBookId() == bookId && r.getMemberId() == memberId && isActive(r)) {
                throw new InvalidInputException("Member already has this book issued");
            }
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        // Constructor sets issue date = today, due date = today + 14 days,
        // status = "Issued", fine = 0.
        IssueRecord record = new IssueRecord(nextIssueId++, bookId, memberId);
        issues.add(record);
        try {
            bookDAO.update(book);
            issueDAO.add(record);
        } catch (Exception ignored) { /* demo mode */ }
        return record;
    }

    /**
     * Returns a book. The fine is Rs 5 per day late (0 if on time) and the
     * copy goes back on the shelf.
     *
     * @return the fine charged (Rs)
     * @throws InvalidInputException if the issue record does not exist or
     *         the book was already returned
     */
    public synchronized double returnBook(int issueId) throws InvalidInputException {
        IssueRecord record = getIssueById(issueId);
        if (record == null) throw new InvalidInputException("No issue record with id " + issueId);
        if (!isActive(record))
            throw new InvalidInputException("This book was already returned");

        // markReturned() sets returnDate = today, fine = Rs 5 x days late,
        // status = "Overdue" if fined, else "Returned".
        record.markReturned();
        double fine = record.getFine();

        Book book = getBookById(record.getBookId());
        if (book != null) book.setAvailableCopies(book.getAvailableCopies() + 1);
        try {
            issueDAO.update(record);
            if (book != null) bookDAO.update(book);
        } catch (Exception ignored) { /* demo mode */ }
        return fine;
    }

    /** Finds an issue record by id, or null. */
    public synchronized IssueRecord getIssueById(int issueId) {
        for (IssueRecord r : issues) {
            if (r.getId() == issueId) return r;
        }
        return null;
    }

    /** All issue records (a copy). */
    public synchronized List<IssueRecord> getAllIssues() {
        return new ArrayList<>(issues);
    }

    /** Number of books a member currently has out. */
    public synchronized int activeIssuesFor(int memberId) {
        int count = 0;
        for (IssueRecord r : issues) {
            if (r.getMemberId() == memberId && isActive(r)) count++;
        }
        return count;
    }

    // ---- Search / reports --------------------------------------------------

    /**
     * Case-insensitive search over title, author, ISBN and category.
     * An empty query returns the whole catalogue.
     */
    public synchronized List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) return getAllBooks();
        String q = query.trim().toLowerCase();
        List<Book> result = new ArrayList<>();
        for (Book b : books) {
            if ((b.getTitle() != null && b.getTitle().toLowerCase().contains(q))
                    || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q))
                    || (b.getIsbn() != null && b.getIsbn().toLowerCase().contains(q))
                    || (b.getCategory() != null && b.getCategory().toLowerCase().contains(q))) {
                result.add(b);
            }
        }
        return result;
    }

    /** Issues still out whose due date has passed. */
    public synchronized List<IssueRecord> getOverdueRecords() {
        List<IssueRecord> result = new ArrayList<>();
        for (IssueRecord r : issues) {
            if (r.isOverdue()) result.add(r);
        }
        return result;
    }

    /** The 5 most-issued books, most issued first. */
    public synchronized List<Book> getMostIssuedBooks() {
        Map<Integer, Integer> countByBook = new HashMap<>();
        for (IssueRecord r : issues) {
            countByBook.merge(r.getBookId(), 1, Integer::sum);
        }
        List<Book> sorted = new ArrayList<>(books);
        sorted.sort(Comparator.comparingInt(
                (Book b) -> countByBook.getOrDefault(b.getId(), 0)).reversed());
        return sorted.subList(0, Math.min(5, sorted.size()));
    }

    /**
     * Dashboard figures: totalBooks, totalMembers, issued (currently out),
     * overdue, totalFines (Rs collected on returned books).
     */
    public synchronized Map<String, Object> getStats() {
        int issued = 0;
        double totalFines = 0.0;
        for (IssueRecord r : issues) {
            if (isActive(r)) issued++;
            totalFines += r.getFine();
        }
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalBooks", books.size());
        stats.put("totalMembers", members.size());
        stats.put("issued", issued);
        stats.put("overdue", getOverdueRecords().size());
        stats.put("totalFines", totalFines);
        return stats;
    }
}
