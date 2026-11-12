package com.library.model;

/**
 * Librarian — a staff login account.
 * Extends User (inheritance): a Librarian IS a User whose role is
 * always LIBRARIAN, plus a staffId.
 */
public class Librarian extends User {

    private static final long serialVersionUID = 1L;

    private String staffId;

    public Librarian() {
        super();
        setRole(LIBRARIAN);
    }

    public Librarian(String username, String plainPassword, String staffId) {
        super(username, plainPassword, LIBRARIAN);
        this.staffId = staffId;
    }

    @Override
    public String toString() {
        return "Librarian{username='" + getUsername()
                + "', staffId='" + staffId + "', role='" + getRole() + "'}";
    }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }
}
