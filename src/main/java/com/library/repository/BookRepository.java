package com.library.repository;

import com.library.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final BeanPropertyRowMapper<Book> ROW_MAPPER =
            new BeanPropertyRowMapper<>(Book.class);

    public List<Book> findAll() {
        return jdbcTemplate.query(
                "SELECT id, title, author, isbn, category, publication_year, total_copies, available_copies "
                        + "FROM books ORDER BY id", ROW_MAPPER);
    }

    public Book findById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, title, author, isbn, category, publication_year, total_copies, available_copies "
                            + "FROM books WHERE id = ?", ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Book> searchByTitleOrAuthor(String keyword) {
        String pattern = "%" + keyword + "%";
        return jdbcTemplate.query(
                "SELECT id, title, author, isbn, category, publication_year, total_copies, available_copies "
                        + "FROM books WHERE title LIKE ? OR author LIKE ? ORDER BY id",
                ROW_MAPPER, pattern, pattern);
    }

    public void save(Book book) {
        jdbcTemplate.update(
                "INSERT INTO books (title, author, isbn, category, publication_year, total_copies, available_copies) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                book.getTitle(), book.getAuthor(), book.getIsbn(), book.getCategory(),
                book.getPublicationYear(), book.getTotalCopies(), book.getAvailableCopies());
    }

    public void update(Book book) {
        jdbcTemplate.update(
                "UPDATE books SET title = ?, author = ?, isbn = ?, category = ?, "
                        + "publication_year = ?, total_copies = ?, available_copies = ? WHERE id = ?",
                book.getTitle(), book.getAuthor(), book.getIsbn(), book.getCategory(),
                book.getPublicationYear(), book.getTotalCopies(), book.getAvailableCopies(),
                book.getId());
    }

    public void deleteById(int id) {
        jdbcTemplate.update("DELETE FROM books WHERE id = ?", id);
    }

    public int countBooks() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM books", Integer.class);
        return count == null ? 0 : count;
    }

    public int countTotalCopies() {
        Integer total = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(total_copies), 0) FROM books", Integer.class);
        return total == null ? 0 : total;
    }
}
