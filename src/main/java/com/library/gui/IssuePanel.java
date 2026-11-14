package com.library.gui;

import com.library.model.IssueRecord;
import com.library.service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;

/**
 * IssuePanel - issue a book to a member and return a book by issue ID.
 *
 * <p>No table here: the issue and return forms plus a status message are
 * all this screen needs. Books are issued for 14 days. The due date is
 * shown in full (for example "Due date: 2026-11-26 (14 days from today)")
 * so it is never cut off.</p>
 */
public class IssuePanel extends JPanel {

    private final LibraryService service;
    private final JTextField bookIdField;
    private final JTextField memberIdField;
    private final JTextField issueIdField;
    private final JLabel statusLabel;

    public IssuePanel(LibraryService service) {
        this.service = service;
        setBackground(Theme.PANEL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(Theme.label("Issue / Return", Theme.TITLE_FONT, Theme.ACCENT), BorderLayout.NORTH);

        JPanel centre = new JPanel();
        centre.setBackground(Theme.PANEL);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));

        // --- Issue section ---
        JPanel issuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        issuePanel.setBackground(Theme.CARD);
        issuePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.ACCENT), "Issue a Book"));
        issuePanel.add(styledLabel("Book ID:"));
        bookIdField = Theme.styledTextField(8);
        issuePanel.add(bookIdField);
        issuePanel.add(styledLabel("Member ID:"));
        memberIdField = Theme.styledTextField(8);
        issuePanel.add(memberIdField);
        JButton issueButton = Theme.styledButton("Issue Book");
        issueButton.addActionListener(e -> issueBook());
        issuePanel.add(issueButton);

        // --- Return section ---
        JPanel returnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        returnPanel.setBackground(Theme.CARD);
        returnPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.ACCENT), "Return a Book"));
        returnPanel.add(styledLabel("Issue ID:"));
        issueIdField = Theme.styledTextField(8);
        returnPanel.add(issueIdField);
        JButton returnButton = Theme.styledButton("Return Book");
        returnButton.addActionListener(e -> returnBook());
        returnPanel.add(returnButton);

        statusLabel = Theme.label("Enter a book ID and a member ID to issue, "
                + "or an issue ID to return. Books are issued for 14 days. "
                + "Fine rule: Rs 5 per day late.",
                Theme.BODY_FONT, Theme.MUTED);
        JPanel statusWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusWrap.setBackground(Theme.PANEL);
        statusWrap.add(statusLabel);

        centre.add(issuePanel);
        centre.add(returnPanel);
        centre.add(statusWrap);
        add(centre, BorderLayout.CENTER);
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.TEXT);
        l.setFont(Theme.BODY_FONT);
        return l;
    }

    private void issueBook() {
        try {
            int bookId = Integer.parseInt(bookIdField.getText().trim());
            int memberId = Integer.parseInt(memberIdField.getText().trim());
            IssueRecord record = service.issueBook(bookId, memberId);
            LocalDate due = record.getDueDate();
            // Full due-date message - never truncated.
            statusLabel.setText("Issued! Issue ID: " + record.getId()
                    + " - Due date: " + due + " (14 days from today, " + LocalDate.now() + ").");
            bookIdField.setText("");
            memberIdField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID and Member ID must be numbers.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not issue book: " + ex.getMessage());
        }
    }

    private void returnBook() {
        try {
            int issueId = Integer.parseInt(issueIdField.getText().trim());
            double fine = service.returnBook(issueId);
            statusLabel.setText("Returned! Issue ID: " + issueId
                    + " - Fine: Rs " + fine
                    + (fine > 0 ? " (Rs 5 per day late)." : " (returned on time, no fine)."));
            issueIdField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Issue ID must be a number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not return book: " + ex.getMessage());
        }
    }
}
