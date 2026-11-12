package com.library.gui;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;
import com.library.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * SampleData — Library Management System (Part C: GUI).
 *
 * <p>Built-in demo catalogue used when the service layer is unavailable
 * (or the data file has not been created yet). Every panel falls back to
 * this data inside a try-catch, so the GUI always opens with real content.
 * The same 12 books / 5 members are the seed data used by the service
 * layer (see {@code com.library.storage.DataStore}).</p>
 *
 * <p>Demo logins: {@code admin / admin123} (ADMIN) and
 * {@code librarian / lib123} (LIBRARIAN).</p>
 */
public final class SampleData {

    public static final String ADMIN_USER = "admin";
    public static final String ADMIN_PASS = "admin123";
    public static final String LIB_USER = "librarian";
    public static final String LIB_PASS = "lib123";

    /** Fine: Rs 5 per day late. Issue period: 14 days. */
    public static final double FINE_PER_DAY = 5.0;
    public static final int ISSUE_DAYS = 14;

    private SampleData() {
    }

    /** 12 sample books. */
    public static List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book(1, "Let Us C", "Yashavant Kanetkar", "978-8183331630",
                "Programming", "BPB Publications", 2016, 5, 4));
        books.add(new Book(2, "Java: The Complete Reference", "Herbert Schildt",
                "978-1260440232", "Programming", "McGraw-Hill", 2019, 4, 3));
        books.add(new Book(3, "Introduction to Algorithms", "Cormen, Leiserson, Rivest, Stein",
                "978-0262033848", "Algorithms", "MIT Press", 2009, 3, 2));
        books.add(new Book(4, "Database System Concepts", "Silberschatz, Korth, Sudarshan",
                "978-0078022159", "Database", "McGraw-Hill", 2019, 4, 4));
        books.add(new Book(5, "Clean Code", "Robert C. Martin",
                "978-0132350884", "Software Engineering", "Prentice Hall", 2008, 3, 3));
        books.add(new Book(6, "Operating System Concepts", "Silberschatz, Galvin, Gagne",
                "978-1119456339", "Operating Systems", "Wiley", 2018, 3, 1));
        books.add(new Book(7, "Data Structures in C", "Seymour Lipschutz",
                "978-0070602437", "Data Structures", "McGraw-Hill", 2014, 5, 5));
        books.add(new Book(8, "Computer Networks", "Andrew S. Tanenbaum",
                "978-0132126953", "Networking", "Pearson", 2010, 3, 2));
        books.add(new Book(9, "The Alchemist", "Paulo Coelho",
                "978-0062315007", "Fiction", "HarperOne", 2014, 4, 4));
        books.add(new Book(10, "Wings of Fire", "A. P. J. Abdul Kalam",
                "978-8173711466", "Biography", "Universities Press", 1999, 4, 3));
        books.add(new Book(11, "Head First Design Patterns", "Eric Freeman",
                "978-0596007126", "Software Engineering", "O'Reilly", 2004, 2, 1));
        books.add(new Book(12, "Discrete Mathematics and Its Applications",
                "Kenneth H. Rosen", "978-0073383095", "Mathematics",
                "McGraw-Hill", 2018, 3, 3));
        return books;
    }

    /** 5 sample members. */
    public static List<Member> getMembers() {
        List<Member> members = new ArrayList<>();
        members.add(new Member(1, "Aarav Sharma", "aarav@college.edu", "9876543210", "Student"));
        members.add(new Member(2, "Priya Patel", "priya@college.edu", "9876543211", "Student"));
        members.add(new Member(3, "Rahul Verma", "rahul@college.edu", "9876543212", "Faculty"));
        members.add(new Member(4, "Sneha Iyer", "sneha@college.edu", "9876543213", "Student"));
        members.add(new Member(5, "Vikram Singh", "vikram@college.edu", "9876543214", "Staff"));
        return members;
    }

    /** A few sample issue records: one returned, one active, one overdue. */
    public static List<IssueRecord> getIssues() {
        List<IssueRecord> issues = new ArrayList<>();
        LocalDate today = LocalDate.now();
        // Returned on time.
        IssueRecord r1 = new IssueRecord(1, 1, 1,
                today.minusDays(20), today.minusDays(6), today.minusDays(7), 0.0, "Returned");
        // Active, not yet due.
        IssueRecord r2 = new IssueRecord(2, 2, 2,
                today.minusDays(5), today.plusDays(9), null, 0.0, "Issued");
        // Overdue: due 6 days ago, Rs 5/day = Rs 30 fine.
        IssueRecord r3 = new IssueRecord(3, 6, 4,
                today.minusDays(20), today.minusDays(6), null, 30.0, "Overdue");
        issues.add(r1);
        issues.add(r2);
        issues.add(r3);
        return issues;
    }

    /** The demo ADMIN user. */
    public static User adminUser() {
        User u = new User(1, "Admin", ADMIN_USER, ADMIN_PASS, "ADMIN");
        return u;
    }

    /** The demo LIBRARIAN user. */
    public static User librarianUser() {
        User u = new User(2, "Librarian", LIB_USER, LIB_PASS, "LIBRARIAN");
        return u;
    }

    /** Find a book by id in a list (linear search, O(n)). */
    public static Book findBookById(List<Book> books, int id) {
        for (Book b : books) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    /** Find a member by id in a list (linear search, O(n)). */
    public static Member findMemberById(List<Member> members, int id) {
        for (Member m : members) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    /** Check a username/password pair against the demo accounts. */
    public static User checkLogin(String username, String password) {
        if (ADMIN_USER.equals(username) && ADMIN_PASS.equals(password)) {
            return adminUser();
        }
        if (LIB_USER.equals(username) && LIB_PASS.equals(password)) {
            return librarianUser();
        }
        return null;
    }
}
