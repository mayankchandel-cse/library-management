# Library Management System — Spring Boot + JDBC

A library management system for a college project (Topic 2). It manages
books, members, issuing and returning books, overdue books and fines.
The desktop GUI is built with Java Swing, and all database work goes
through Spring Boot with JDBC (`JdbcTemplate`) and MySQL / MariaDB.

There is **no login screen** in this version — the application opens
straight into the Dashboard.

## Prerequisites

- JDK 17
- Maven 3.9.9
- MariaDB (or MySQL) running on your computer, port 3306

## Create the database

Run this once in a terminal:

```bash
sudo mysql -u root -e "CREATE DATABASE IF NOT EXISTS library_db; CREATE USER IF NOT EXISTS 'library'@'localhost' IDENTIFIED BY 'library123'; GRANT ALL PRIVILEGES ON library_db.* TO 'library'@'localhost'; FLUSH PRIVILEGES;"
```

The application connects as user `library` with password `library123`
to the database `library_db`. The tables and sample data are created
automatically on startup by `schema.sql` and `data.sql`
(`spring.sql.init.mode=always` in `application.properties`).

## Run the application

```bash
mvn spring-boot:run
```

The Dashboard window opens. Use the sidebar on the left:

- **Dashboard** — live counts: total books, total members, issued now,
  overdue, fines due.
- **Books** — search by title or author, add, edit or delete books.
- **Members** — register a member; click a member row to see their
  borrowing history.
- **Issue / Return** — issue a book (due in 14 days) or return a book
  by issue ID and see the fine.
- **Overdue & Fines** — overdue books, days late, fine per book.
  Fine rule: **Rs 5 per day late.**

There is also a console demo: `LibraryConsoleDemo` (run it from
VS Code, see below). It shows the same service calls in the terminal.

## Run in VS Code

1. Open this folder in VS Code.
2. Install the **Extension Pack for Java** if it is not installed yet.
3. Press **F5** and pick **Run Library System (Spring GUI)**, or open
   `src/main/java/com/library/main/LibraryApplication.java` and click
   **Run** above `main`.
4. To run the console demo instead, pick
   **Run LibraryConsoleDemo** from the run menu.

The launch settings are in `.vscode/launch.json`.

## Project structure

```
library-management-spring/
├── pom.xml                                  Spring Boot 3.2.x, spring-boot-starter-jdbc,
│                                            spring-boot-starter, mysql-connector-j
├── src/main/java/com/library/
│   ├── main/
│   │   ├── LibraryApplication.java          Entry point: starts Spring (no web server),
│   │   │                                    gets the LibraryService bean, opens the GUI
│   │   └── LibraryConsoleDemo.java          Console demo of the same service
│   ├── model/
│   │   ├── Book.java                        Book: id, title, author, ISBN, category,
│   │   │                                    year, total / available copies
│   │   ├── Member.java                      Member: id, name, email, phone, type, max books
│   │   └── IssueRecord.java                 Issue: book, member, dates, fine, status
│   ├── repository/
│   │   ├── BookRepository.java              Book SQL with JdbcTemplate
│   │   ├── MemberRepository.java            Member SQL with JdbcTemplate
│   │   └── IssueRepository.java             Issue / return / overdue SQL
│   ├── service/
│   │   ├── LibraryService.java              Business rules: issue, return, fines, stats
│   │   └── FineCalculator.java              Fine = Rs 5 x days late
│   └── gui/
│       ├── Theme.java                       Navy #14213D / amber #FCA311 look, shared helpers
│       ├── MainFrame.java                   Window, sidebar, CardLayout, top search bar
│       ├── DashboardPanel.java              Stat cards + how-to-use guide
│       ├── BooksPanel.java                  Book table, search, add / edit / delete
│       ├── MembersPanel.java                Member table, add form, borrowing history
│       ├── IssuePanel.java                  Issue form, return form, due date / fine message
│       └── OverduePanel.java                Overdue table, days late, fine, refresh
├── src/main/resources/
│   ├── application.properties               Database connection, spring.sql.init.mode=always
│   ├── schema.sql                           CREATE TABLE books, members, issues
│   └── data.sql                             Sample books, members and issues
├── .vscode/
│   ├── settings.json
│   └── launch.json
├── README.md
├── VIVA_QA.md                               12 viva questions and answers
└── VIVA_DEMO_SCRIPT.md                      2-minute demo script
```

## How each Topic-2 requirement is met

| Requirement | Where it is met |
|---|---|
| 1. Book management (add, update, delete, search) | `BooksPanel.java` + `LibraryService` (`addBook`, `updateBook`, `deleteBook`, `searchBooks`) + `BookRepository.java` (SQL LIKE search) |
| 2. Member management (register, borrowing history) | `MembersPanel.java` + `LibraryService` (`registerMember`, `getBorrowingHistory`) + `MemberRepository.java` |
| 3. Issue and return with due date (14 days) | `IssuePanel.java` + `LibraryService` (`issueBook`, `returnBook`) + `IssueRepository.java` |
| 4. Fine calculation (Rs 5 per day late) | `FineCalculator.java` + `IssuePanel.java` (shows the fine) + `OverduePanel.java` (fine per book) |
| 5. Overdue tracking | `OverduePanel.java` + `LibraryService.getOverdueRecords()` + `IssueRepository.java` |
| 6. Reports / statistics (dashboard counts) | `DashboardPanel.java` + `LibraryService.getStats()` (total books, total members, issued, overdue, total fines) |

## Troubleshooting

- **Cannot connect to the database / port 3306:** check MariaDB is
  running (`sudo systemctl status mariadb`) and that the database and
  user exist — run the `CREATE DATABASE` / `CREATE USER` commands above
  again.
- **Tables are empty or missing:** the schema is re-created on every
  start because `spring.sql.init.mode=always` is set in
  `application.properties`. Stop the app, check the database, and run
  it again. You can also re-run `schema.sql` and `data.sql` by hand in
  the `mysql` client.
- **Wrong Java version:** run `java -version` — it must say 17.
  If another version is used, set `JAVA_HOME` to your JDK 17 folder.
- **Maven not found:** use the full path to Maven 3.9.9, or add its
  `bin` folder to your `PATH`.
- **GUI does not open:** make sure you ran `LibraryApplication`
  (the GUI), not only the console demo, and check the terminal for a
  database error first — the window only opens after Spring starts.
