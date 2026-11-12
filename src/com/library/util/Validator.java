package com.library.util;

import com.library.exceptions.InvalidInputException;
import com.library.model.Book;
import com.library.model.Member;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Validator — input validation helpers.
 *
 * <p>Every method that can fail throws {@link InvalidInputException} with a
 * message the GUI can show to the user, instead of letting bad data reach
 * the service or storage layer.</p>
 */
public final class Validator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    /** ISBN-10 or ISBN-13, with or without hyphens/spaces. */
    private static final Pattern ISBN_PATTERN =
            Pattern.compile("^(97[89][- ]?)?\\d{1,5}[- ]?\\d{1,7}[- ]?\\d{1,7}[- ]?[\\dXx]$");

    private Validator() { /* utility class — no instances */ }

    /** True if the text is non-null and has at least one non-space character. */
    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /** Throws unless the text is non-empty. */
    public static void requireNonEmpty(String value, String fieldName)
            throws InvalidInputException {
        if (!isNonEmpty(value)) {
            throw new InvalidInputException(fieldName + " cannot be empty");
        }
    }

    /** True for a well-formed e-mail address (name@domain.tld). */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /** Throws unless the e-mail is well-formed. */
    public static void requireValidEmail(String email) throws InvalidInputException {
        if (!isValidEmail(email)) {
            throw new InvalidInputException("Invalid e-mail address: " + email);
        }
    }

    /** True for a 10- or 13-digit ISBN (hyphens allowed). */
    public static boolean isValidISBN(String isbn) {
        if (isbn == null) return false;
        String digits = isbn.replaceAll("[- ]", "");
        return (digits.length() == 10 || digits.length() == 13)
                && digits.matches("[0-9Xx]+")
                && ISBN_PATTERN.matcher(isbn.trim()).matches();
    }

    /** Throws unless the ISBN is a valid ISBN-10/13. */
    public static void requireValidISBN(String isbn) throws InvalidInputException {
        if (!isValidISBN(isbn)) {
            throw new InvalidInputException("Invalid ISBN (need 10 or 13 digits): " + isbn);
        }
    }

    /** True for a sensible publication year (1000 … this year). */
    public static boolean isValidYear(int year) {
        return year >= 1000 && year <= LocalDate.now().getYear();
    }

    /** Throws unless the year is sensible. */
    public static void requireValidYear(int year) throws InvalidInputException {
        if (!isValidYear(year)) {
            throw new InvalidInputException("Invalid publication year: " + year);
        }
    }

    /** Validates a whole book (title, author, ISBN, year, copy counts). */
    public static void validateBook(Book book) throws InvalidInputException {
        if (book == null) throw new InvalidInputException("Book cannot be null");
        requireNonEmpty(book.getTitle(), "Book title");
        requireNonEmpty(book.getAuthor(), "Author");
        requireValidISBN(book.getIsbn());
        requireValidYear(book.getYear());
        if (book.getTotalCopies() < 0)
            throw new InvalidInputException("Total copies cannot be negative");
        if (book.getAvailableCopies() < 0 || book.getAvailableCopies() > book.getTotalCopies())
            throw new InvalidInputException("Available copies must be between 0 and total copies");
    }

    /** Validates a whole member (name, e-mail, member type, max books). */
    public static void validateMember(Member member) throws InvalidInputException {
        if (member == null) throw new InvalidInputException("Member cannot be null");
        requireNonEmpty(member.getName(), "Member name");
        requireValidEmail(member.getEmail());
        if (member.getMaxBooks() <= 0)
            throw new InvalidInputException("Max books must be at least 1");
    }
}
