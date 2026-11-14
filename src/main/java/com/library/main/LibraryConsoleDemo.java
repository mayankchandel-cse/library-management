package com.library.main;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.service.FineCalculator;
import com.library.service.LibraryService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

/**
 * Headless console demo of the Spring Boot + JDBC backend.
 * Plain main class - not a @SpringBootApplication, no GUI.
 * Note: a plain AnnotationConfigApplicationContext does not load
 * application.properties by itself (SpringApplication normally does that),
 * so the properties file is loaded explicitly via @PropertySource.
 */
@Configuration
@ComponentScan("com.library")
@EnableAutoConfiguration
@PropertySource("classpath:application.properties")
public class LibraryConsoleDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(LibraryConsoleDemo.class);
        try {
            LibraryService service = context.getBean(LibraryService.class);
            FineCalculator fineCalculator = context.getBean(FineCalculator.class);
            JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

            System.out.println("=== Library Catalogue ===");
            for (Book book : service.getAllBooks()) {
                System.out.println(book.getId() + ". " + book.getTitle() + " by " + book.getAuthor()
                        + " (" + book.getAvailableCopies() + "/" + book.getTotalCopies() + " available)");
            }

            System.out.println("\n=== Issue book 2 to member 1 ===");
            IssueRecord issue = service.issueBook(2, 1);
            System.out.println("Issued: " + issue + " (due " + issue.getDueDate() + ")");

            System.out.println("\n=== Simulate a 5-day-late return ===");
            jdbcTemplate.update("UPDATE issues SET due_date = ? WHERE id = ?",
                    LocalDate.now().minusDays(5), issue.getId());
            double fine = service.returnBook(issue.getId());
            System.out.println("Returned issue " + issue.getId() + ", fine = Rs " + fine
                    + " (5 days late x Rs 5/day = Rs 25.0)");
            System.out.println("FineCalculator check: Rs "
                    + fineCalculator.calculateFine(LocalDate.now().minusDays(5), LocalDate.now()));

            System.out.println("\n=== Search for 'java' ===");
            for (Book book : service.searchBooks("java")) {
                System.out.println(book.getId() + ". " + book.getTitle() + " by " + book.getAuthor());
            }

            System.out.println("\n=== Library Stats ===");
            System.out.println(service.getStats());
        } finally {
            context.close();
        }
    }
}
