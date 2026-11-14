package com.library.repository;

import com.library.model.IssueRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Repository
public class IssueRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final BeanPropertyRowMapper<IssueRecord> ROW_MAPPER =
            new BeanPropertyRowMapper<>(IssueRecord.class);

    private static final String SELECT_WITH_JOINS =
            "SELECT i.id, i.book_id, i.member_id, i.issue_date, i.due_date, i.return_date, "
                    + "i.fine, i.status, b.title AS book_title, m.name AS member_name "
                    + "FROM issues i "
                    + "JOIN books b ON i.book_id = b.id "
                    + "JOIN members m ON i.member_id = m.id ";

    public List<IssueRecord> findAll() {
        return jdbcTemplate.query(SELECT_WITH_JOINS + "ORDER BY i.id", ROW_MAPPER);
    }

    public List<IssueRecord> findActive() {
        return jdbcTemplate.query(
                SELECT_WITH_JOINS + "WHERE i.return_date IS NULL ORDER BY i.id", ROW_MAPPER);
    }

    public List<IssueRecord> findOverdue() {
        return jdbcTemplate.query(
                SELECT_WITH_JOINS + "WHERE i.return_date IS NULL AND i.due_date < CURDATE() ORDER BY i.id",
                ROW_MAPPER);
    }

    public List<IssueRecord> findByMemberId(int memberId) {
        return jdbcTemplate.query(
                SELECT_WITH_JOINS + "WHERE i.member_id = ? ORDER BY i.id", ROW_MAPPER, memberId);
    }

    public void save(IssueRecord issue) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO issues (book_id, member_id, issue_date, due_date, return_date, fine, status) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, issue.getBookId());
            ps.setInt(2, issue.getMemberId());
            ps.setDate(3, Date.valueOf(issue.getIssueDate()));
            ps.setDate(4, Date.valueOf(issue.getDueDate()));
            ps.setDate(5, issue.getReturnDate() == null ? null : Date.valueOf(issue.getReturnDate()));
            ps.setDouble(6, issue.getFine());
            ps.setString(7, issue.getStatus());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            issue.setId(key.intValue());
        }
    }

    public void markReturned(int issueId, LocalDate returnDate, double fine) {
        jdbcTemplate.update(
                "UPDATE issues SET return_date = ?, fine = ?, status = 'Returned' WHERE id = ?",
                Date.valueOf(returnDate), fine, issueId);
    }
}
