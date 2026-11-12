package com.library.service;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;
import com.library.storage.DataStore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ReportService — plain-text and CSV reports (Java file handling).
 *
 * <p>Uses {@link BufferedWriter} + {@code try-with-resources}, so the file
 * is always closed (and flushed) even if writing fails halfway. Reports go
 * to {@code data/} next to the working directory.</p>
 */
public class ReportService {

    private final LibraryService service;

    /** Creates the report service for the given library service. */
    public ReportService(LibraryService service) {
        this.service = service;
    }

    /**
     * Writes {@code data/books_report.txt}: totals, the 5 most-issued
     * books, and the overdue list. Returns the file that was written.
     */
    public File generateReport() throws IOException {
        File dir = new File("data");
        if (!dir.exists()) dir.mkdirs();
        File report = new File(dir, "books_report.txt");
        Map<String, Object> stats = service.getStats();
        List<Book> top = service.getMostIssuedBooks();
        List<IssueRecord> overdue = service.getOverdueRecords();

        try (BufferedWriter w = new BufferedWriter(new FileWriter(report))) {
            w.write("Library Management System — Report");
            w.newLine();
            w.write("Generated on: " + LocalDate.now());
            w.newLine();
            w.write("====================================");
            w.newLine();
            w.newLine();
            w.write("Totals");
            w.newLine();
            w.write("  Total books   : " + stats.get("totalBooks"));
            w.newLine();
            w.write("  Total members : " + stats.get("totalMembers"));
            w.newLine();
            w.write("  Books issued  : " + stats.get("issued"));
            w.newLine();
            w.write("  Overdue       : " + stats.get("overdue"));
            w.newLine();
            w.write("  Total fines   : Rs " + stats.get("totalFines"));
            w.newLine();
            w.newLine();
            w.write("Most-issued books");
            w.newLine();
            for (Book b : top) {
                w.write("  - " + b.getTitle() + " by " + b.getAuthor()
                        + " (" + b.getAvailableCopies() + "/" + b.getTotalCopies()
                        + " copies available)");
                w.newLine();
            }
            w.newLine();
            w.write("Overdue books");
            w.newLine();
            if (overdue.isEmpty()) {
                w.write("  (none)");
                w.newLine();
            } else {
                for (IssueRecord r : overdue) {
                    Book book = service.getBookById(r.getBookId());
                    Member member = service.getMemberById(r.getMemberId());
                    w.write("  - " + (book == null ? "book " + r.getBookId() : book.getTitle())
                            + " — " + (member == null ? "member " + r.getMemberId() : member.getName())
                            + " — due " + r.getDueDate());
                    w.newLine();
                }
            }
        }
        return report;
    }

    /**
     * Writes a CSV export of the catalogue to the given path
     * ({@code id,isbn,title,author,category,year,totalCopies,availableCopies}).
     * The export is delegated to {@link DataStore} when it offers one, and
     * falls back to writing the file directly here.
     */
    public File exportBooksCsv(String path) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null) parent.mkdirs();
        // DataStore (Part A) exports to its fixed path data/books.csv.
        if (DataStore.BOOKS_CSV.equals(path)) {
            try {
                DataStore.getInstance().exportBooksToCSV();
                return file;
            } catch (Exception ignored) {
                // Fall through and write the file ourselves below.
            }
        }
        try (BufferedWriter w = new BufferedWriter(new FileWriter(file))) {
            w.write("id,isbn,title,author,category,year,totalCopies,availableCopies");
            w.newLine();
            for (Book b : service.getAllBooks()) {
                w.write(b.getId() + "," + csv(b.getIsbn()) + "," + csv(b.getTitle())
                        + "," + csv(b.getAuthor()) + "," + csv(b.getCategory())
                        + "," + b.getYear() + "," + b.getTotalCopies()
                        + "," + b.getAvailableCopies());
                w.newLine();
            }
        }
        return file;
    }

    /** CSV-escapes one field (quotes around values containing , or "). */
    private static String csv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
