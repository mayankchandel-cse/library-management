package com.library.repository;

import com.library.model.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final BeanPropertyRowMapper<Member> ROW_MAPPER =
            new BeanPropertyRowMapper<>(Member.class);

    public List<Member> findAll() {
        return jdbcTemplate.query(
                "SELECT id, name, email, phone, member_type, max_books FROM members ORDER BY id",
                ROW_MAPPER);
    }

    public Member findById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, name, email, phone, member_type, max_books FROM members WHERE id = ?",
                    ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void save(Member member) {
        jdbcTemplate.update(
                "INSERT INTO members (name, email, phone, member_type, max_books) VALUES (?, ?, ?, ?, ?)",
                member.getName(), member.getEmail(), member.getPhone(),
                member.getMemberType(), member.getMaxBooks());
    }

    public int countMembers() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM members", Integer.class);
        return count == null ? 0 : count;
    }
}
