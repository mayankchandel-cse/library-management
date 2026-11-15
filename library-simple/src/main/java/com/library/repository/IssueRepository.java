package com.library.repository;

import com.library.model.IssueRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

/** Checklist items 3 and 4: issue, return and overdue records. */
@Repository
public class IssueRepository {

    private final JdbcTemplate jdbcTemplate;

    public IssueRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<IssueRecord> rowMapper = (rs, rowNum) -> {
        IssueRecord r = new IssueRecord();
        r.setId(rs.getInt("id"));
        r.setBookId(rs.getInt("book_id"));
        r.setStudentId(rs.getInt("student_id"));
        r.setBookTitle(rs.getString("title"));
        r.setStudentName(rs.getString("name"));
        r.setIssueDate(rs.getDate("issue_date").toLocalDate());
        r.setDueDate(rs.getDate("due_date").toLocalDate());
        Date returned = rs.getDate("return_date");
        r.setReturnDate(returned == null ? null : returned.toLocalDate());
        r.setFine(rs.getDouble("fine"));
        r.setStatus(rs.getString("status"));
        return r;
    };

    private static final String BASE_QUERY =
            "SELECT i.id, i.book_id, i.student_id, b.title, s.name, "
                    + "i.issue_date, i.due_date, i.return_date, i.fine, i.status "
                    + "FROM issues i "
                    + "JOIN books b ON i.book_id = b.id "
                    + "JOIN students s ON i.student_id = s.id ";

    public int issueBook(int bookId, int studentId, LocalDate issueDate, LocalDate dueDate) {
        // KeyHolder, not SELECT LAST_INSERT_ID(): LAST_INSERT_ID() is
        // per-connection, so a pooled connection could return the wrong id.
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO issues (book_id, student_id, issue_date, due_date, status) "
                            + "VALUES (?, ?, ?, ?, 'Issued')",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, bookId);
            ps.setInt(2, studentId);
            ps.setDate(3, Date.valueOf(issueDate));
            ps.setDate(4, Date.valueOf(dueDate));
            return ps;
        }, keyHolder);
        Number id = keyHolder.getKey();
        return id == null ? -1 : id.intValue();
    }

    public IssueRecord findById(int issueId) {
        List<IssueRecord> list = jdbcTemplate.query(
                BASE_QUERY + "WHERE i.id = ?", rowMapper, issueId);
        return list.isEmpty() ? null : list.get(0);
    }

    public void returnBook(int issueId, LocalDate returnDate, double fine, String status) {
        jdbcTemplate.update(
                "UPDATE issues SET return_date = ?, fine = ?, status = ? WHERE id = ?",
                returnDate, fine, status, issueId);
    }

    /** Overdue = not returned yet and due date is before today. */
    public List<IssueRecord> findOverdue() {
        return jdbcTemplate.query(
                BASE_QUERY + "WHERE i.return_date IS NULL AND i.due_date < CURDATE() "
                        + "ORDER BY i.due_date",
                rowMapper);
    }
}
