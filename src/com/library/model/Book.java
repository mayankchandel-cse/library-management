package com.library.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Book — a book in the library catalogue.
 * Comparable by title (case-insensitive) so the catalogue can be
 * sorted with Collections.sort / Arrays.sort.
 * Serializable so the whole catalogue can be saved with
 * ObjectOutputStream (see storage.DataStore).
 */
public class Book implements Serializable, Comparable<Book> {

    private static final long serialVersionUID = 1L;

    private int id;
    private String isbn;          // unique — used by equals/hashCode
    private String title;
    private String author;
    private String category;
    private String publisher;
    private int year;
    private int totalCopies;
    private int availableCopies;
    private double price;         // in Rs

    /** Default constructor (needed by serialization / forms). */
    public Book() {
    }

    /** Full constructor. */
    public Book(int id, String isbn, String title, String author,
                String category, String publisher, int year,
                int totalCopies, int availableCopies, double price) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.publisher = publisher;
        this.year = year;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.price = price;
    }

    /**
     * Overloaded constructor without price (price defaults to 0).
     * NOTE: there is only ONE 9-arg constructor — in
     * (id, title, author, isbn, category, publisher, year,
     * totalCopies, availableCopies) order, as used by the GUI sample
     * data. (A second 9-arg overload in (id, isbn, title, ...) order
     * would have the identical signature and cannot coexist in Java.)
     * The full 10-arg constructor above keeps the (id, isbn, title,
     * author, ...) order.
     */
    public Book(int id, String title, String author, String isbn,
                String category, String publisher, int year,
                int totalCopies, int availableCopies) {
        this(id, isbn, title, author, category, publisher, year,
             totalCopies, availableCopies, 0.0);
    }

    /** True when at least one copy can be issued. */
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /** Sort by title, case-insensitive. */
    @Override
    public int compareTo(Book other) {
        if (other == null || other.title == null) {
            return 1;
        }
        if (this.title == null) {
            return -1;
        }
        return this.title.compareToIgnoreCase(other.title);
    }

    /** Two books are the same book when their ISBN matches. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book other = (Book) o;
        return Objects.equals(isbn, other.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{id=" + id + ", isbn='" + isbn + "', title='" + title
                + "', author='" + author + "', category='" + category
                + "', year=" + year + ", available=" + availableCopies
                + "/" + totalCopies + ", price=Rs " + price + "}";
    }

    // ----- getters / setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    /** Alias for getTotalCopies() used by the GUI panels. */
    public int getCopies() { return totalCopies; }
    /** Alias for setTotalCopies() used by the GUI panels. */
    public void setCopies(int copies) { this.totalCopies = copies; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
