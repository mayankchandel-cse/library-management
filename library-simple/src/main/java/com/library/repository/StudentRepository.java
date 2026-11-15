package com.library.repository;

import com.library.model.IssueRecord;
import com.library.model.Student;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

/** Checklist item 2: register students and keep their borrowing history. */
@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Student> rowMapper = (rs, rowNum) -> new Student(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"));

    public void registerStudent(String name, String email) {
        jdbcTemplate.update(
                "INSERT INTO students (name, email) VALUES (?, ?)", name, email);
    }

    public List<Student> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name, email FROM students ORDER BY id", rowMapper);
    }

    /** Borrowing history: one JOIN across issues, books and students. */
    public List<IssueRecord> borrowingHistory(int studentId) {
        RowMapper<IssueRecord> mapper = (rs, rowNum) -> {
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
        return jdbcTemplate.query(
                "SELECT i.id, i.book_id, i.student_id, b.title, s.name, "
                        + "i.issue_date, i.due_date, i.return_date, i.fine, i.status "
                        + "FROM issues i "
                        + "JOIN books b ON i.book_id = b.id "
                        + "JOIN students s ON i.student_id = s.id "
                        + "WHERE i.student_id = ? ORDER BY i.issue_date",
                mapper, studentId);
    }
}
