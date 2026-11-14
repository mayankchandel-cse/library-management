package com.library.repository;

import com.library.model.Book;
import com.library.model.IssueRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Plain-JDBC data access (requirement 5): uses DataSource, Connection,
 * PreparedStatement and ResultSet directly - no JdbcTemplate here.
 */
@Component
public class BookJdbcDao {

    @Autowired
    private DataSource dataSource;

    public Book findBookById(int id) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, title, author, isbn, category, publication_year, "
                             + "total_copies, available_copies FROM books WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setCategory(rs.getString("category"));
                book.setPublicationYear(rs.getInt("publication_year"));
                book.setTotalCopies(rs.getInt("total_copies"));
                book.setAvailableCopies(rs.getInt("available_copies"));
                return book;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find book with id " + id, e);
        }
    }

    public List<IssueRecord> findOverdueRecords() {
        List<IssueRecord> records = new ArrayList<>();
        String sql = "SELECT i.id, i.book_id, i.member_id, i.issue_date, i.due_date, "
                + "i.return_date, i.fine, i.status, b.title AS book_title, m.name AS member_name "
                + "FROM issues i "
                + "JOIN books b ON i.book_id = b.id "
                + "JOIN members m ON i.member_id = m.id "
                + "WHERE i.return_date IS NULL AND i.due_date < CURDATE() ORDER BY i.id";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                IssueRecord record = new IssueRecord();
                record.setId(rs.getInt("id"));
                record.setBookId(rs.getInt("book_id"));
                record.setMemberId(rs.getInt("member_id"));
                Date issueDate = rs.getDate("issue_date");
                record.setIssueDate(issueDate == null ? null : issueDate.toLocalDate());
                Date dueDate = rs.getDate("due_date");
                record.setDueDate(dueDate == null ? null : dueDate.toLocalDate());
                Date returnDate = rs.getDate("return_date");
                record.setReturnDate(returnDate == null ? null : returnDate.toLocalDate());
                record.setFine(rs.getDouble("fine"));
                record.setStatus(rs.getString("status"));
                record.setBookTitle(rs.getString("book_title"));
                record.setMemberName(rs.getString("member_name"));
                records.add(record);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load overdue records", e);
        }
        return records;
    }
}
