# Library Management System — Simple Version

A simple library system that does **only** these 6 things, nothing else:

| # | What it does | Where in the code |
|---|---|---|
| 1 | Maintain book details — book ID, title, author, availability | `Book.java`, `BookRepository.java` |
| 2 | Register students and maintain their borrowing history | `Student.java`, `StudentRepository.java` (`borrowingHistory` JOIN) |
| 3 | Issue and return books (due in 14 days) | `LibraryService.issueBook` / `returnBook`, `IssueRepository.java` |
| 4 | Calculate fines for overdue books — **Rs 5 per day late** | `FineCalculator.java` (only place the fine is calculated) |
| 5 | Use JDBC `PreparedStatement` and `ResultSet` for database operations | `BookJdbcDao.java` — plain JDBC, no JdbcTemplate |
| 6 | Use Spring Boot annotations and `JdbcTemplate` | `@SpringBootApplication`, `@Configuration` + `@Bean` (`DatabaseConfig`), `@Service`, `@Repository`, `@Component`, `@Transactional` — `JdbcTemplate` in the repositories |

No GUI, no login screen, no reminders, no reports, no extra features.
The app is a console menu, so it is easy to run and easy to explain in a viva.

## Item 6 — Spring Boot annotations + JdbcTemplate, in 5 lines

1. `@SpringBootApplication` (`LibraryApplication`) starts Spring.
2. `@Configuration` + `@Bean` (`DatabaseConfig`) make one `DataSource` — the MySQL connection. Spring Boot then makes a `JdbcTemplate` from it.
3. `@Repository` (`BookRepository`, `StudentRepository`, `IssueRepository`) run all SQL through that `JdbcTemplate`. Spring hands it to them; they never open a connection themselves.
4. `@Service` (`LibraryService`) holds the rules, `@Component` (`FineCalculator`) holds the fine. Spring creates one of each and wires them together.
5. `@Transactional` (on `issueBook` / `returnBook`) means: insert the issue row AND change the available copies together, or neither happens.

That is the whole of item 6. `BookJdbcDao` is the one exception on purpose — it uses plain JDBC (`PreparedStatement` + `ResultSet`) with no JdbcTemplate, because that is item 5.

## Prerequisites

- JDK 17
- Maven 3.9.9
- MySQL running on your computer, port 3306

## Create the database (once)

Run this once as MySQL root. It makes a **separate** database
`library_simple_db` — separate from the full project's `library_db`,
because that project uses different tables (`members` / `member_id`,
extra book columns), so sharing one database would clash.
The `library` user already exists from this morning, so only the
new database and grant are needed:

```sql
CREATE DATABASE IF NOT EXISTS library_simple_db;
GRANT ALL PRIVILEGES ON library_simple_db.* TO 'library'@'localhost';
FLUSH PRIVILEGES;
```

In VS Code PowerShell, if `mysql` is not recognised, use the full path,
for example:
```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -p
```

The tables and sample data are created automatically on startup
by `schema.sql` and `data.sql`
(`spring.sql.init.mode=always` in `application.properties`).
The sample data uses `INSERT IGNORE` with UNIQUE keys, so restarting
the app does not add the same books/students/issues again.

## Run the application

```bash
mvn spring-boot:run
```

In VS Code: open this folder, press **F5** and pick
**Run Library (Simple)**.

You will see a menu:

```
1. Add a book
2. List all books
3. Search books
4. Register a student
5. List students
6. Borrowing history of a student
7. Issue a book
8. Return a book
9. Overdue books
0. Exit
```

## Quick demo (2 minutes)

1. Choose **2** — you see 5 sample books.
2. Choose **7**, Book ID `2`, Student ID `1` — issued, due in 14 days.
3. Choose **8**, Issue ID `3` — this is the overdue sample record
   (due 6 Nov). It says: `Returned! Fine: Rs 5 × days late` —
   Rs 45 if you return it today, 15 Nov (9 days late × Rs 5).
   The fine is calculated from today's date, so it grows by Rs 5 a day.
4. Choose **9** — overdue list.
5. Choose **6**, Student ID `1` — borrowing history of Aarav Sharma.

## Project structure

```
library-simple/
├── pom.xml
├── src/main/java/com/library/
│   ├── LibraryApplication.java   Entry point, starts Spring, opens the menu
│   ├── config/
│   │   └── DatabaseConfig.java   @Configuration + @Bean DataSource
│   ├── model/
│   │   ├── Book.java             id, title, author, total / available copies
│   │   ├── Student.java          id, name, email
│   │   └── IssueRecord.java      issue, due date, return date, fine, status
│   ├── repository/
│   │   ├── BookJdbcDao.java      Plain JDBC: PreparedStatement + ResultSet
│   │   ├── BookRepository.java   JdbcTemplate: add / update / delete / list books
│   │   ├── StudentRepository.java JdbcTemplate: register, borrowing history JOIN
│   │   └── IssueRepository.java  JdbcTemplate: issue, return, overdue
│   ├── service/
│   │   ├── LibraryService.java   @Service, @Transactional issue / return
│   │   └── FineCalculator.java   @Component, fine = Rs 5 x days late
│   └── ui/
│       └── ConsoleMenu.java      Console menu only, no database code here
├── src/main/resources/
│   ├── application.properties   Database connection
│   ├── schema.sql               CREATE TABLE books, students, issues
│   └── data.sql                 5 books, 3 students, 3 issues
└── .vscode/
    └── launch.json
```

## Viva — short answers

- **PreparedStatement vs Statement?** A `Statement` runs SQL as a plain
  string, so user input joined into it can change the query (SQL injection).
  A `PreparedStatement` uses `?` placeholders and the values are sent
  separately, so they are treated as data only. See `BookJdbcDao.java`.
- **What is a ResultSet?** The rows returned by a `SELECT`. We read them
  with `next()`, `getInt("id")`, `getString("title")` — see `BookJdbcDao.java`.
- **What does JdbcTemplate do?** It is Spring's helper on top of JDBC.
  It opens and closes the connection and runs the query; we only write
  the SQL and the RowMapper — see `BookRepository.java`.
- **What is @Transactional?** Issue and return are two steps that must
  happen together (insert the issue row AND change the available copies).
  If one fails, both are rolled back — see `LibraryService.java`.
- **Where is the fine calculated?** Only in `FineCalculator.java`:
  Rs 5 per day late.
