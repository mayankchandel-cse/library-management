package com.library.ui;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Student;
import com.library.service.LibraryService;

import java.util.List;
import java.util.Scanner;

/**
 * Simple console menu. Only input and output live here.
 * All rules and all database work are in LibraryService.
 */
public class ConsoleMenu {

    private final LibraryService service;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(LibraryService service) {
        this.service = service;
    }

    public void run() {
        System.out.println("=== Library Management System ===");
        System.out.println("Books, students, issue / return, fines. Nothing else.");
        while (true) {
            System.out.println();
            System.out.println("1. Add a book");
            System.out.println("2. List all books");
            System.out.println("3. Search books");
            System.out.println("4. Register a student");
            System.out.println("5. List students");
            System.out.println("6. Borrowing history of a student");
            System.out.println("7. Issue a book");
            System.out.println("8. Return a book");
            System.out.println("9. Overdue books");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> addBook();
                    case "2" -> service.listBooks().forEach(System.out::println);
                    case "3" -> searchBooks();
                    case "4" -> registerStudent();
                    case "5" -> service.listStudents().forEach(System.out::println);
                    case "6" -> borrowingHistory();
                    case "7" -> issueBook();
                    case "8" -> returnBook();
                    case "9" -> overdueBooks();
                    case "0" -> {
                        System.out.println("Bye!");
                        return;
                    }
                    default -> System.out.println("Please choose 0-9.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addBook() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Number of copies: ");
        int copies = Integer.parseInt(scanner.nextLine().trim());
        service.addBook(title, author, copies);
        System.out.println("Book added.");
    }

    private void searchBooks() {
        System.out.print("Search title or author: ");
        String keyword = scanner.nextLine().trim();
        List<Book> books = service.searchBooks(keyword);
        if (books.isEmpty()) {
            System.out.println("No books found.");
        } else {
            books.forEach(System.out::println);
        }
    }

    private void registerStudent() {
        System.out.print("Student name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        service.registerStudent(name, email);
        System.out.println("Student registered.");
    }

    private void borrowingHistory() {
        System.out.print("Student ID: ");
        int studentId = Integer.parseInt(scanner.nextLine().trim());
        List<IssueRecord> history = service.borrowingHistory(studentId);
        if (history.isEmpty()) {
            System.out.println("No borrowing history for this student.");
        } else {
            history.forEach(System.out::println);
        }
    }

    private void issueBook() {
        System.out.print("Book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Student ID: ");
        int studentId = Integer.parseInt(scanner.nextLine().trim());
        int issueId = service.issueBook(bookId, studentId);
        System.out.println("Issued! Issue ID: " + issueId
                + " | Due in 14 days: " + java.time.LocalDate.now().plusDays(14));
    }

    private void returnBook() {
        System.out.print("Issue ID: ");
        int issueId = Integer.parseInt(scanner.nextLine().trim());
        IssueRecord record = service.returnBook(issueId);
        System.out.println("Returned! Fine: Rs " + record.getFine()
                + " (Rs 5 per day late)");
    }

    private void overdueBooks() {
        List<IssueRecord> overdue = service.overdueBooks();
        if (overdue.isEmpty()) {
            System.out.println("No overdue books.");
            return;
        }
        for (IssueRecord r : overdue) {
            long daysLate = java.time.temporal.ChronoUnit.DAYS.between(
                    r.getDueDate(), java.time.LocalDate.now());
            System.out.println(r + " | days late: " + daysLate);
        }
        System.out.println("Fine rule: Rs 5 per day late.");
    }
}
