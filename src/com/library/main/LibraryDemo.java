package com.library.main;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;
import com.library.service.FineCalculator;
import com.library.service.LibraryService;
import com.library.service.ReminderService;
import com.library.service.ReportService;
import com.library.service.SearchSort;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * LibraryDemo — console demo of the Library Management System (viva demo).
 *
 * <p>Runs headless, no GUI and no database:</p>
 * <pre>
 *   java -cp out com.library.main.LibraryDemo
 * </pre>
 * It seeds a catalogue, issues a book, returns it late to show the fine,
 * searches, sorts, prints statistics, and writes a report file.
 */
public class LibraryDemo {

    public static void main(String[] args) {
        try {
            runDemo();
        } catch (Exception e) {
            System.err.println("Demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runDemo() throws Exception {
        System.out.println("=== Library Management System — Console Demo ===\n");
        LibraryService service = new LibraryService();

        // 1. Seed the catalogue and members.
        seed(service);
        System.out.println("Catalogue (" + service.getAllBooks().size() + " books):");
        for (Book b : service.getAllBooks()) {
            System.out.println("  [" + b.getId() + "] " + b.getTitle()
                    + " by " + b.getAuthor() + " (" + b.getYear() + ")"
                    + " — " + b.getAvailableCopies() + "/" + b.getTotalCopies() + " available");
        }

        // 2. Issue a book (due in 14 days).
        IssueRecord issue = service.issueBook(3, 1);
        System.out.println("\nIssued '" + service.getBookById(3).getTitle()
                + "' to " + service.getMemberById(1).getName()
                + " — due " + issue.getDueDate() + " (issue id " + issue.getId() + ").");

        // 3. Return it late: pretend the due date was 5 days ago.
        issue.setDueDate(LocalDate.now().minusDays(5));
        double fine = service.returnBook(issue.getId());
        System.out.println("Returned 5 days late — fine: Rs " + fine
                + " (Rs " + FineCalculator.FINE_PER_DAY + " x 5 days).");

        // 4. Search (linear search, O(n)) and sort (Quick Sort, O(n log n)).
        Book found = SearchSort.linearSearchByTitle(service.getAllBooks(), "wings");
        System.out.println("\nSearch for 'wings': "
                + (found == null ? "not found" : found.getTitle() + " by " + found.getAuthor()));
        List<Book> sorted = service.getAllBooks();
        SearchSort.quickSortByTitle(sorted);
        System.out.println("Catalogue sorted by title (Quick Sort):");
        for (Book b : sorted) System.out.println("  - " + b.getTitle());

        // 5. Overdue reminder thread (one scan, then stop).
        ReminderService reminders = new ReminderService(service);
        reminders.start();
        Thread.sleep(6000); // let it scan once (5 s interval)
        reminders.stop();

        // 6. Statistics and report file.
        Map<String, Object> stats = service.getStats();
        System.out.println("\nStatistics: " + stats);
        ReportService reports = new ReportService(service);
        System.out.println("Report written to: "
                + reports.generateReport().getAbsolutePath());
        System.out.println("CSV written to: "
                + reports.exportBooksCsv("data/books.csv").getAbsolutePath());
        System.out.println("\n=== Demo complete ===");
    }

    /** Seeds 5 books and 2 members. */
    private static void seed(LibraryService service) throws Exception {
        service.addBook(book(1, "978-81-291-0000-1", "Wings of Fire",
                "A. P. J. Abdul Kalam", "Biography", 1999, 3));
        service.addBook(book(2, "978-93-5000-000-2", "Let Us C",
                "Yashavant Kanetkar", "Programming", 2016, 5));
        service.addBook(book(3, "978-93-5000-000-3", "Java: The Complete Reference",
                "Herbert Schildt", "Programming", 2018, 4));
        service.addBook(book(4, "978-93-5000-000-4", "Data Structures in C",
                "Reema Thareja", "Computer Science", 2011, 2));
        service.addBook(book(5, "978-93-5000-000-5", "Operating System Concepts",
                "Silberschatz", "Computer Science", 2018, 3));

        // Member type decides the limit: Student = 3, Faculty = 5
        // (getMaxBooks() is derived in Part A — there is no setMaxBooks).
        Member student = new Member();
        student.setId(1);
        student.setName("Mayank");
        student.setEmail("mayank@college.edu");
        student.setMemberType(Member.STUDENT);
        service.registerMember(student);

        Member faculty = new Member();
        faculty.setId(2);
        faculty.setName("Prof. Sharma");
        faculty.setEmail("sharma@college.edu");
        faculty.setMemberType(Member.FACULTY);
        service.registerMember(faculty);
    }

    private static Book book(int id, String isbn, String title, String author,
                             String category, int year, int copies) {
        Book b = new Book();
        b.setId(id);
        b.setIsbn(isbn);
        b.setTitle(title);
        b.setAuthor(author);
        b.setCategory(category);
        b.setYear(year);
        b.setTotalCopies(copies);
        b.setAvailableCopies(copies);
        return b;
    }
}
