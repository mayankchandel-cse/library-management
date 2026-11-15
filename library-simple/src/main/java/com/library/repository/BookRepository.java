package com.library.repository;

import com.library.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Checklist item 6 - JdbcTemplate, in the simplest form.
 *
 * @Repository = "this class talks to the database".
 * JdbcTemplate is made by Spring Boot from the DataSource bean.
 * It opens and closes the connection for us; we only write the SQL
 * and the RowMapper (how one row becomes one Book).
 */
@Repository
public class BookRepository {

    // Spring hands us the JdbcTemplate. We never create it ourselves.
    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> rowMapper = (rs, rowNum) -> new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getInt("total_copies"),
            rs.getInt("available_copies"));

    public void addBook(String title, String author, int copies) {
        jdbcTemplate.update(
                "INSERT INTO books (title, author, total_copies, available_copies) VALUES (?, ?, ?, ?)",
                title, author, copies, copies);
    }

    public void updateBook(int id, String title, String author) {
        jdbcTemplate.update(
                "UPDATE books SET title = ?, author = ? WHERE id = ?",
                title, author, id);
    }

    public void deleteBook(int id) {
        jdbcTemplate.update("DELETE FROM books WHERE id = ?", id);
    }

    public List<Book> findAll() {
        return jdbcTemplate.query(
                "SELECT id, title, author, total_copies, available_copies FROM books ORDER BY id",
                rowMapper);
    }

    public void changeAvailableCopies(int bookId, int change) {
        jdbcTemplate.update(
                "UPDATE books SET available_copies = available_copies + ? WHERE id = ?",
                change, bookId);
    }
}
