package com.library.exceptions;

/** Thrown for invalid user input (blank fields, bad email, negative numbers...). */
public class InvalidInputException extends LibraryException {

    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}
