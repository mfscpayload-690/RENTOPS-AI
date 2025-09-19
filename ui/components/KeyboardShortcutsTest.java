package ui.components;

import java.awt.*;
import javax.swing.*;

/**
 * Test class for KeyboardShortcuts
 */
public class KeyboardShortcutsTest extends JFrame {

    public KeyboardShortcutsTest() {
        setTitle("KeyboardShortcuts Test");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JTextField searchField = new JTextField(20);
        searchField.setName("searchField");
        mainPanel.add(searchField, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setText("Press Ctrl+F to focus search field\n"
                + "Press Ctrl+L to logout\n"
                + "Press Ctrl+H for help\n"
                + "Press Home to go to dashboard\n"
                + "Press Esc to close dialogs");
        JScrollPane scrollPane = new JScrollPane(infoArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton dashboardButton = new JButton("Dashboard");
        dashboardButton.setName("home");
        dashboardButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Dashboard clicked"));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Logout clicked"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(dashboardButton);
        buttonPanel.add(logoutButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Initialize keyboard shortcuts
        KeyboardShortcuts.initialize(getRootPane());

        // Shortcut hint will be shown after the frame is visible
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KeyboardShortcutsTest test = new KeyboardShortcutsTest();
            test.setLocationRelativeTo(null);
            test.setVisible(true);

            // Show keyboard shortcut hint after the frame is visible
            Timer timer = new Timer(1000, e -> KeyboardShortcuts.showShortcutHint(test));
            timer.setRepeats(false);
            timer.start();
        });
    }
}
