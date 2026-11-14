package com.library.service;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;
import com.library.repository.BookJdbcDao;
import com.library.repository.BookRepository;
import com.library.repository.IssueRepository;
import com.library.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LibraryService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private BookJdbcDao bookJdbcDao;

    @Autowired
    private FineCalculator fineCalculator;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchByTitleOrAuthor(keyword);
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public void updateBook(Book book) {
        bookRepository.update(book);
    }

    public void deleteBook(int id) {
        bookRepository.deleteById(id);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public void registerMember(Member member) {
        memberRepository.save(member);
    }

    public List<IssueRecord> getBorrowingHistory(int memberId) {
        return issueRepository.findByMemberId(memberId);
    }

    public IssueRecord issueBook(int bookId, int memberId) {
        Book book = bookRepository.findById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("No book found with id " + bookId);
        }
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Book '" + book.getTitle() + "' has no copies available");
        }
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("No member found with id " + memberId);
        }
        long activeIssues = issueRepository.findByMemberId(memberId).stream()
                .filter(record -> record.getReturnDate() == null)
                .count();
        if (activeIssues >= member.getMaxBooks()) {
            throw new IllegalStateException("Member '" + member.getName()
                    + "' has already borrowed the maximum of " + member.getMaxBooks() + " books");
        }

        IssueRecord issue = new IssueRecord();
        issue.setBookId(bookId);
        issue.setMemberId(memberId);
        issue.setIssueDate(LocalDate.now());
        issue.setDueDate(LocalDate.now().plusDays(14));
        issue.setReturnDate(null);
        issue.setFine(0);
        issue.setStatus("Issued");
        issueRepository.save(issue);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.update(book);
        return issue;
    }

    public double returnBook(int issueId) {
        IssueRecord issue = issueRepository.findAll().stream()
                .filter(record -> record.getId() == issueId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No issue found with id " + issueId));
        if (issue.getReturnDate() != null) {
            throw new IllegalStateException("Issue " + issueId + " has already been returned");
        }
        double fine = fineCalculator.calculateFine(issue.getDueDate(), LocalDate.now());
        issueRepository.markReturned(issueId, LocalDate.now(), fine);

        Book book = bookRepository.findById(issue.getBookId());
        if (book != null) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.update(book);
        }
        return fine;
    }

    public List<IssueRecord> getOverdueRecords() {
        return bookJdbcDao.findOverdueRecords();
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", bookRepository.countBooks());
        stats.put("totalMembers", memberRepository.countMembers());
        stats.put("issued", issueRepository.findActive().size());
        stats.put("overdue", issueRepository.findOverdue().size());
        double totalFines = issueRepository.findAll().stream()
                .mapToDouble(IssueRecord::getFine)
                .sum();
        stats.put("totalFines", totalFines);
        return stats;
    }
}
