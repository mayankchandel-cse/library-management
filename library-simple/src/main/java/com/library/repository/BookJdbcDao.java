package com.library.repository;

import com.library.model.Book;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Checklist item 5: plain JDBC.
 * This class does NOT use JdbcTemplate. It uses a Connection,
 * a PreparedStatement (with ? placeholders, so no SQL injection)
 * and a ResultSet, read row by row with next().
 */
@Repository
public class BookJdbcDao {

    private final DataSource dataSource;

    public BookJdbcDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** Find one book by ID using plain JDBC. */
    public Book findBookById(int id) {
        String sql = "SELECT id, title, author, total_copies, available_copies "
                + "FROM books WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); // the ? is filled in separately, never joined into the SQL
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("total_copies"),
                            rs.getInt("available_copies"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not find book " + id, e);
        }
        return null;
    }

    /** Search books by title or author using plain JDBC. */
    public List<Book> searchBooks(String keyword) {
        String sql = "SELECT id, title, author, total_copies, available_copies "
                + "FROM books WHERE title LIKE ? OR author LIKE ?";
        List<Book> books = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("total_copies"),
                            rs.getInt("available_copies")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not search books", e);
        }
        return books;
    }
}
