package com.library.gui;

import com.library.exceptions.InvalidInputException;
import com.library.model.Book;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * BooksPanel — Library Management System (Part C: GUI).
 *
 * <p>Screen 4: book catalogue. A JTable of all books, a search field
 * (title / author / category), a sort menu, and Add / Edit / Delete.
 * Sorting is done here with real algorithms on a copy of the list:
 * Quick Sort by title, Merge Sort by year, and author A–Z
 * (see {@link #quickSortByTitle} and {@link #mergeSortByYear}).</p>
 */
public class BooksPanel extends JPanel {

    private final MainFrame parent;
    private String query = "";
    private final JTextField searchField = Theme.styledTextField(20);
    private final JComboBox<String> sortBox = new JComboBox<>(new String[]{
            "Title — Quick Sort", "Year — Merge Sort", "Author — A to Z"});
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Title", "Author", "ISBN", "Category",
                    "Year", "Copies", "Available"}, 0);
    private final JTable table = new JTable(tableModel);

    public BooksPanel(MainFrame parent) {
        super(new BorderLayout(10, 10));
        this.parent = parent;
        setBackground(Theme.BG);

        JPanel top = Theme.panel();
        top.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(Theme.label("Books", Theme.HEADING_FONT, Theme.ACCENT));
        top.add(Theme.label("Search:"));
        top.add(searchField);
        JButton searchButton = Theme.styledButton("Search");
        searchButton.setPreferredSize(new java.awt.Dimension(100, 36));
        searchButton.addActionListener(e -> {
            query = searchField.getText().trim();
            refresh();
        });
        searchField.addActionListener(e -> searchButton.doClick());
        top.add(searchButton);
        top.add(Theme.label("Sort by:"));
        sortBox.addActionListener(e -> refresh());
        top.add(sortBox);
        add(top, BorderLayout.NORTH);

        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = Theme.panel();
        buttons.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton addButton = Theme.styledButton("Add Book");
        addButton.addActionListener(e -> openBookDialog(null));
        JButton editButton = Theme.secondaryButton("Edit Selected");
        editButton.addActionListener(e -> {
            Book selected = selectedBook();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a book first.",
                        "Edit", JOptionPane.WARNING_MESSAGE);
            } else {
                openBookDialog(selected);
            }
        });
        JButton deleteButton = Theme.secondaryButton("Delete Selected");
        deleteButton.addActionListener(e -> {
            Book selected = selectedBook();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a book first.",
                        "Delete", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int ok = JOptionPane.showConfirmDialog(this,
                    "Delete \"" + selected.getTitle() + "\"?",
                    "Delete book", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                if (!parent.deleteBook(selected.getId())) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete — this book is currently issued.",
                            "Delete", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        buttons.add(Theme.label("Tip: click a row, then Edit / Delete.",
                Theme.BODY_FONT, Theme.MUTED));
        add(buttons, BorderLayout.SOUTH);
        refresh();
    }

    /** Search query set from the MainFrame top bar. */
    public void setQuery(String query) {
        this.query = query == null ? "" : query;
        searchField.setText(this.query);
        refresh();
    }

    /** The book selected in the table, or null. */
    private Book selectedBook() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        return SampleData.findBookById(parent.getBooks(), id);
    }

    /** Rebuild the table: filter by query, then sort by the chosen menu. */
    public void refresh() {
        List<Book> view = new ArrayList<>();
        String q = query.toLowerCase();
        for (Book b : parent.getBooks()) {
            if (q.isEmpty()
                    || b.getTitle().toLowerCase().contains(q)
                    || b.getAuthor().toLowerCase().contains(q)
                    || b.getCategory().toLowerCase().contains(q)) {
                view.add(b);
            }
        }
        int choice = sortBox.getSelectedIndex();
        if (choice == 0) {
            quickSortByTitle(view, 0, view.size() - 1);
        } else if (choice == 1) {
            view = mergeSortByYear(view);
        } else {
            view.sort((a, b) -> a.getAuthor().compareToIgnoreCase(b.getAuthor()));
        }
        tableModel.setRowCount(0);
        for (Book b : view) {
            tableModel.addRow(new Object[]{
                    b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                    b.getCategory(), b.getYear(), b.getCopies(),
                    b.getAvailableCopies()});
        }
    }

    // ---- sorting algorithms (ADSA-style, on a copy of the list) -----------

    /** Quick Sort by title, O(n log n) average. */
    public static void quickSortByTitle(List<Book> list, int low, int high) {
        if (low < high) {
            int p = partitionByTitle(list, low, high);
            quickSortByTitle(list, low, p - 1);
            quickSortByTitle(list, p + 1, high);
        }
    }

    private static int partitionByTitle(List<Book> list, int low, int high) {
        String pivot = list.get(high).getTitle();
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (list.get(j).getTitle().compareToIgnoreCase(pivot) <= 0) {
                i++;
                Book t = list.get(i);
                list.set(i, list.get(j));
                list.set(j, t);
            }
        }
        Book t = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, t);
        return i + 1;
    }

    /** Merge Sort by year (newest first), O(n log n), stable. */
    public static List<Book> mergeSortByYear(List<Book> list) {
        if (list.size() <= 1) {
            return list;
        }
        int mid = list.size() / 2;
        List<Book> left = mergeSortByYear(new ArrayList<>(list.subList(0, mid)));
        List<Book> right = mergeSortByYear(new ArrayList<>(list.subList(mid, list.size())));
        List<Book> out = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getYear() >= right.get(j).getYear()) {
                out.add(left.get(i++));
            } else {
                out.add(right.get(j++));
            }
        }
        while (i < left.size()) {
            out.add(left.get(i++));
        }
        while (j < right.size()) {
            out.add(right.get(j++));
        }
        return out;
    }

    // ---- add / edit dialog --------------------------------------------------

    private void openBookDialog(Book existing) {
        JDialog dialog = new JDialog(
                javax.swing.SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add Book" : "Edit Book",
                JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG);

        JTextField titleField = Theme.styledTextField(22);
        JTextField authorField = Theme.styledTextField(22);
        JTextField isbnField = Theme.styledTextField(22);
        JTextField categoryField = Theme.styledTextField(22);
        JTextField yearField = Theme.styledTextField(22);
        JTextField copiesField = Theme.styledTextField(22);
        if (existing != null) {
            titleField.setText(existing.getTitle());
            authorField.setText(existing.getAuthor());
            isbnField.setText(existing.getIsbn());
            categoryField.setText(existing.getCategory());
            yearField.setText(String.valueOf(existing.getYear()));
            copiesField.setText(String.valueOf(existing.getCopies()));
        }
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Theme.BG);
        form.add(Theme.label("Title:")); form.add(titleField);
        form.add(Theme.label("Author:")); form.add(authorField);
        form.add(Theme.label("ISBN:")); form.add(isbnField);
        form.add(Theme.label("Category:")); form.add(categoryField);
        form.add(Theme.label("Year:")); form.add(yearField);
        form.add(Theme.label("Copies:")); form.add(copiesField);
        JButton save = Theme.styledButton("Save");
        save.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                if (title.isEmpty() || author.isEmpty()) {
                    throw new InvalidInputException(
                            "Title and author must not be empty.");
                }
                int year = Integer.parseInt(yearField.getText().trim());
                int copies = Integer.parseInt(copiesField.getText().trim());
                if (copies < 1) {
                    throw new InvalidInputException("Copies must be at least 1.");
                }
                if (existing == null) {
                    int id = parent.nextBookId();
                    parent.addBook(new Book(id, title, author,
                            isbnField.getText().trim(),
                            categoryField.getText().trim().isEmpty()
                                    ? "General" : categoryField.getText().trim(),
                            "", year, copies, copies));
                } else {
                    int issued = existing.getCopies() - existing.getAvailableCopies();
                    int available = Math.max(0, copies - issued);
                    parent.updateBook(new Book(existing.getId(), title, author,
                            isbnField.getText().trim(),
                            categoryField.getText().trim(),
                            existing.getPublisher(), year, copies, available));
                }
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Year and Copies must be numbers.",
                        "Invalid input", JOptionPane.WARNING_MESSAGE);
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                        "Invalid input", JOptionPane.WARNING_MESSAGE);
            }
        });
        JPanel south = Theme.panel();
        south.add(save);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(south, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
