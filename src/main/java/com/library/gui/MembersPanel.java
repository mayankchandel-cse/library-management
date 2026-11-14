package com.library.gui;

import com.library.model.IssueRecord;
import com.library.model.Member;
import com.library.service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
 * MembersPanel - register members and view their borrowing history.
 *
 * <p>The top form registers a member through
 * {@link LibraryService#registerMember(Member)}. Clicking a member row
 * loads that member's borrowing history (below the table) through
 * {@link LibraryService#getBorrowingHistory(int)}.</p>
 */
public class MembersPanel extends JPanel {

    private static final String[] MEMBER_COLUMNS = {
            "ID", "Name", "Email", "Phone", "Type", "Max Books"
    };
    private static final String[] HISTORY_COLUMNS = {
            "Issue ID", "Book", "Issue Date", "Due Date", "Return Date", "Fine Rs", "Status"
    };

    private final LibraryService service;
    private final DefaultTableModel memberModel;
    private final DefaultTableModel historyModel;
    private final JTable memberTable;
    private final JTextField nameField;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JComboBox<String> typeCombo;

    public MembersPanel(LibraryService service) {
        this.service = service;
        setBackground(Theme.PANEL);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(Theme.label("Members", Theme.TITLE_FONT, Theme.ACCENT), BorderLayout.NORTH);

        // --- Add Member form ---
        JPanel form = new JPanel(new GridLayout(2, 4, 8, 8));
        form.setBackground(Theme.PANEL);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.ACCENT), "Add Member"));
        nameField = Theme.styledTextField(15);
        emailField = Theme.styledTextField(15);
        phoneField = Theme.styledTextField(15);
        typeCombo = new JComboBox<>(new String[]{"Student", "Faculty", "Staff"});
        typeCombo.setFont(Theme.BODY_FONT);
        form.add(styledLabel("Name:"));
        form.add(styledLabel("Email:"));
        form.add(styledLabel("Phone:"));
        form.add(styledLabel("Type:"));
        form.add(nameField);
        form.add(emailField);
        form.add(phoneField);
        form.add(typeCombo);

        JButton addButton = Theme.styledButton("Add Member");
        addButton.addActionListener(e -> addMember());
        JPanel formWrap = new JPanel(new BorderLayout(8, 8));
        formWrap.setBackground(Theme.PANEL);
        formWrap.add(form, BorderLayout.CENTER);
        JPanel formButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        formButtons.setBackground(Theme.PANEL);
        formButtons.add(addButton);
        formWrap.add(formButtons, BorderLayout.SOUTH);

        // --- Member table ---
        memberModel = new DefaultTableModel(MEMBER_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(memberModel);
        Theme.styleTable(memberTable);
        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showHistoryForSelected();
            }
        });

        // --- Borrowing history table ---
        historyModel = new DefaultTableModel(HISTORY_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = new JTable(historyModel);
        Theme.styleTable(historyTable);

        JPanel centre = new JPanel(new BorderLayout(10, 10));
        centre.setBackground(Theme.PANEL);
        centre.add(formWrap, BorderLayout.NORTH);

        JPanel tables = new JPanel(new GridLayout(2, 1, 10, 10));
        tables.setBackground(Theme.PANEL);
        JPanel memberWrap = new JPanel(new BorderLayout());
        memberWrap.setBackground(Theme.PANEL);
        memberWrap.add(Theme.label("All Members (click a row to see borrowing history)",
                Theme.SUBHEADING_FONT, Theme.ACCENT), BorderLayout.NORTH);
        memberWrap.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        JPanel historyWrap = new JPanel(new BorderLayout());
        historyWrap.setBackground(Theme.PANEL);
        historyWrap.add(Theme.label("Borrowing History",
                Theme.SUBHEADING_FONT, Theme.ACCENT), BorderLayout.NORTH);
        historyWrap.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        tables.add(memberWrap);
        tables.add(historyWrap);
        centre.add(tables, BorderLayout.CENTER);

        add(centre, BorderLayout.CENTER);
        refresh();
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.TEXT);
        l.setFont(Theme.BODY_FONT);
        return l;
    }

    /** Reloads all members from the database. */
    public void refresh() {
        memberModel.setRowCount(0);
        for (Member m : service.getAllMembers()) {
            memberModel.addRow(new Object[]{
                    m.getId(), m.getName(), m.getEmail(), m.getPhone(),
                    m.getMemberType(), m.getMaxBooks()
            });
        }
    }

    private void addMember() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return;
        }
        try {
            Member member = new Member();
            member.setName(name);
            member.setEmail(email);
            member.setPhone(phone);
            member.setMemberType(type);
            // Borrowing limit by member type.
            member.setMaxBooks(switch (type) {
                case "Faculty" -> 5;
                case "Staff" -> 4;
                default -> 3; // Student
            });
            service.registerMember(member);
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            refresh();
            JOptionPane.showMessageDialog(this, "Member \"" + name + "\" registered.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not register member: " + ex.getMessage());
        }
    }

    private void showHistoryForSelected() {
        int row = memberTable.getSelectedRow();
        historyModel.setRowCount(0);
        if (row < 0) {
            return;
        }
        int memberId = (int) memberModel.getValueAt(row, 0);
        List<IssueRecord> history = service.getBorrowingHistory(memberId);
        for (IssueRecord r : history) {
            historyModel.addRow(new Object[]{
                    r.getId(), r.getBookTitle(), r.getIssueDate(), r.getDueDate(),
                    r.getReturnDate(), r.getFine(), r.getStatus()
            });
        }
    }
}
