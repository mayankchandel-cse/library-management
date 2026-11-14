INSERT INTO books (title, author, isbn, category, publication_year, total_copies, available_copies) VALUES
('Wings of Fire', 'A. P. J. Abdul Kalam', '9788173711466', 'Biography', 1999, 3, 3),
('Let Us C', 'Yashavant Kanetkar', '9788183331630', 'Programming', 2016, 5, 5),
('Java: The Complete Reference', 'Herbert Schildt', '9781260440232', 'Programming', 2018, 4, 3),
('Data Structures in C', 'Reema Thareja', '9780198099307', 'Programming', 2011, 3, 3),
('Operating System Concepts', 'Silberschatz', '9781118063330', 'Programming', 2018, 4, 4),
('Clean Code', 'Robert C. Martin', '9780132350884', 'Software Engineering', 2008, 3, 2),
('Computer Networks', 'Andrew S. Tanenbaum', '9780132126953', 'Networking', 2010, 2, 2),
('Data Structures', 'Seymour Lipschutz', '9780070701988', 'Programming', 2014, 3, 3),
('Database System Concepts', 'Silberschatz', '9780078022159', 'Database', 2019, 4, 4),
('Discrete Mathematics', 'Kenneth H. Rosen', '9780072899054', 'Mathematics', 2018, 3, 3),
('Head First Design Patterns', 'Eric Freeman', '9780596007126', 'Software Engineering', 2004, 2, 2),
('The Alchemist', 'Paulo Coelho', '9780062315007', 'Fiction', 2014, 5, 5);

INSERT INTO members (name, email, phone, member_type, max_books) VALUES
('Aarav Sharma', 'aarav.sharma@example.com', '9876543210', 'Student', 3),
('Priya Patel', 'priya.patel@example.com', '9876543211', 'Faculty', 5),
('Sneha Iyer', 'sneha.iyer@example.com', '9876543212', 'Student', 3),
('Rahul Verma', 'rahul.verma@example.com', '9876543213', 'Staff', 4),
('Ananya Gupta', 'ananya.gupta@example.com', '9876543214', 'Student', 3);

INSERT INTO issues (book_id, member_id, issue_date, due_date, return_date, fine, status) VALUES
(1, 1, '2026-10-22', '2026-11-05', '2026-11-04', 0, 'Returned'),
(3, 2, '2026-11-06', '2026-11-20', NULL, 0, 'Issued'),
(6, 3, '2026-10-23', '2026-11-06', NULL, 30, 'Overdue');
