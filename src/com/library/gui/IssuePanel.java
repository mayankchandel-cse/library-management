package com.library.gui;

import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.Member;

import javax.swing.JButton;
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
import java.time.LocalDate;

/**
 * IssuePanel — Library Management System (Part C: GUI).
 *
 * <p>Screen 6: issue and return. Issue a book by book id + member id —
 * the due date shown is today + 14 days. Return a book by issue id —
 * the fine shown is Rs 5 per day late (0 if returned on time).
 * The table lists all currently active issues.</p>
 */
public class IssuePanel extends JPanel {

    private final MainFrame parent;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Issue ID", "Book", "Member", "Issue Date",
                    "Due Date", "Status", "Fine (Rs)"}, 0);
    private final JTable table = new JTable(tableModel);

    private final JTextField issueBookField = Theme.styledTextField(8);
    private final JTextField issueMemberField = Theme.styledTextField(8);
    private final JTextField returnIssueField = Theme.styledTextField(8);
    private final JLabel dueLabel = Theme.label(
            "Due date: " + LocalDate.now().plusDays(14),
            Theme.BODY_FONT, Theme.MUTED);
    private final JLabel fineLabel = Theme.label("Fine: —",
            Theme.SUBHEADING_FONT, Theme.ACCENT);

    public IssuePanel(MainFrame parent) {
        super(new BorderLayout(10, 10));
        this.parent = parent;
        setBackground(Theme.BG);

        add(Theme.label("Issue / Return Books", Theme.HEADING_FONT, Theme.ACCENT),
                BorderLayout.NORTH);

        JPanel forms = new JPanel(new GridLayout(1, 2, 12, 12));
        forms.setBackground(Theme.BG);

        // Issue form.
        JPanel issueForm = new JPanel(new GridLayout(0, 2, 8, 8));
        issueForm.setBackground(Theme.PANEL);
        issueForm.add(Theme.label("Book ID:")); issueForm.add(issueBookField);
        issueForm.add(Theme.label("Member ID:")); issueForm.add(issueMemberField);
        issueForm.add(dueLabel); issueForm.add(new JLabel(""));
        JButton issueButton = Theme.styledButton("Issue Book");
        issueButton.addActionListener(e -> doIssue());
        issueForm.add(issueButton);
        issueForm.add(Theme.label("14-day loan • Rs 5/day late fine",
                Theme.BODY_FONT, Theme.MUTED));
        forms.add(issueForm);

        // Return form.
        JPanel returnForm = new JPanel(new GridLayout(0, 2, 8, 8));
        returnForm.setBackground(Theme.PANEL);
        returnForm.add(Theme.label("Issue ID:")); returnForm.add(returnIssueField);
        returnForm.add(fineLabel); returnForm.add(new JLabel(""));
        JButton returnButton = Theme.styledButton("Return Book");
        returnButton.addActionListener(e -> doReturn());
        returnForm.add(returnButton);
        returnForm.add(Theme.label("Fine is calculated on return.",
                Theme.BODY_FONT, Theme.MUTED));
        forms.add(returnForm);
        add(forms, BorderLayout.CENTER);

        JPanel bottom = Theme.panel();
        bottom.setLayout(new BorderLayout(8, 8));
        bottom.add(Theme.label("Active Issues", Theme.SUBHEADING_FONT, Theme.TEXT),
                BorderLayout.NORTH);
        Theme.styleTable(table);
        bottom.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel hint = Theme.panel();
        hint.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        hint.add(Theme.label("Tip: use the Book ID and Member ID from the Books / Members screens.",
                Theme.BODY_FONT, Theme.MUTED));
        bottom.add(hint, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
        refresh();
    }

    private void doIssue() {
        try {
            int bookId = Integer.parseInt(issueBookField.getText().trim());
            int memberId = Integer.parseInt(issueMemberField.getText().trim());
            IssueRecord record = parent.issueBook(bookId, memberId);
            if (record != null) {
                Book book = SampleData.findBookById(parent.getBooks(), bookId);
                Member member = SampleData.findMemberById(parent.getMembers(), memberId);
                JOptionPane.showMessageDialog(this,
                        "Issued \"" + (book != null ? book.getTitle() : bookId) + "\""
                                + " to " + (member != null ? member.getName() : memberId)
                                + ".\nIssue ID: " + record.getId()
                                + "  •  Due: " + record.getDueDate(),
                        "Book issued", JOptionPane.INFORMATION_MESSAGE);
                issueBookField.setText("");
                issueMemberField.setText("");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Book ID and Member ID must be numbers.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void doReturn() {
        try {
            int issueId = Integer.parseInt(returnIssueField.getText().trim());
            double fine = parent.returnBook(issueId);
            if (fine < 0) {
                JOptionPane.showMessageDialog(this,
                        "No active issue with id " + issueId + ".",
                        "Return failed", JOptionPane.WARNING_MESSAGE);
                return;
            }
            fineLabel.setText("Fine: Rs " + String.format("%.0f", fine)
                    + (fine > 0 ? " (late return)" : " (on time)"));
            JOptionPane.showMessageDialog(this,
                    "Book returned.\nFine: Rs " + String.format("%.0f", fine),
                    "Book returned", JOptionPane.INFORMATION_MESSAGE);
            returnIssueField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Issue ID must be a number.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Rebuild the active-issues table. */
    public void refresh() {
        tableModel.setRowCount(0);
        for (IssueRecord r : parent.getIssues()) {
            // Active = not yet returned (a late return is marked "Overdue"
            // by the service, so status alone is not enough).
            if (r.getReturnDate() == null) {
                Book b = SampleData.findBookById(parent.getBooks(), r.getBookId());
                Member m = SampleData.findMemberById(parent.getMembers(), r.getMemberId());
                tableModel.addRow(new Object[]{
                        r.getId(),
                        b != null ? b.getTitle() : ("Book #" + r.getBookId()),
                        m != null ? m.getName() : ("Member #" + r.getMemberId()),
                        r.getIssueDate(), r.getDueDate(), r.getStatus(),
                        String.format("%.0f", r.getFine())});
            }
        }
    }
}
