package com.library.storage;

import com.library.exceptions.LibraryException;
import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Librarian;
import com.library.model.Member;
import com.library.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DataStore — Singleton holding all data in memory and persisting it
 * with Java file handling (no database, no JDBC):
 * <ul>
 *   <li>Object serialization: data/library.dat (whole lists at once)</li>
 *   <li>CSV export / import: data/books.csv</li>
 *   <li>Backup: data/library-backup.dat</li>
 * </ul>
 * Every save/load uses try-catch-finally (streams are closed in
 * finally, or by try-with-resources where noted).
 */
public class DataStore {

    /** Single shared instance (Singleton pattern). */
    private static DataStore instance;

    public static final String DATA_DIR = "data";
    public static final String DATA_FILE = DATA_DIR + "/library.dat";
    public static final String BACKUP_FILE = DATA_DIR + "/library-backup.dat";
    public static final String BOOKS_CSV = DATA_DIR + "/books.csv";

    private List<Book> books;
    private List<Member> members;
    private List<IssueRecord> issues;
    private List<User> users;

    private DataStore() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        issues = new ArrayList<>();
        users = new ArrayList<>();
    }

    /** Returns the single DataStore instance (thread-safe). */
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    /** For tests: forget the instance so a fresh one is created. */
    static synchronized void resetInstance() {
        instance = null;
    }

    // ----- access to the in-memory lists (DAOs work on these) -----

    public List<Book> getBooks() { return books; }
    public List<Member> getMembers() { return members; }
    public List<IssueRecord> getIssues() { return issues; }
    public List<User> getUsers() { return users; }

    public void setBooks(List<Book> books) { this.books = books; }
    public void setMembers(List<Member> members) { this.members = members; }
    public void setIssues(List<IssueRecord> issues) { this.issues = issues; }
    public void setUsers(List<User> users) { this.users = users; }

    /** Next free id for a list, based on the largest existing id. */
    public int nextBookId() {
        int max = 0;
        for (Book b : books) {
            max = Math.max(max, b.getId());
        }
        return max + 1;
    }

    public int nextMemberId() {
        int max = 0;
        for (Member m : members) {
            max = Math.max(max, m.getId());
        }
        return max + 1;
    }

    public int nextIssueId() {
        int max = 0;
        for (IssueRecord r : issues) {
            max = Math.max(max, r.getId());
        }
        return max + 1;
    }

    // ----- persistence: serialization -----

    /** Saves all four lists to data/library.dat. */
    public void save() throws LibraryException {
        ensureDataDir();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(DATA_FILE));
            out.writeObject(books);
            out.writeObject(members);
            out.writeObject(issues);
            out.writeObject(users);
            out.flush();
        } catch (IOException e) {
            throw new LibraryException("Could not save data to " + DATA_FILE, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                    // Nothing more we can do while closing.
                }
            }
        }
    }

    /** Loads all four lists from data/library.dat. */
    @SuppressWarnings("unchecked")
    public void load() throws LibraryException {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            throw new LibraryException("No saved data found at " + DATA_FILE
                    + ". Seed the data first (seedData) or save once.");
        }
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(file));
            books = (List<Book>) in.readObject();
            members = (List<Member>) in.readObject();
            issues = (List<IssueRecord>) in.readObject();
            users = (List<User>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new LibraryException("Could not load data from " + DATA_FILE, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                    // Nothing more we can do while closing.
                }
            }
        }
    }

    /** True when a saved data file already exists. */
    public boolean hasSavedData() {
        return new File(DATA_FILE).exists();
    }

    /** Copies the current data file to data/library-backup.dat. */
    public void backup() throws LibraryException {
        ensureDataDir();
        File src = new File(DATA_FILE);
        if (!src.exists()) {
            // Nothing saved yet — save first, then the backup exists.
            save();
        }
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(BACKUP_FILE);
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            throw new LibraryException("Backup failed", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ignored) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    // ----- CSV export / import (books only) -----

    /** Writes the catalogue to data/books.csv (header + one line per book). */
    public void exportBooksToCSV() throws LibraryException {
        ensureDataDir();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(BOOKS_CSV));
            writer.println("id,isbn,title,author,category,publisher,year,totalCopies,availableCopies,price");
            for (Book b : books) {
                writer.println(b.getId() + "," + csv(b.getIsbn()) + ","
                        + csv(b.getTitle()) + "," + csv(b.getAuthor()) + ","
                        + csv(b.getCategory()) + "," + csv(b.getPublisher())
                        + "," + b.getYear() + "," + b.getTotalCopies() + ","
                        + b.getAvailableCopies() + "," + b.getPrice());
            }
            writer.flush();
        } catch (IOException e) {
            throw new LibraryException("Could not export books to " + BOOKS_CSV, e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /** Reads books from data/books.csv, replacing the in-memory catalogue. */
    public void importBooksFromCSV() throws LibraryException {
        File file = new File(BOOKS_CSV);
        if (!file.exists()) {
            throw new LibraryException("No CSV file found at " + BOOKS_CSV);
        }
        List<Book> loaded = new ArrayList<>();
        java.io.BufferedReader reader = null;
        try {
            reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length < 10) {
                    continue; // skip malformed lines
                }
                Book b = new Book(
                        Integer.parseInt(parts[0].trim()),
                        parts[1].trim(), parts[2].trim(), parts[3].trim(),
                        parts[4].trim(), parts[5].trim(),
                        Integer.parseInt(parts[6].trim()),
                        Integer.parseInt(parts[7].trim()),
                        Integer.parseInt(parts[8].trim()),
                        Double.parseDouble(parts[9].trim()));
                loaded.add(b);
            }
            books = loaded;
        } catch (IOException | NumberFormatException e) {
            throw new LibraryException("Could not import books from " + BOOKS_CSV, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Static CSV export used by the service layer (ReportService):
     * writes the given books to the given path.
     */
    public static void exportBooksCsv(List<Book> books, String path)
            throws LibraryException {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(file));
            writer.println("id,isbn,title,author,category,publisher,year,totalCopies,availableCopies,price");
            for (Book b : books) {
                writer.println(b.getId() + "," + csvStatic(b.getIsbn()) + ","
                        + csvStatic(b.getTitle()) + "," + csvStatic(b.getAuthor()) + ","
                        + csvStatic(b.getCategory()) + "," + csvStatic(b.getPublisher())
                        + "," + b.getYear() + "," + b.getTotalCopies() + ","
                        + b.getAvailableCopies() + "," + b.getPrice());
            }
            writer.flush();
        } catch (IOException e) {
            throw new LibraryException("Could not export books to " + path, e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /** Static version of the CSV quoting helper. */
    private static String csvStatic(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /** Quotes a CSV field if it contains a comma or a quote. */
    private String csv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void ensureDataDir() throws LibraryException {
        File dir = new File(DATA_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new LibraryException("Could not create data folder: " + DATA_DIR);
        }
    }

    // ----- seed data -----

    /** Fills the store with 12 books, 5 members and 2 login users. */
    public void seedData() {
        books.clear();
        members.clear();
        issues.clear();
        users.clear();

        books.add(new Book(1, "978-81-318-7342-6", "Let Us C", "Yashavant Kanetkar",
                "Programming", "BPB Publications", 2016, 5, 5, 299.0));
        books.add(new Book(2, "978-0-596-00920-5", "Head First Java", "Kathy Sierra",
                "Programming", "O'Reilly", 2005, 4, 4, 999.0));
        books.add(new Book(3, "978-0-13-235088-4", "Clean Code", "Robert C. Martin",
                "Programming", "Pearson", 2008, 3, 3, 799.0));
        books.add(new Book(4, "978-0-13-468599-1", "Effective Java", "Joshua Bloch",
                "Programming", "Pearson", 2018, 3, 3, 899.0));
        books.add(new Book(5, "978-0-262-03384-8", "Introduction to Algorithms",
                "Thomas H. Cormen", "Algorithms", "MIT Press", 2009, 2, 2, 1499.0));
        books.add(new Book(6, "978-1-118-06333-0", "Operating System Concepts",
                "Abraham Silberschatz", "Systems", "Wiley", 2018, 4, 4, 1199.0));
        books.add(new Book(7, "978-0-07-352332-3", "Database System Concepts",
                "Abraham Silberschatz", "Database", "McGraw-Hill", 2019, 4, 4, 1299.0));
        books.add(new Book(8, "978-0-13-212695-3", "Computer Networks",
                "Andrew S. Tanenbaum", "Networks", "Pearson", 2010, 3, 3, 999.0));
        books.add(new Book(9, "978-81-291-1548-8", "Wings of Fire", "A. P. J. Abdul Kalam",
                "Biography", "Universities Press", 1999, 5, 5, 350.0));
        books.add(new Book(10, "978-0-06-112241-5", "The Alchemist", "Paulo Coelho",
                "Fiction", "HarperCollins", 1993, 4, 4, 399.0));
        books.add(new Book(11, "978-0-7475-3269-9", "Harry Potter and the Philosopher's Stone",
                "J. K. Rowling", "Fiction", "Bloomsbury", 1997, 5, 5, 499.0));
        books.add(new Book(12, "978-1-84794-183-1", "Atomic Habits", "James Clear",
                "Self-Help", "Random House", 2018, 4, 4, 599.0));

        String today = LocalDate.now().toString();
        members.add(new Member(1, "Aarav Sharma", "aarav@college.edu", "9876543210",
                Member.STUDENT, today));
        members.add(new Member(2, "Priya Patel", "priya@college.edu", "9876543211",
                Member.STUDENT, today));
        members.add(new Member(3, "Dr. Rajesh Kumar", "rajesh@college.edu", "9876543212",
                Member.FACULTY, today));
        members.add(new Member(4, "Sneha Gupta", "sneha@college.edu", "9876543213",
                Member.STUDENT, today));
        members.add(new Member(5, "Vikram Singh", "vikram@college.edu", "9876543214",
                Member.STAFF, today));

        // Login users — passwords are SHA-256 hashed by the User constructor.
        users.add(new User("admin", "admin123", User.ADMIN));
        users.add(new Librarian("librarian", "lib123", "LIB-001"));
    }
}
