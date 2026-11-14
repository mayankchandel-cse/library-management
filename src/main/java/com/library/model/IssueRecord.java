package com.library.model;

import java.time.LocalDate;

public class IssueRecord {
    private int id;
    private int bookId;
    private int memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private String status;
    // Display fields filled from JOINs with books and members
    private String bookTitle;
    private String memberName;

    public IssueRecord() {
    }

    public IssueRecord(int id, int bookId, int memberId, LocalDate issueDate, LocalDate dueDate,
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

    public IssueRecord(int id, int bookId, int memberId, LocalDate issueDate, LocalDate dueDate,
                       LocalDate returnDate, double fine, String status,
                       String bookTitle, String memberName) {
        this(id, bookId, memberId, issueDate, dueDate, returnDate, fine, status);
        this.bookTitle = bookTitle;
        this.memberName = memberName;
    }

    public boolean isOverdue() {
        return returnDate == null && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {
        return "IssueRecord{id=" + id + ", bookId=" + bookId + ", memberId=" + memberId
                + ", issueDate=" + issueDate + ", dueDate=" + dueDate
                + ", returnDate=" + returnDate + ", fine=" + fine + ", status='" + status + '\''
                + ", bookTitle='" + bookTitle + '\'' + ", memberName='" + memberName + '\'' + '}';
    }
}
