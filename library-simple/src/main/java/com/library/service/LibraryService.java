package com.library.service;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Student;
import com.library.repository.BookJdbcDao;
import com.library.repository.BookRepository;
import com.library.repository.IssueRepository;
import com.library.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Checklist items 1-4, business rules only. No SQL here, no screen code here.
 * @Service marks this as the business layer. Spring gives us the
 * repositories through the constructor (dependency injection with @Autowired
 * behaviour) instead of us doing "new BookRepository()".
 */
@Service
public class LibraryService {

    public static final int LOAN_DAYS = 14;

    private final BookRepository bookRepository;
    private final BookJdbcDao bookJdbcDao;
    private final StudentRepository studentRepository;
    private final IssueRepository issueRepository;
    private final FineCalculator fineCalculator;

    public LibraryService(BookRepository bookRepository,
                          BookJdbcDao bookJdbcDao,
                          StudentRepository studentRepository,
                          IssueRepository issueRepository,
                          FineCalculator fineCalculator) {
        this.bookRepository = bookRepository;
        this.bookJdbcDao = bookJdbcDao;
        this.studentRepository = studentRepository;
        this.issueRepository = issueRepository;
        this.fineCalculator = fineCalculator;
    }

    // --- Books (item 1) ---
    public void addBook(String title, String author, int copies) {
        bookRepository.addBook(title, author, copies);
    }

    public void updateBook(int id, String title, String author) {
        bookRepository.updateBook(id, title, author);
    }

    public void deleteBook(int id) {
        bookRepository.deleteBook(id);
    }

    public List<Book> listBooks() {
        return bookRepository.findAll();
    }

    /** Search uses plain JDBC (BookJdbcDao), not JdbcTemplate. */
    public List<Book> searchBooks(String keyword) {
        return bookJdbcDao.searchBooks(keyword);
    }

    // --- Students (item 2) ---
    public void registerStudent(String name, String email) {
        studentRepository.registerStudent(name, email);
    }

    public List<Student> listStudents() {
        return studentRepository.findAll();
    }

    public List<IssueRecord> borrowingHistory(int studentId) {
        return studentRepository.borrowingHistory(studentId);
    }

    // --- Issue / return (item 3) ---
    // @Transactional: insert the issue AND reduce the copies together,
    // or neither happens if one step fails.
    @Transactional
    public int issueBook(int bookId, int studentId) {
        Book book = bookJdbcDao.findBookById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("No book with ID " + bookId);
        }
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies available for: " + book.getTitle());
        }
        LocalDate today = LocalDate.now();
        int issueId = issueRepository.issueBook(bookId, studentId, today, today.plusDays(LOAN_DAYS));
        bookRepository.changeAvailableCopies(bookId, -1);
        return issueId;
    }

    @Transactional
    public IssueRecord returnBook(int issueId) {
        IssueRecord record = issueRepository.findById(issueId);
        if (record == null) {
            throw new IllegalArgumentException("No issue with ID " + issueId);
        }
        if (record.getReturnDate() != null) {
            throw new IllegalStateException("This book was already returned.");
        }
        LocalDate today = LocalDate.now();
        // Checklist item 4: fine is calculated here, Rs 5 per day late.
        double fine = fineCalculator.calculateFine(record.getDueDate(), today);
        String status = "Returned";
        issueRepository.returnBook(issueId, today, fine, status);
        bookRepository.changeAvailableCopies(record.getBookId(), 1);
        record.setReturnDate(today);
        record.setFine(fine);
        record.setStatus(status);
        return record;
    }

    // --- Overdue (item 4) ---
    public List<IssueRecord> overdueBooks() {
        return issueRepository.findOverdue();
    }
}
