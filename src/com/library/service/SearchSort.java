package com.library.service;

import com.library.model.Book;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * SearchSort — searching and sorting algorithms for the catalogue.
 *
 * <p>These are hand-written (not {@code Collections.sort}) where the
 * algorithm itself is the point, so they can be demonstrated in the viva.
 * All sort methods sort the given list <b>in place</b>.</p>
 */
public final class SearchSort {

    private SearchSort() { /* utility class — no instances */ }

    // ---- Searching ---------------------------------------------------------

    /**
     * Linear search by title (case-insensitive, partial match).
     * Time: O(n), Space: O(1). Returns the first match, or null.
     */
    public static Book linearSearchByTitle(List<Book> books, String title) {
        if (books == null || title == null) return null;
        String wanted = title.trim().toLowerCase();
        for (Book b : books) {
            if (b.getTitle() != null && b.getTitle().toLowerCase().contains(wanted)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Binary search by id. The list MUST already be sorted by id ascending
     * (see {@link #sortById(List)}); otherwise the result is meaningless.
     * Time: O(log n), Space: O(1). Returns the book, or null.
     */
    public static Book binarySearchById(List<Book> sortedById, int id) {
        if (sortedById == null) return null;
        int low = 0, high = sortedById.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2; // avoids overflow
            int midId = sortedById.get(mid).getId();
            if (midId == id) return sortedById.get(mid);
            if (midId < id) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }

    /** Sorts a list by id ascending (needed before binary search). O(n log n). */
    public static void sortById(List<Book> books) {
        books.sort(Comparator.comparingInt(Book::getId));
    }

    // ---- Sorting -----------------------------------------------------------

    /**
     * Quick Sort by title (alphabetical, case-insensitive).
     * Time: O(n log n) average, O(n^2) worst (already sorted + bad pivot);
     * Space: O(log n) for the recursion stack.
     */
    public static void quickSortByTitle(List<Book> books) {
        quickSort(books, 0, books.size() - 1);
    }

    private static void quickSort(List<Book> books, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(books, low, high);
            quickSort(books, low, pivotIndex - 1);
            quickSort(books, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Book> books, int low, int high) {
        String pivot = titleOf(books.get(high));
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (titleOf(books.get(j)).compareTo(pivot) <= 0) {
                i++;
                swap(books, i, j);
            }
        }
        swap(books, i + 1, high);
        return i + 1;
    }

    /**
     * Merge Sort by publication year (oldest first, stable).
     * Time: O(n log n) always; Space: O(n) for the temporary lists.
     */
    public static void mergeSortByYear(List<Book> books) {
        if (books.size() <= 1) return;
        List<Book> sorted = mergeSort(new ArrayList<>(books));
        for (int i = 0; i < books.size(); i++) books.set(i, sorted.get(i));
    }

    private static List<Book> mergeSort(List<Book> books) {
        if (books.size() <= 1) return books;
        int mid = books.size() / 2;
        List<Book> left = mergeSort(new ArrayList<>(books.subList(0, mid)));
        List<Book> right = mergeSort(new ArrayList<>(books.subList(mid, books.size())));
        return merge(left, right);
    }

    private static List<Book> merge(List<Book> left, List<Book> right) {
        List<Book> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getYear() <= right.get(j).getYear()) result.add(left.get(i++));
            else result.add(right.get(j++));
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }

    /**
     * Bubble Sort by author (alphabetical) — kept for viva comparison with
     * Quick/Merge Sort. Time: O(n^2) worst/average, O(n) if already sorted;
     * Space: O(1).
     */
    public static void bubbleSortByAuthor(List<Book> books) {
        int n = books.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (authorOf(books.get(j)).compareTo(authorOf(books.get(j + 1))) > 0) {
                    swap(books, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) break; // already sorted — best case O(n)
        }
    }

    /**
     * Sort by available copies (most copies first).
     * Time: O(n log n) — uses the library sort, as the algorithm is not
     * the point here, only the ordering.
     */
    public static void sortByAvailableCopies(List<Book> books) {
        books.sort(Comparator.comparingInt(Book::getAvailableCopies).reversed());
    }

    // ---- Helpers -----------------------------------------------------------

    private static String titleOf(Book b) {
        return b.getTitle() == null ? "" : b.getTitle().toLowerCase();
    }

    private static String authorOf(Book b) {
        return b.getAuthor() == null ? "" : b.getAuthor().toLowerCase();
    }

    private static void swap(List<Book> books, int i, int j) {
        Book tmp = books.get(i);
        books.set(i, books.get(j));
        books.set(j, tmp);
    }

    // ---- Demo --------------------------------------------------------------

    /** Console demo: prints the catalogue before and after each sort/search. */
    public static void main(String[] args) {
        List<Book> books = sampleBooks();
        System.out.println("=== Before sorting ===");
        printTitles(books);

        quickSortByTitle(books);
        System.out.println("\n=== After Quick Sort by title (O(n log n) avg) ===");
        printTitles(books);

        mergeSortByYear(books);
        System.out.println("\n=== After Merge Sort by year (O(n log n)) ===");
        for (Book b : books) System.out.println("  " + b.getYear() + " - " + b.getTitle());

        bubbleSortByAuthor(books);
        System.out.println("\n=== After Bubble Sort by author (O(n^2)) ===");
        for (Book b : books) System.out.println("  " + b.getAuthor() + " - " + b.getTitle());

        sortById(books);
        Book found = binarySearchById(books, 3);
        System.out.println("\nBinary search for id 3 (O(log n)): "
                + (found == null ? "not found" : found.getTitle()));
        Book linear = linearSearchByTitle(books, "wings");
        System.out.println("Linear search for 'wings' (O(n)): "
                + (linear == null ? "not found" : linear.getTitle()));
    }

    private static List<Book> sampleBooks() {
        List<Book> list = new ArrayList<>();
        list.add(make(1, "Wings of Fire", "A. P. J. Abdul Kalam", 1999));
        list.add(make(2, "Let Us C", "Yashavant Kanetkar", 2016));
        list.add(make(3, "Java: The Complete Reference", "Herbert Schildt", 2018));
        list.add(make(4, "Data Structures in C", "Reema Thareja", 2011));
        list.add(make(5, "Operating System Concepts", "Silberschatz", 2018));
        return list;
    }

    private static Book make(int id, String title, String author, int year) {
        Book b = new Book();
        b.setId(id);
        b.setTitle(title);
        b.setAuthor(author);
        b.setYear(year);
        b.setCategory("General");
        b.setIsbn("978-0-00-00000-" + id);
        b.setTotalCopies(3);
        b.setAvailableCopies(3);
        return b;
    }

    private static void printTitles(List<Book> books) {
        for (Book b : books) System.out.println("  " + b.getTitle());
    }
}
