package ui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import services.AuthService;
import utils.ModernTheme;
import ui.components.PasswordResetDialog;

/**
 * ModernLoginPanel - Professional dark-themed login interface
 * 
 * Features:
 * - Split-panel design: sidebar with mode buttons + main login card
 * - Dark color scheme with orange accent buttons
 * - Smooth hover effects and rounded corners
 * - "Remember me" checkbox and "Forgot password?" link
 * - Responsive layout that scales gracefully
 * - Toggle between Login and Registration modes
 * 
 * Customization:
 * - Colors: Adjust ModernTheme constants
 * - Layout: Modify createSidePanel() and createMainPanel()
 * - Fonts: Change in ModernTheme.java
 */
public class LoginPanel extends JPanel {

    private AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    private JLabel statusLabel;
    
    private JLabel forgotPasswordLabel;
    
    // Registration mode fields
    private boolean isRegistrationMode = false;
    private JPasswordField confirmPasswordField;
    private JLabel confirmPasswordLabel;
    private JComboBox<String> roleComboBox;
    private JLabel roleLabel;
    private JTextField organizationField;
    private JLabel organizationLabel;
    

    public LoginPanel(AuthService authService, Runnable onLoginSuccess) {
        this.authService = authService;
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Rentops-AI Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(16);
        usernameField.setPreferredSize(new Dimension(200, 30));
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(16);
        passwordField.setPreferredSize(new Dimension(200, 30));
        add(passwordField, gbc);

        // Confirm password field (initially hidden)
        gbc.gridx = 0;
        gbc.gridy++;
        confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setVisible(false);
        add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(16);
        confirmPasswordField.setPreferredSize(new Dimension(200, 30));
        confirmPasswordField.setVisible(false);
        add(confirmPasswordField, gbc);

        // Role selection (initially hidden)
        gbc.gridx = 0;
        gbc.gridy++;
        roleLabel = new JLabel("Role:");
        roleLabel.setVisible(false);
        add(roleLabel, gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"user", "admin"});
        roleComboBox.setPreferredSize(new Dimension(200, 30));
        roleComboBox.setVisible(false);
        add(roleComboBox, gbc);

        // Organization field (initially hidden)
        gbc.gridx = 0;
        gbc.gridy++;
        organizationLabel = new JLabel("Organization:");
        organizationLabel.setVisible(false);
        add(organizationLabel, gbc);
        gbc.gridx = 1;
        organizationField = new JTextField(16);
        organizationField.setPreferredSize(new Dimension(200, 30));
        organizationField.setVisible(false);
        add(organizationField, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setBackground(new Color(52, 73, 94));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        add(loginButton, gbc);

    gbc.gridx = 1;
    registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(100, 35));
        registerButton.setBackground(new Color(142, 68, 173));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        add(registerButton, gbc);

    // Forgot password link
    gbc.gridx = 1;
    gbc.gridy++;
    JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    linksPanel.setOpaque(false);
    forgotPasswordLabel = new JLabel("<html><a href='#'>Forgot password?</a></html>");
    forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    linksPanel.add(forgotPasswordLabel);
    add(linksPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(192, 57, 43));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(statusLabel, gbc);

        // Add Enter key support
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (isRegistrationMode) {
                        performRegistration();
                    } else {
                        performLogin(onLoginSuccess);
                    }
                }
            }
        };

        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);
        organizationField.addKeyListener(enterKeyListener);

        loginButton.addActionListener(evt -> {
            if (isRegistrationMode) {
                performRegistration();
            } else {
                performLogin(onLoginSuccess);
            }
        });

        registerButton.addActionListener(evt -> {
            if (isRegistrationMode) {
                // Switch back to login mode
                switchToLoginMode();
            } else {
                // Switch to registration mode
                switchToRegistrationMode();
            }
        });

        // Hook up forgot password dialog
        forgotPasswordLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                Window owner = SwingUtilities.getWindowAncestor(LoginPanel.this);
                PasswordResetDialog dlg = new PasswordResetDialog(owner, authService);
                dlg.setVisible(true);
            }
        });
    }

    private void performLogin(Runnable onLoginSuccess) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            statusLabel.setText("Please enter a username");
            statusLabel.setForeground(new Color(192, 57, 43));
            return;
        }

        if (password.isEmpty()) {
            statusLabel.setText("Please enter a password");
            statusLabel.setForeground(new Color(192, 57, 43));
            return;
        }

        if (authService.login(username, password)) {
            statusLabel.setText("Login successful!");
            statusLabel.setForeground(new Color(39, 174, 96));
            clearFields();
            onLoginSuccess.run();
        } else {
            statusLabel.setText(authService.getLastError());
            statusLabel.setForeground(new Color(192, 57, 43));
        }
    }

    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();
        String organization = organizationField.getText().trim();

        if (username.isEmpty()) {
            statusLabel.setText("Please enter a username");
            statusLabel.setForeground(new Color(192, 57, 43));
            return;
        }

        if (password.isEmpty()) {
            statusLabel.setText("Please enter a password");
            statusLabel.setForeground(new Color(192, 57, 43));
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            statusLabel.setForeground(new Color(192, 57, 43));
            return;
        }

        // Organization is optional, so empty string is converted to null
        String orgToSave = organization.isEmpty() ? null : organization;

        if (authService.register(username, password, selectedRole, orgToSave)) {
            statusLabel.setText("Registration successful! You can now login.");
            statusLabel.setForeground(new Color(39, 174, 96));
            switchToLoginMode();
            clearFields();
        } else {
            statusLabel.setText(authService.getLastError());
            statusLabel.setForeground(new Color(192, 57, 43));
        }
    }

    private void switchToRegistrationMode() {
        isRegistrationMode = true;
        confirmPasswordLabel.setVisible(true);
        confirmPasswordField.setVisible(true);
        roleLabel.setVisible(true);
        roleComboBox.setVisible(true);
        organizationLabel.setVisible(true);
        organizationField.setVisible(true);
        loginButton.setText("Register");
        registerButton.setText("Back to Login");
        statusLabel.setText("Enter your details to create a new account");
        statusLabel.setForeground(new Color(52, 73, 94));
        clearFields();
        revalidate();
        repaint();
    }

    private void switchToLoginMode() {
        isRegistrationMode = false;
        confirmPasswordLabel.setVisible(false);
        confirmPasswordField.setVisible(false);
        roleLabel.setVisible(false);
        roleComboBox.setVisible(false);
        organizationLabel.setVisible(false);
        organizationField.setVisible(false);
        loginButton.setText("Login");
        registerButton.setText("Register");
        statusLabel.setText(" ");
        clearFields();
        revalidate();
        repaint();
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        organizationField.setText("");
        roleComboBox.setSelectedIndex(0); // Default to "user"
    }
}
