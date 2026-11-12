package com.library.exceptions;

/** Thrown when a book id / ISBN is not in the catalogue. */
public class BookNotFoundException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(int bookId) {
        super("Book not found with id: " + bookId);
    }

    public BookNotFoundException(String field, String value) {
        super("Book not found with " + field + ": " + value);
    }
}
