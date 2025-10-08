package ui;

import services.AuthService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoutDialog extends JDialog {

    private AuthService authService;
    private JFrame parentFrame;
    private CardLayout parentCardLayout;
    private JPanel parentCardPanel;
    private boolean logoutConfirmed = false;

    public LogoutDialog(JFrame parent, AuthService authService, CardLayout cardLayout, JPanel cardPanel) {
        super(parent, "Logout Options", true);
        this.parentFrame = parent;
        this.authService = authService;
        this.parentCardLayout = cardLayout;
        this.parentCardPanel = cardPanel;

        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(400, 250);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Choose an Action");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Current user info
        String currentUserInfo = "Current User: " + authService.getCurrentUser().getUsername()
                + " (" + authService.getCurrentUser().getRole() + ")";
        JLabel userInfoLabel = new JLabel(currentUserInfo);
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfoLabel.setForeground(new Color(127, 140, 141));
        gbc.gridy++;
        gbc.gridwidth = 2;
        mainPanel.add(userInfoLabel, gbc);

        // Buttons
        gbc.gridwidth = 1;
        gbc.gridy++;

        // Logout button
        JButton logoutButton = createStyledButton("Logout", new Color(231, 76, 60));
        logoutButton.addActionListener(e -> performLogout());
        gbc.gridx = 0;
        mainPanel.add(logoutButton, gbc);

        // Switch user button
        JButton switchUserButton = createStyledButton("Switch User", new Color(52, 152, 219));
        switchUserButton.addActionListener(e -> switchUser());
        gbc.gridx = 1;
        mainPanel.add(switchUserButton, gbc);

        // Add user button (only show for admins)
        if (authService.isAdmin()) {
            gbc.gridy++;
            JButton addUserButton = createStyledButton("Add New User", new Color(46, 204, 113));
            addUserButton.addActionListener(e -> showAddUserDialog());
            gbc.gridx = 0;
            mainPanel.add(addUserButton, gbc);
        }

        // Cancel button
        gbc.gridy++;
        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> dispose());
        gbc.gridx = authService.isAdmin() ? 1 : 0;
        if (!authService.isAdmin()) {
            gbc.gridwidth = 2;
        }
        mainPanel.add(cancelButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(130, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (authService.logout()) {
                logoutConfirmed = true;
                dispose();
                // Show login screen
                parentCardLayout.show(parentCardPanel, "login");
                JOptionPane.showMessageDialog(parentFrame, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error during logout. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void switchUser() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Switch to a different user account?",
                "Switch User",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (authService.logout()) {
                logoutConfirmed = true;
                dispose();
                // Show login screen for user switching
                parentCardLayout.show(parentCardPanel, "login");
                JOptionPane.showMessageDialog(parentFrame, "Please login with a different account.", "Switch User", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error during user switch. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddUserDialog() {
        dispose(); // Close this dialog

        // Create a simple add user dialog
        JDialog addUserDialog = new JDialog(parentFrame, "Add New User", true);
        addUserDialog.setSize(350, 200);
        addUserDialog.setLocationRelativeTo(parentFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Quick Add User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Role:"), gbc);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"user", "admin"});
        gbc.gridx = 1;
        panel.add(roleBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton createButton = createStyledButton("Create", new Color(46, 204, 113));
        createButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(addUserDialog, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authService.register(username, password, role)) {
                JOptionPane.showMessageDialog(addUserDialog, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                addUserDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(addUserDialog, "Failed to create user: " + authService.getLastError(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = createStyledButton("Cancel", new Color(149, 165, 166));
        cancelButton.addActionListener(e -> addUserDialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        addUserDialog.add(panel);
        addUserDialog.setVisible(true);
    }

    public boolean isLogoutConfirmed() {
        return logoutConfirmed;
    }
}
