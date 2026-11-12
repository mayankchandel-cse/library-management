# Library Management System — 2-minute viva demo script (VS Code)

Not a report — a timed script to read / rehearse before the viva.
Run it from VS Code using the launch configs already in `.vscode/launch.json`.
Pure Java 17 + Swing — no database, no server.

## 30-second pre-flight (do before the viva)

1. JDK 17: `java -version` and `javac -version` both say 17+.
2. Open the **folder** `library-management/` in VS Code (not a single
   file), wait for "Importing Java project" to finish, Extension Pack
   for Java installed.
3. Know the logins: admin / admin123, librarian / lib123.
4. Know the two rules: issue period is **14 days**, fine is
   **Rs 5 per day late**.
5. If anything looks empty, the app falls back to the built-in sample
   data (12 books, 5 members) — say so, and carry on.

## The 2-minute script

**0:00–0:20 — What it is, and log in**
> "Library Management System, built in pure Java and Swing — no
> database. Books, members, issues and fines, saved to a file with
> Java serialization."

Open `src/com/library/main/Main.java`, press F5, pick
**"Run Library System (GUI)"**. Log in as `admin` / `admin123`.
Say: "Admin and Librarian are separate roles."

**0:20–0:50 — Books: search + sort**
Click **Books**. In the search box type `Java` — the list filters to
the Java books. Change **Sort by** and name the algorithm each time:
- "Title — Quick Sort" → O(n log n) average.
- "Year — Merge Sort" → O(n log n), stable.
Say: "The sort menu calls the real sort code in the panel, not just
a library sort. Add / Edit / Delete are on the same screen."

**0:50–1:20 — Issue a book, then return it late and show the fine**
Click **Issue / Return**. Issue book id **2** ("Java: The Complete
Reference") to member id **1** (Aarav Sharma).
Say: "Due date is today + 14 days."
Then return issue id **3** — the overdue sample record (due 6 days
ago). Say: "Fine is Rs 5 per day late, so 6 days = Rs 30. The copy
goes back on the shelf and the record becomes Returned."

**1:20–1:45 — Overdue + Reports**
Click **Overdue & Fines**: "This list is recomputed from the due
dates, and a background reminder thread re-checks it every
30 seconds without blocking the screen."
Click **Reports** → **Generate Report**: "The report is written to
data/books_report.txt, and Export CSV writes
data/books_export.csv."

**1:45–2:00 — Close on the console demo**
> "The same logic runs on the console in LibraryDemo — searching,
> sorting, issue/return and the fine calculation, plus the file
> save and load."

Run **"Run LibraryDemo (console)"** from the same launch menu and
show the output. If the examiner asks a concept question, answers
are in `VIVA_QA.md`.

## If something goes wrong live

| Problem | What to say / do |
|---|---|
| Login fails | Use the demo buttons on the login screen: "Use Admin" / "Use Librarian". |
| A book won't issue | It has 0 available copies — pick another book id, or return one first. |
| A book won't delete | Correct behaviour: a book that is currently issued cannot be deleted — return it first. |
| `javac: command not found` | JDK 17 isn't on PATH — say so, and run the already-compiled `out/` folder if you have it. |
| No data file yet | Normal on first run — the app loads the 12 sample books and 5 sample members. |
