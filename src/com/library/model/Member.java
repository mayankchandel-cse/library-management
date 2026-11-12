package com.library.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Member — a library member (Student / Faculty / Staff).
 * maxBooks is derived from memberType, not stored separately:
 * Student = 3, Faculty = 5, Staff = 4.
 * joinedDate is stored as an ISO String (yyyy-MM-dd) so the class
 * stays simple and serializable without extra date handling.
 */
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STUDENT = "Student";
    public static final String FACULTY = "Faculty";
    public static final String STAFF = "Staff";

    private int id;
    private String name;
    private String email;         // unique
    private String phone;
    private String memberType;    // Student / Faculty / Staff
    private String joinedDate;    // yyyy-MM-dd
    /** If set (via setMaxBooks), overrides the type-derived limit. */
    private Integer maxBooksOverride;

    public Member() {
    }

    public Member(int id, String name, String email, String phone,
                  String memberType, String joinedDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.memberType = memberType;
        this.joinedDate = joinedDate;
    }

    /** Overloaded constructor; joinedDate defaults to today. */
    public Member(int id, String name, String email, String phone,
                  String memberType) {
        this(id, name, email, phone, memberType,
             java.time.LocalDate.now().toString());
    }

    /**
     * Borrowing limit: an explicit value set with setMaxBooks() wins,
     * otherwise it is derived from the member type.
     */
    public int getMaxBooks() {
        if (maxBooksOverride != null) {
            return maxBooksOverride;
        }
        if (FACULTY.equalsIgnoreCase(memberType)) {
            return 5;
        }
        if (STAFF.equalsIgnoreCase(memberType)) {
            return 4;
        }
        return 3; // Student (default)
    }

    /** Overrides the type-derived borrowing limit. */
    public void setMaxBooks(int maxBooks) {
        this.maxBooksOverride = maxBooks;
    }

    /** Alias for getMemberType() used by the GUI panels. */
    public String getMembershipType() { return memberType; }
    /** Alias for setMemberType() used by the GUI panels. */
    public void setMembershipType(String memberType) { this.memberType = memberType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        Member other = (Member) o;
        return Objects.equals(email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "Member{id=" + id + ", name='" + name + "', email='" + email
                + "', type='" + memberType + "', maxBooks=" + getMaxBooks()
                + ", joined=" + joinedDate + "}";
    }

    // ----- getters / setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMemberType() { return memberType; }
    public void setMemberType(String memberType) { this.memberType = memberType; }
    public String getJoinedDate() { return joinedDate; }
    public void setJoinedDate(String joinedDate) { this.joinedDate = joinedDate; }
}
