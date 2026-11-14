package com.library.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int publicationYear;
    private int totalCopies;
    private int availableCopies;

    public Book() {
    }

    public Book(int id, String title, String author, String isbn, String category,
                int publicationYear, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publicationYear = publicationYear;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + '\'' + ", author='" + author + '\''
                + ", isbn='" + isbn + '\'' + ", category='" + category + '\''
                + ", publicationYear=" + publicationYear + ", totalCopies=" + totalCopies
                + ", availableCopies=" + availableCopies + '}';
    }
}
