DROP TABLE IF EXISTS issues;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS books;

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    category VARCHAR(50),
    publication_year INT CHECK (publication_year > 0),
    total_copies INT CHECK (total_copies >= 0),
    available_copies INT CHECK (available_copies >= 0)
);

CREATE TABLE members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15),
    member_type VARCHAR(20) CHECK (member_type IN ('Student', 'Faculty', 'Staff')),
    max_books INT
);

CREATE TABLE issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine DECIMAL(8, 2) DEFAULT 0,
    status VARCHAR(20),
    FOREIGN KEY (book_id) REFERENCES books (id),
    FOREIGN KEY (member_id) REFERENCES members (id)
);
