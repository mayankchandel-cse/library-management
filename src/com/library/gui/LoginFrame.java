package com.library.gui;

import com.library.exceptions.InvalidInputException;
import com.library.model.User;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * LoginFrame — Library Management System (Part C: GUI).
 *
 * <p>Screen 1: login. Two roles — ADMIN and LIBRARIAN. Demo buttons fill
 * in the demo accounts so the application can be shown without typing.
 * Validation throws {@link InvalidInputException} for empty fields, which
 * is caught here and shown as a friendly message (exception handling).</p>
 */
public class LoginFrame extends JFrame {

    private final JTextField usernameField = Theme.styledTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);

    public LoginFrame() {
        super("Library Management System — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 420);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout(10, 10));

        // Header.
        JPanel header = Theme.panel();
        JLabel title = Theme.label("LIBRARY MANAGEMENT SYSTEM",
                Theme.TITLE_FONT, Theme.ACCENT);
        title.setHorizontalAlignment(JLabel.CENTER);
        JLabel sub = Theme.label("Books  •  Members  •  Issues  •  Fines",
                Theme.SUBHEADING_FONT, Theme.MUTED);
        sub.setHorizontalAlignment(JLabel.CENTER);
        header.setLayout(new BorderLayout());
        header.add(title, BorderLayout.CENTER);
        header.add(sub, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Form.
        JPanel form = Theme.panel();
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;

        passwordField.setFont(Theme.BODY_FONT);
        passwordField.setBackground(new java.awt.Color(0x2A, 0x3D, 0x66));
        passwordField.setForeground(Theme.TEXT);
        passwordField.setCaretColor(Theme.TEXT);

        c.gridx = 0; c.gridy = 0;
        form.add(Theme.label("Username:"), c);
        c.gridx = 1;
        form.add(usernameField, c);
        c.gridx = 0; c.gridy = 1;
        form.add(Theme.label("Password:"), c);
        c.gridx = 1;
        form.add(passwordField, c);

        JButton loginButton = Theme.styledButton("Login");
        loginButton.addActionListener(e -> doLogin());
        c.gridx = 1; c.gridy = 2;
        form.add(loginButton, c);
        add(form, BorderLayout.CENTER);

        // Demo buttons + hint.
        JPanel footer = Theme.panel();
        JButton adminButton = Theme.secondaryButton("Use Admin");
        adminButton.addActionListener(e -> {
            usernameField.setText(SampleData.ADMIN_USER);
            passwordField.setText(SampleData.ADMIN_PASS);
            doLogin();
        });
        JButton libButton = Theme.secondaryButton("Use Librarian");
        libButton.addActionListener(e -> {
            usernameField.setText(SampleData.LIB_USER);
            passwordField.setText(SampleData.LIB_PASS);
            doLogin();
        });
        footer.add(adminButton);
        footer.add(libButton);
        footer.add(Theme.label("Demo: admin / admin123  •  librarian / lib123",
                Theme.BODY_FONT, Theme.MUTED));
        add(footer, BorderLayout.SOUTH);

        // Pressing Enter in the password field logs in.
        passwordField.addActionListener(e -> doLogin());
    }

    /** Validate the fields, check the login, open MainFrame on success. */
    private void doLogin() {
        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                throw new InvalidInputException(
                        "Username and password must not be empty.");
            }
            User user = SampleData.checkLogin(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.\n"
                                + "Try the demo buttons below.",
                        "Login failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(user).setVisible(true));
        } catch (InvalidInputException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error during login: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
