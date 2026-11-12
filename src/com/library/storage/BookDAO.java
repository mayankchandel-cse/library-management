package com.library.storage;

import com.library.exceptions.BookNotFoundException;
import com.library.exceptions.InvalidInputException;
import com.library.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * BookDAO — data-access object for books.
 * Works on the in-memory list in DataStore (no SQL, no database).
 * All lookups that miss throw BookNotFoundException.
 */
public class BookDAO {

    private final DataStore store;

    public BookDAO() {
        this.store = DataStore.getInstance();
    }

    /** For tests: use a given store instead of the singleton. */
    BookDAO(DataStore store) {
        this.store = store;
    }

    /** Adds a book after validating it; ISBN must be unique. */
    public Book add(Book book) throws InvalidInputException {
        validate(book);
        for (Book b : store.getBooks()) {
            if (b.getIsbn().equalsIgnoreCase(book.getIsbn())) {
                throw new InvalidInputException(
                        "A book with ISBN " + book.getIsbn() + " already exists.");
            }
        }
        if (book.getId() <= 0) {
            book.setId(store.nextBookId());
        }
        store.getBooks().add(book);
        return book;
    }

    public Book getById(int id) throws BookNotFoundException {
        for (Book b : store.getBooks()) {
            if (b.getId() == id) {
                return b;
            }
        }
        throw new BookNotFoundException(id);
    }

    public Book getByIsbn(String isbn) throws BookNotFoundException {
        for (Book b : store.getBooks()) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) {
                return b;
            }
        }
        throw new BookNotFoundException("isbn", isbn);
    }

    /** A copy of the whole catalogue (callers cannot corrupt the store). */
    public List<Book> getAll() {
        return new ArrayList<>(store.getBooks());
    }

    // ----- aliases used by the service layer (same behaviour) -----
    public List<Book> getAllBooks() { return getAll(); }
    public Book addBook(Book book) throws InvalidInputException { return add(book); }
    public Book updateBook(Book book) throws BookNotFoundException, InvalidInputException {
        return update(book);
    }
    public void deleteBook(int id) throws BookNotFoundException { delete(id); }

    /** Case-insensitive search in title, author or category. */
    public List<Book> search(String keyword) {
        List<Book> result = new ArrayList<>();
        if (keyword == null || keyword.isBlank()) {
            return result;
        }
        String q = keyword.trim().toLowerCase();
        for (Book b : store.getBooks()) {
            if ((b.getTitle() != null && b.getTitle().toLowerCase().contains(q))
                    || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q))
                    || (b.getCategory() != null && b.getCategory().toLowerCase().contains(q))) {
                result.add(b);
            }
        }
        return result;
    }

    public List<Book> searchByCategory(String category) {
        List<Book> result = new ArrayList<>();
        for (Book b : store.getBooks()) {
            if (b.getCategory() != null
                    && b.getCategory().equalsIgnoreCase(category)) {
                result.add(b);
            }
        }
        return result;
    }

    public List<Book> getAvailableBooks() {
        List<Book> result = new ArrayList<>();
        for (Book b : store.getBooks()) {
            if (b.isAvailable()) {
                result.add(b);
            }
        }
        return result;
    }

    /** Replaces the stored book with the same id. */
    public Book update(Book book) throws BookNotFoundException, InvalidInputException {
        validate(book);
        List<Book> list = store.getBooks();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == book.getId()) {
                list.set(i, book);
                return book;
            }
        }
        throw new BookNotFoundException(book.getId());
    }

    public void delete(int id) throws BookNotFoundException {
        Book book = getById(id); // throws if missing
        store.getBooks().remove(book);
    }

    private void validate(Book book) throws InvalidInputException {
        if (book == null) {
            throw new InvalidInputException("Book cannot be null.");
        }
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new InvalidInputException("ISBN is required.");
        }
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new InvalidInputException("Title is required.");
        }
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new InvalidInputException("Author is required.");
        }
        if (book.getTotalCopies() < 0 || book.getAvailableCopies() < 0) {
            throw new InvalidInputException("Copy counts cannot be negative.");
        }
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new InvalidInputException(
                    "Available copies cannot exceed total copies.");
        }
        if (book.getPrice() < 0) {
            throw new InvalidInputException("Price cannot be negative.");
        }
    }
}
