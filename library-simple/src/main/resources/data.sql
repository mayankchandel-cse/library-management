-- INSERT IGNORE + the UNIQUE keys in schema.sql mean the sample data
-- is only added once: restarting the app does not duplicate it.
INSERT IGNORE INTO books (title, author, total_copies, available_copies) VALUES
('Wings of Fire', 'A. P. J. Abdul Kalam', 3, 3),
('Let Us C', 'Yashavant Kanetkar', 5, 5),
('Java: The Complete Reference', 'Herbert Schildt', 4, 3),
('Clean Code', 'Robert C. Martin', 3, 2),
('Data Structures in C', 'Reema Thareja', 3, 3);

INSERT IGNORE INTO students (name, email) VALUES
('Aarav Sharma', 'aarav.sharma@example.com'),
('Priya Patel', 'priya.patel@example.com'),
('Rahul Verma', 'rahul.verma@example.com');

INSERT IGNORE INTO issues (book_id, student_id, issue_date, due_date, return_date, fine, status) VALUES
(1, 1, '2026-10-22', '2026-11-05', '2026-11-04', 0, 'Returned'),
(3, 2, '2026-11-06', '2026-11-20', NULL, 0, 'Issued'),
(4, 3, '2026-10-23', '2026-11-06', NULL, 0, 'Overdue');
