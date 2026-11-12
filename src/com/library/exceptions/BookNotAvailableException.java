package com.library.exceptions;

/** Thrown when a book has no available copies to issue. */
public class BookNotAvailableException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public BookNotAvailableException(String message) {
        super(message);
    }

    public BookNotAvailableException(String title, int availableCopies) {
        super("Book '" + title + "' is not available ("
                + availableCopies + " copies available).");
    }
}
