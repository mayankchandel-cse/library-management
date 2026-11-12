# Library Management System — Pure Java + Swing

A college-level (B.Tech CSE) library management system built with
**pure Java 17 and Java Swing only**. No database, no server, no JDBC,
no external libraries. Books, members, issues and fines are stored in
Java collections and saved to a file with Java serialization
(`data/library.ser`).

## Features

- Log in as **Admin** (`admin` / `admin123`) or **Librarian**
  (`librarian` / `lib123`)
- Dashboard: total books/copies, members, books issued, overdue, fines due
- Books: browse, search (title / author / category), sort by title
  (Quick Sort), year (Merge Sort) or author, Add / Edit / Delete
- Members: view and add members (Student / Faculty / Staff)
- Issue / Return: issue a book (14-day loan), return it — fine is
  **Rs 5 per day late**, calculated automatically
- Overdue & Fines: overdue list with days late and fine, plus a
  background reminder thread that re-checks every 30 seconds
- Reports: generate a text report (`data/books_report.txt`) and export
  the catalogue as CSV (`data/books_export.csv`)
- Console demo: `com.library.main.LibraryDemo` shows searching, sorting,
  issue/return, fines and file save/load on the console

## Prerequisites

| Item | Version / notes |
|---|---|
| JDK | 17 or later (`java -version`, `javac -version`) |
| IDE (optional) | VS Code, IntelliJ IDEA or Eclipse |
| OS | Windows / macOS / Linux |

No database or server is needed. No JAR to download.

## Quick start — command line

From the project root (`library-management/`):

```bash
# Linux / macOS — NOTE: -encoding UTF-8 is required: some source files
# contain special characters, and javac's default encoding on some
# systems is US-ASCII, which fails the build without it.
javac -encoding UTF-8 -d out $(find src -name "*.java")
java  -cp out com.library.main.Main

# Windows (PowerShell / cmd)
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java  -cp out com.library.main.Main
```

Run the console demo instead:

```bash
java -cp out com.library.main.LibraryDemo
```

## Run it in VS Code

1. Install the **Extension Pack for Java** (Microsoft) from Extensions
   (Ctrl+Shift+X).
2. **File → Open Folder** → select the `library-management/` folder
   (open the *folder*, not a single file).
3. Wait 30–60 seconds for "Importing Java project" to finish.
4. Open `src/com/library/main/Main.java` → click **Run** ▶ above
   `public static void main` (or press **F5** and pick
   **"Run Library System (GUI)"**).
5. Pick **"Run LibraryDemo (console)"** from the same launch menu to
   run the console demo. The `.vscode/` folder in this project already
   has both launch configs.

## Run it in IntelliJ IDEA

1. **File → Open** → select the `library-management/` folder.
2. Set the SDK to **Java 17**: File → Project Structure → SDK.
3. Right-click `src/` → **Mark Directory as → Sources Root**.
4. Open `src/com/library/main/Main.java` → click the green **Run** arrow.

## Run it in Eclipse

1. **File → New → Java Project** → uncheck "use default location" →
   browse to `library-management/`.
2. Set **Java Compiler → Compiler compliance level → 17**.
3. Ensure `src/` is on the build path.
4. Right-click `src/com/library/main/Main.java` → **Run As →
   Java Application**.

## Default logins

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| Librarian | `librarian` | `lib123` |

## Project structure

```
library-management/
├── README.md
├── VIVA_QA.md
├── VIVA_DEMO_SCRIPT.md
├── .vscode/
│   ├── settings.json           # src/ is the source root, output is out/
│   └── launch.json             # Run GUI / Run LibraryDemo configs
├── data/                       # created at runtime: library.ser,
│                               #   books_report.txt, books_export.csv
└── src/com/library/
    ├── main/
    │   ├── Main.java           # entry point (Nimbus L&F, opens LoginFrame)
    │   └── LibraryDemo.java    # console demonstration
    ├── model/                  # Book, Member, User, Librarian,
    │                           #   IssueRecord (Part A)
    ├── storage/
    │   └── DataStore.java      # serialization save/load (Part A)
    ├── service/
    │   └── LibraryService.java # issue/return, fines, search, stats (Part B)
    ├── util/
    │   └── Sorting.java        # Quick Sort, Merge Sort (Part B)
    ├── exceptions/             # LibraryException, BookNotFoundException,
    │                           #   BookNotAvailableException,
    │                           #   InvalidInputException (Part A)
    └── gui/                    # Swing UI (Part C — this part)
        ├── SampleData.java     # 12 books, 5 members, demo accounts
        ├── Theme.java          # colours, fonts, styled widgets
        ├── LoginFrame.java     # login (Admin / Librarian)
        ├── MainFrame.java      # CardLayout shell, sidebar, shared state
        ├── DashboardPanel.java # stats cards, recent issues
        ├── BooksPanel.java     # book table, search, sort, Add/Edit/Delete
        ├── MembersPanel.java   # member table, Add Member form
        ├── IssuePanel.java     # issue (14 days) / return (Rs 5/day fine)
        ├── OverduePanel.java   # overdue list, reminder thread
        └── ReportsPanel.java   # text report + CSV export
```

## How the GUI works

- **Navigation:** `MainFrame` uses a `CardLayout`; the left sidebar
  switches between Dashboard, Books, Members, Issue/Return,
  Overdue & Fines and Reports. The top bar has a global book search.
- **Shared state:** `MainFrame` holds the book, member and issue
  lists, so every panel sees the same data without re-reading the file.
- **Service first, demo fallback:** panels use `LibraryService` when
  it is available; every service call is in try-catch and falls back
  to the in-memory lists from `gui/SampleData.java`, so the GUI never
  goes blank.
- **Business rules:** issue period is 14 days; fine is Rs 5 per day
  late (see `SampleData.FINE_PER_DAY` / `ISSUE_DAYS` and
  `MainFrame.computeFine`).
- **Threading:** the overdue reminder runs on a daemon thread and
  updates the UI only through `SwingUtilities.invokeLater`, so the
  Swing event-dispatch thread is never blocked.

## Service / model API (integration contract — Part C calls these)

```java
// model — bean style: no-arg constructor + setters/getters
Book:        getId/getTitle/getAuthor/getIsbn/getCategory/getPublisher/
             getYear/getCopies/getAvailableCopies (+ matching setters)
Member:      getId/getName/getEmail/getPhone/getMembershipType (+ setters)
User:        getId/getName/getUsername/getPassword/getRole (+ setters)
Librarian extends User
IssueRecord: getId/getBookId/getMemberId/getIssueDate/getDueDate/
             getReturnDate/getFine/getStatus (+ setters);
             getIssueDate()/getDueDate()/getReturnDate() are LocalDate

// service — instance methods, no-arg constructor; all synchronized
LibraryService:
    Book              addBook(Book book)          // throws InvalidInputException
    boolean           removeBook(int bookId)      // no updateBook: GUI does remove + add
    List<Book>        getAllBooks()
    Book              getBookById(int bookId)
    List<Book>        searchBooks(String query)
    Member            registerMember(Member member)
    List<Member>      getAllMembers()
    Member            getMemberById(int memberId)
    IssueRecord       issueBook(int bookId, int memberId)  // throws InvalidInputException
    double            returnBook(int issueId)     // returns the fine (Rs)
    IssueRecord       getIssueById(int issueId)
    List<IssueRecord> getAllIssues()
    List<IssueRecord> getOverdueRecords()
    Map<String,Object> getStats()                 // totalBooks, issued, overdue, ...
    List<Book>        getMostIssuedBooks()

// NOTE: the service mutates the shared Book objects (issue decrements
// availableCopies, return increments it), so the GUI must not repeat
// those updates on the service path — see MainFrame.issueBook/returnBook.
```

## Troubleshooting

| Problem | Fix |
|---|---|
| `javac: command not found` | Install JDK 17 and put it on `PATH`; check `java -version`. |
| `UnsupportedClassVersionError` | You compiled with JDK 17+ but are running an older JRE (or vice versa) — use the same JDK for both. |
| Red squiggles in VS Code / "cannot resolve" | Java server still loading — wait, then **Ctrl+Shift+P → Java: Clean Java Language Server Workspace** and reload. |
| Run button missing above `main` in VS Code | Extension Pack for Java isn't installed, or you opened a file instead of the folder. |
| GUI looks unstyled / tiny fonts | Nimbus L&F is optional; the app falls back to the platform L&F. Colours are set explicitly in `gui/Theme.java`. |
| `data/library.ser` appears in the project folder | Normal — that's the serialized data file. Delete it to reset to the sample data. |
| A book cannot be deleted | Correct behaviour: a book that is currently issued cannot be deleted — return it first. |
