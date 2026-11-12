package com.library.gui;

import com.library.exceptions.InvalidInputException;
import com.library.model.Member;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * MembersPanel — Library Management System (Part C: GUI).
 *
 * <p>Screen 5: members. A JTable of all members and an Add Member form
 * (name, email, phone, membership type). Validation throws
 * {@link InvalidInputException} for empty/invalid fields.</p>
 */
public class MembersPanel extends JPanel {

    private final MainFrame parent;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Email", "Phone", "Type"}, 0);
    private final JTable table = new JTable(tableModel);

    private final JTextField nameField = Theme.styledTextField(18);
    private final JTextField emailField = Theme.styledTextField(18);
    private final JTextField phoneField = Theme.styledTextField(18);
    private final JComboBox<String> typeBox = new JComboBox<>(
            new String[]{"Student", "Faculty", "Staff"});

    public MembersPanel(MainFrame parent) {
        super(new BorderLayout(10, 10));
        this.parent = parent;
        setBackground(Theme.BG);

        add(Theme.label("Members", Theme.HEADING_FONT, Theme.ACCENT),
                BorderLayout.NORTH);
        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setBackground(Theme.BG);
        form.add(Theme.label("Name:")); form.add(nameField);
        form.add(Theme.label("Email:")); form.add(emailField);
        form.add(Theme.label("Phone:")); form.add(phoneField);
        form.add(Theme.label("Membership type:")); form.add(typeBox);

        JPanel south = Theme.panel();
        south.setLayout(new BorderLayout(8, 8));
        south.add(form, BorderLayout.CENTER);
        JButton addButton = Theme.styledButton("Add Member");
        addButton.addActionListener(e -> addMember());
        JPanel btnRow = Theme.panel();
        btnRow.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnRow.add(addButton);
        btnRow.add(Theme.label("New members get the next free member id.",
                Theme.BODY_FONT, Theme.MUTED));
        south.add(btnRow, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
        refresh();
    }

    /** Validate the form and add the member (service + in-memory). */
    private void addMember() {
        try {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                throw new InvalidInputException("Name and email must not be empty.");
            }
            if (!email.contains("@") || !email.contains(".")) {
                throw new InvalidInputException("Enter a valid email address.");
            }
            if (!phone.isEmpty() && !phone.matches("[0-9 +\\-()]+")) {
                throw new InvalidInputException("Phone must contain digits only.");
            }
            parent.addMember(new Member(parent.nextMemberId(), name, email, phone,
                    (String) typeBox.getSelectedItem()));
            nameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            JOptionPane.showMessageDialog(this, "Member added.",
                    "Members", JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidInputException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Rebuild the members table. */
    public void refresh() {
        tableModel.setRowCount(0);
        for (Member m : parent.getMembers()) {
            tableModel.addRow(new Object[]{
                    m.getId(), m.getName(), m.getEmail(), m.getPhone(),
                    m.getMembershipType()});
        }
    }
}
