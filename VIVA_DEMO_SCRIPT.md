# Viva Demo Script — Library Management System (Spring Boot + JDBC)

2-minute timed demo. Run the GUI first with `mvn spring-boot:run`.
Keep the console demo ready in a second terminal
(`mvn spring-boot:run -Dspring-boot.run.main-class=com.library.main.LibraryConsoleDemo`
or run `LibraryConsoleDemo` from VS Code).

## 0:00 — Open the app, Dashboard counts

> "This is my Library Management System, built with Spring Boot, JDBC
> and Swing. There is no login screen — it opens straight into the
> Dashboard."

Open the app. Point at the Dashboard stat cards and read them out:
total books, total members, issued now, overdue, and fines due in Rs.
Say: "These numbers come live from the MySQL database through the
`LibraryService` — the GUI gets the Spring bean, it does not create the
service itself."

## 0:20 — Books search "java" (SQL LIKE)

> "Now I search for books. This search is a SQL LIKE query, not a
> sorting algorithm."

Click **Books** in the sidebar. Type `java` in the search box and click
**Search**. The table shows only the matching books (title or author).
Say: "The panel calls `service.searchBooks("java")`, the repository runs
`SELECT ... WHERE title LIKE ? OR author LIKE ?` with a
`PreparedStatement`, so it is safe from SQL injection."
Click **Show All** to bring all books back.

## 0:50 — Issue book 2 to member 1, due +14 days

> "Next I issue a book. The due date is 14 days from today."

Click **Issue / Return**. Type Book ID `2` and Member ID `1`, then click
**Issue Book**. Read the full message out loud, for example:
"Issued! Issue ID: ... — Due date: 2026-11-26 (14 days from today...)."
Say: "The service inserts the issue row and reduces the available
copies in one transaction, so both happen together or not at all."

## 1:20 — Return overdue issue 3, Rs 30 fine

> "Now I return an overdue book and the fine is calculated."

In the **Return a Book** section, type Issue ID `3` and click
**Return Book**. Read the message: "Returned! Issue ID: 3 — Fine:
Rs 30 (Rs 5 per day late)." Say: "Issue 3 was 6 days late, and the
`FineCalculator` does Rs 5 times days late, so Rs 30. If it were on
time, the fine would be Rs 0."

## 1:45 — Overdue panel + console demo close

> "Finally, the overdue list and the console demo."

Click **Overdue & Fines**. Show the table: issue ID, book, member, due
date, days late and fine. Point at the footer: "Fine rule: Rs 5 per day
late." Then switch to the terminal and run `LibraryConsoleDemo`. Point
at its output: it issues/returns through the same `LibraryService` and
shows a **Rs 25 fine demo** (5 days late x Rs 5).

> "That is the full flow: dashboard, search, issue, return with fine,
> and overdue list — all through Spring Boot and JDBC. Thank you."

## If something goes wrong

- If the app does not start: say the database is checked first —
  MariaDB must be running and `library_db` must exist — then start again.
- If a search shows nothing: check the spelling, then click **Show All**.
- If issuing fails: the book may have 0 available copies, or the member
  may already be at their book limit — read the error message on screen.
