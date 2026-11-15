# Library Management System — Project Report

**Source Code Repository:** https://github.com/mayankchandel-cse/library-management

**Report PDF in this repository:** `Library_Management_System_Report.pdf`
(26 pages. The updated PDF, with the repository link on the title page, in the Abstract, and as Reference 9, is attached to the project chat — upload it over the existing PDF in GitHub to replace it.)

Submitted by Mayank Chandel (25BCS10333), Shivansh (25BCS11580), Madhav Anand (25BCS11087), in partial fulfilment for the award of the degree of Bachelor of Technology in Computer Science and Engineering, Chandigarh University.

## What this project is

A Library Management System for a college library, built with Java 17, Java Swing for the screens, Spring Boot 3.2.5, and JDBC / Spring JdbcTemplate for database work, with a MySQL / MariaDB database named `library_db`.

It manages books, members, issuing and returning books, overdue books, and fines. There is no login screen — the application opens straight into the Dashboard.

## Features (all 6 case-study requirements)

1. **Book management** — add, update, delete, and search books by title or author (SQL `LIKE` search).
2. **Member management** — register a member; click a member row to see their full borrowing history.
3. **Issue and return** — issue a book with a due date 14 days from the issue date; return by issue ID.
4. **Fine calculation** — Rs 5 per day late, in one place only (`FineCalculator`).
5. **Overdue tracking** — overdue books with days late and fine per book.
6. **Reports / statistics** — Dashboard counts: total books, total members, issued now, overdue, total fines due.

Both database methods asked for in the case study are used: plain JDBC with `PreparedStatement` and `ResultSet` (`BookJdbcDao`), and Spring JdbcTemplate with Spring Boot annotations (`@SpringBootApplication`, `@Repository`, `@Service`, `@Configuration`, `@Bean`).

## Database

Tables: `books`, `members`, `issues` — see `src/main/resources/schema.sql` and `data.sql`.
Spring Boot runs both files on every start (`spring.sql.init.mode=always` in `application.properties`), so only the empty database and user need to be created once:

```sql
CREATE DATABASE IF NOT EXISTS library_db;
CREATE USER IF NOT EXISTS 'library'@'localhost' IDENTIFIED BY 'library123';
GRANT ALL PRIVILEGES ON library_db.* TO 'library'@'localhost';
FLUSH PRIVILEGES;
```

The application connects as user `library` to `library_db` on `localhost:3306`.

## Verified test results

Verified on a live MariaDB 10.11 database (JDK 17, Maven 3.9.9, Spring Boot 3.2.5):

- Books: 12 rows, 41 total copies, 39 available copies.
- Members: 5 rows.
- Issues: 3 rows — 1 Returned, 1 Issued, 1 Overdue.
- Search `title LIKE '%java%'` returns book 3, "Java: The Complete Reference" by Herbert Schildt, 3 copies available.
- Borrowing history for member 1: Aarav Sharma, "Wings of Fire", issued 2026-10-22, due 2026-11-05, status Returned.
- Overdue query returns: "Clean Code", 6 days late, fine Rs 30 (6 x Rs 5).
- Console demo: issue a book, return it 5 days late, fine Rs 25.

## How to run

```bash
mvn spring-boot:run
```

In VS Code: open this folder, install the Extension Pack for Java, press F5 and pick **Run Library System (Spring GUI)**.

## Project structure

```
library-management-spring/
├── pom.xml
├── src/main/java/com/library/
│   ├── main/          LibraryApplication.java, LibraryConsoleDemo.java
│   ├── model/         Book.java, Member.java, IssueRecord.java
│   ├── repository/    BookRepository.java, MemberRepository.java, IssueRepository.java, BookJdbcDao.java
│   ├── service/       LibraryService.java, FineCalculator.java
│   ├── config/        DatabaseConfig.java
│   └── gui/           MainFrame.java, DashboardPanel.java, BooksPanel.java,
│                      MembersPanel.java, IssuePanel.java, OverduePanel.java, Theme.java
├── src/main/resources/
│   ├── application.properties
│   ├── schema.sql
│   └── data.sql
├── README.md
├── VIVA_QA.md
├── VIVA_DEMO_SCRIPT.md
├── Library_Management_System_Report.pdf
└── PROJECT_REPORT.md
```

## Repository

https://github.com/mayankchandel-cse/library-management
