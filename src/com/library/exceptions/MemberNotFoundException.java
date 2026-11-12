package com.library.exceptions;

/** Thrown when a member id / email is not registered. */
public class MemberNotFoundException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(int memberId) {
        super("Member not found with id: " + memberId);
    }
}
