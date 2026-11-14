# Verification notes — Spring Boot Library Management System

Verified by running the project on 2026-11-12 (MariaDB 10.11, JDK 17, Maven 3.9.9).
Use only these numbers in the project report. Do not invent other outputs.

## Build
- `mvn -o clean compile`: exit 0, 19 Java source files.
- Spring Boot 3.2.5, Java 17, spring-boot-starter-jdbc, mysql-connector-j 8.3.0.
- `mvn -o package` fails offline (surefire plugin not cached). On a machine with
  internet it resolves normally. Run with `mvn spring-boot:run`.

## Database (live, MariaDB 10.11, database library_db)
- `schema.sql` + `data.sql` ran cleanly.
- Books: 12 rows, 41 total copies, 39 available copies.
- Members: 5 rows.
- Issues: 3 rows — 1 Returned, 1 Issued, 1 Overdue.
- Search `title LIKE '%java%'` returns book 3, "Java: The Complete Reference"
  by Herbert Schildt, 3 copies available.
- Borrowing history JOIN for member 1 returns: Aarav Sharma, "Wings of Fire",
  issued 2026-10-22, due 2026-11-05, status Returned.
- Overdue query returns: "Clean Code", 6 days late, fine Rs 30 (6 x Rs 5).

## Fine rule
- Rs 5 per day late. 14-day loan period. Due date = issue date + 14 days.
- The console demo (issue a book, return it 5 days late, fine Rs 25) is in
  `LibraryConsoleDemo.java` but could NOT be run in this sandbox: Java TCP
  connections are blocked here, so the demo and the GUI could not reach the
  database from Java. The same applies to GUI screenshots: there are none yet.
  The app must be run on a normal machine (the student's laptop) for the demo
  and screenshots. The Spring Boot application itself starts cleanly
  ("Started LibraryApplication in 2.562 seconds").

## Seed data fix applied 2026-11-12
- Book 2 "Let Us C" was listed as 5 total / 4 available with no active issue
  record. Fixed in data.sql to 5 total / 5 available. Counts above are after
  the fix.

## What was removed (not in the Topic 2 requirements)
- No login screen, no User/Librarian classes.
- No reminder thread, no reports/CSV export, no sorting-algorithm demos.
- Only panels: Dashboard, Books, Members, Issue/Return, Overdue & Fines.
