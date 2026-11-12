package com.library.main;

import com.library.gui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main — Library Management System (Part C: GUI).
 *
 * <p>Application entry point. Sets the Nimbus look-and-feel (with a
 * fallback to the platform look-and-feel) and opens the login window
 * on the Swing event-dispatch thread (EDT) — all Swing work must happen
 * on the EDT.</p>
 *
 * <p>Run with: {@code java -cp out com.library.main.Main}</p>
 * <p>Tip: run {@code com.library.main.LibraryDemo} to see the console
 * demonstration (searching, sorting, issue/return, fines, file save).</p>
 */
public class Main {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info
                    : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus look-and-feel not available ("
                    + e.getMessage() + ") — using the default look-and-feel.");
        }
        System.out.println("Library Management System started.");
        System.out.println("Tip: run com.library.main.LibraryDemo to see the "
                + "console demonstration (searching, sorting, issue/return, fines).");
        System.out.println("Demo login: admin / admin123  |  librarian / lib123");
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
