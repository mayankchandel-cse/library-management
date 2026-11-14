package com.library.gui;

import com.library.model.Book;
import com.library.service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import java.util.List;

/**
 * BooksPanel - browse, search, add, edit and delete books.
 *
 * <p>Search goes through {@link LibraryService#searchBooks(String)}, which
 * runs a SQL LIKE query on title and author. No sorting algorithm is used
 * here - searching is done by the database.</p>
 */
public class BooksPanel extends JPanel {

    private static final String[] COLUMNS = {
            "ID", "Title", "Author", "ISBN", "Category", "Year", "Copies", "Available"
    };

    private final LibraryService service;
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField searchField;

    public BooksPanel(LibraryService service) {
        this.service = service;
        setBackground(Theme.PANEL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(Theme.label("Books", Theme.TITLE_FONT, Theme.ACCENT), BorderLayout.NORTH);

        model = new DefaultTableModel(COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        Theme.styleTable(table);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Theme.PANEL);
        top.add(Theme.label("Search (title / author): "));
        searchField = Theme.styledTextField(25);
        top.add(searchField);
        JButton searchButton = Theme.styledButton("Search");
        searchButton.addActionListener(e -> search(searchField.getText().trim()));
        top.add(searchButton);
        JButton showAllButton = Theme.secondaryButton("Show All");
        showAllButton.addActionListener(e -> {
            searchField.setText("");
            refresh();
        });
        top.add(showAllButton);

        JPanel centre = new JPanel(new BorderLayout(10, 10));
        centre.setBackground(Theme.PANEL);
        centre.add(top, BorderLayout.NORTH);
        centre.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setBackground(Theme.PANEL);
        JButton addButton = Theme.styledButton("Add Book");
        addButton.addActionListener(e -> showBookDialog(null));
        JButton editButton = Theme.secondaryButton("Edit Selected");
        editButton.addActionListener(e -> editSelected());
        JButton deleteButton = Theme.secondaryButton("Delete Selected");
        deleteButton.addActionListener(e -> deleteSelected());
        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);
        centre.add(buttons, BorderLayout.SOUTH);

        add(centre, BorderLayout.CENTER);
        refresh();
    }

    /** Searches by title/author and shows the matching books. Called by the top bar too. */
    public void search(String keyword) {
        searchField.setText(keyword == null ? "" : keyword);
        load(keyword == null || keyword.isBlank()
                ? service.getAllBooks()
                : service.searchBooks(keyword));
    }

    /** Reloads all books from the database. */
    public void refresh() {
        load(service.getAllBooks());
    }

    private void load(List<Book> books) {
        model.setRowCount(0);
        for (Book b : books) {
            model.addRow(new Object[]{
                    b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                    b.getCategory(), b.getPublicationYear(),
                    b.getTotalCopies(), b.getAvailableCopies()
            });
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        Book existing = null;
        for (Book b : service.getAllBooks()) {
            if (b.getId() == id) {
                existing = b;
                break;
            }
        }
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Book not found. Please refresh.");
            return;
        }
        showBookDialog(existing);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String title = String.valueOf(model.getValueAt(row, 1));
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete book \"" + title + "\" (ID " + id + ")?",
                "Delete Book", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.deleteBook(id);
            refresh();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not delete book: " + ex.getMessage());
        }
    }

    /**
     * Small form dialog for adding a book (existing == null) or editing one.
     * Uses a JDialog with labelled fields so nothing is truncated.
     */
    private void showBookDialog(Book existing) {
        boolean isNew = existing == null;
        JDialog dialog = new JDialog();
        dialog.setTitle(isNew ? "Add Book" : "Edit Book");
        dialog.setModal(true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBackground(Theme.PANEL);
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JTextField titleField = Theme.styledTextField(20);
        JTextField authorField = Theme.styledTextField(20);
        JTextField isbnField = Theme.styledTextField(20);
        JTextField categoryField = Theme.styledTextField(20);
        JTextField yearField = Theme.styledTextField(20);
        JTextField copiesField = Theme.styledTextField(20);
        if (!isNew) {
            titleField.setText(existing.getTitle());
            authorField.setText(existing.getAuthor());
            isbnField.setText(existing.getIsbn());
            categoryField.setText(existing.getCategory());
            yearField.setText(String.valueOf(existing.getPublicationYear()));
            copiesField.setText(String.valueOf(existing.getTotalCopies()));
        }
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Author:"));
        form.add(authorField);
        form.add(new JLabel("ISBN:"));
        form.add(isbnField);
        form.add(new JLabel("Category:"));
        form.add(categoryField);
        form.add(new JLabel("Year:"));
        form.add(yearField);
        form.add(new JLabel("Copies:"));
        form.add(copiesField);
        // Style the labels for the dark theme.
        for (java.awt.Component c : form.getComponents()) {
            if (c instanceof JLabel label) {
                label.setForeground(Theme.TEXT);
                label.setFont(Theme.BODY_FONT);
            }
        }
        JButton saveButton = Theme.styledButton(isNew ? "Add Book" : "Save Changes");
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Title and author are required.");
                    return;
                }
                int year = Integer.parseInt(yearField.getText().trim());
                int copies = Integer.parseInt(copiesField.getText().trim());
                if (copies < 1) {
                    JOptionPane.showMessageDialog(dialog, "Copies must be at least 1.");
                    return;
                }
                Book book = isNew ? new Book() : existing;
                book.setTitle(title);
                book.setAuthor(author);
                book.setIsbn(isbnField.getText().trim());
                book.setCategory(categoryField.getText().trim());
                book.setPublicationYear(year);
                book.setTotalCopies(copies);
                if (isNew) {
                    book.setAvailableCopies(copies);
                    service.addBook(book);
                } else {
                    service.updateBook(book);
                }
                dialog.dispose();
                refresh();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Year and copies must be numbers.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Could not save book: " + ex.getMessage());
            }
        });
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Theme.PANEL);
        bottom.add(saveButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
