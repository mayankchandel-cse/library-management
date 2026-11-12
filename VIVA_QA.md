# Library Management System — Viva Q&A (short answers)

## OOP

1. **Inheritance in this project?** `Librarian extends User` — a librarian
   *is a* user with a role. `User` holds id, name, username, password, role;
   `Librarian` adds librarian-specific behaviour.
2. **Polymorphism?** A `User` reference can hold a `Librarian` (or any
   subclass); overridden methods (e.g. `getRole()` / `displayInfo()`)
   are chosen at run time. The GUI holds users as `User` and branches on
   `getRole()` — "ADMIN" vs "LIBRARIAN".
3. **Encapsulation?** All model fields are `private` with public
   getters/setters — e.g. `Book.setAvailableCopies()` is the only way the
   count changes, so issue/return logic cannot corrupt it directly.
4. **Abstraction?** The service layer (`LibraryService`) hides storage
   details: the GUI calls `issueBook()` without knowing whether data is
   in memory or in a file. Abstract classes / interfaces define *what*,
   concrete classes define *how*.
5. **Constructor overloading?** `Book` has a no-arg constructor (used by
   deserialization) and a full constructor with all fields; the GUI uses
   the full one when adding a book.

## Collections

6. **ArrayList vs LinkedList?** `ArrayList` for books/members — random
   access by index is O(1) and we mostly iterate and search. `LinkedList`
   would be better for frequent insert/delete in the middle; we use it
   conceptually for the issue queue. `HashMap` (id → Book) gives O(1)
   average lookup by id, vs O(n) linear search through a list.
7. **Searching / sorting complexities?** Linear search O(n); binary search
   O(log n) but needs a sorted list. Quick Sort O(n log n) average,
   O(n²) worst; Merge Sort O(n log n) always, stable, needs O(n) extra
   space. The Books screen sorts by title with Quick Sort and by year
   with Merge Sort.

## Exceptions, files, threads

8. **Exception handling?** `try-catch-finally`: invalid input throws
   `InvalidInputException` (checked, custom), a missing book throws
   `BookNotFoundException`, issuing an unavailable book throws
   `BookNotAvailableException`. File I/O is in try-with-resources so
   streams close even on error.
9. **File handling / serialization?** `DataStore` writes the whole
   library (books, members, issues) with `ObjectOutputStream` to
   `data/library.ser` and reads it back with `ObjectInputStream`.
   Model classes implement `Serializable`; `serialVersionUID` keeps
   old files readable. Reports use `FileWriter`/`PrintWriter`.
10. **Multithreading?** The overdue reminder runs on a daemon thread
    that sleeps 30 seconds, then updates the UI via
    `SwingUtilities.invokeLater` (Swing is single-threaded — only the
    EDT may touch components). Shared lists are guarded with
    `synchronized` methods in the service so issue and return cannot
    corrupt the available-copies count at the same time.

## Swing

11. **Swing structure?** `JFrame` is the window (`LoginFrame`,
    `MainFrame`); `JPanel` + `CardLayout` switches screens;
    `JTable` + `DefaultTableModel` shows books/members/issues;
    `JTextField`/`JButton`/`JComboBox` are the inputs. All Swing code
    runs on the EDT via `SwingUtilities.invokeLater`.
12. **Why no database?** The syllabus topic is Java only — collections
    + serialization are enough for a college library, and the design
    keeps storage behind `DataStore`, so a database could replace it
    later without touching the GUI.
