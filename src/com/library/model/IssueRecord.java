package com.library.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * IssueRecord — one issue of a book to a member.
 * Loan period is 14 days. Fine is Rs 5 per day late.
 * Dates are java.time.LocalDate (the service / GUI layers work
 * with LocalDate: dueDate.isBefore(today), FineCalculator, ...).
 * returnDate is null while the book is still issued.
 * A String-date constructor is also provided for convenience.
 */
public class IssueRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int LOAN_DAYS = 14;
    public static final double FINE_PER_DAY = 5.0;

    private int id;
    private int bookId;
    private int memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;        // issueDate + 14 days
    private LocalDate returnDate;     // null until returned
    private double fine;              // in Rs
    private String status;            // Issued / Returned / Overdue

    public IssueRecord() {
    }

    /** Creates an issue starting today; due date is +14 days. */
    public IssueRecord(int id, int bookId, int memberId) {
        this(id, bookId, memberId, LocalDate.now());
    }

    /** Creates an issue starting on the given date. */
    public IssueRecord(int id, int bookId, int memberId, LocalDate issueDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = (issueDate == null)
                ? null : issueDate.plusDays(LOAN_DAYS);
        this.returnDate = null;
        this.fine = 0.0;
        this.status = "Issued";
    }

    /**
     * Full constructor used by the GUI sample data:
     * (id, bookId, memberId, issueDate, dueDate, returnDate, fine, status).
     * returnDate may be null (book still out).
     */
    public IssueRecord(int id, int bookId, int memberId,
                       LocalDate issueDate, LocalDate dueDate,
                       LocalDate returnDate, double fine, String status) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
        this.status = status;
    }

    /** String-date version of the full constructor (ISO yyyy-MM-dd). */
    public IssueRecord(int id, int bookId, int memberId,
                       String issueDate, String dueDate,
                       String returnDate, double fine, String status) {
        this(id, bookId, memberId,
             issueDate == null ? null : LocalDate.parse(issueDate),
             dueDate == null ? null : LocalDate.parse(dueDate),
             returnDate == null ? null : LocalDate.parse(returnDate),
             fine, status);
    }

    /** String-date version of the 4-arg constructor. */
    public IssueRecord(int id, int bookId, int memberId, String issueDate) {
        this(id, bookId, memberId,
             issueDate == null ? null : LocalDate.parse(issueDate));
    }

    /** True when the book is still out and today is past the due date. */
    public boolean isOverdue() {
        if (returnDate != null || dueDate == null) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }

    /** Days late: 0 if on time. Uses returnDate if returned, else today. */
    public long daysLate() {
        if (dueDate == null) {
            return 0;
        }
        LocalDate end = (returnDate != null) ? returnDate : LocalDate.now();
        long days = ChronoUnit.DAYS.between(dueDate, end);
        return Math.max(0, days);
    }

    /** Fine = Rs 5 x days late. Also updates the fine field. */
    public double calculateFine() {
        this.fine = daysLate() * FINE_PER_DAY;
        return this.fine;
    }

    /** Marks the book returned today and works out the fine. */
    public void markReturned() {
        this.returnDate = LocalDate.now();
        calculateFine();
        this.status = (fine > 0) ? "Overdue" : "Returned";
    }

    @Override
    public String toString() {
        return "IssueRecord{id=" + id + ", bookId=" + bookId
                + ", memberId=" + memberId + ", issue=" + issueDate
                + ", due=" + dueDate + ", return=" + returnDate
                + ", fine=Rs " + fine + ", status='" + status + "'}";
    }

    // ----- getters / setters (dates are LocalDate) -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
