package ui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import services.AuthService;

public class LoginPanel extends JPanel {
    private AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;
    private boolean isRegistrationMode = false;
    private JPasswordField confirmPasswordField;
    private JLabel confirmPasswordLabel;

    public LoginPanel(AuthService authService, Runnable onLoginSuccess) {
        this.authService = authService;
        setLayout(new GridBagLayout());
        setBackground(new Color(245,247,250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Rentops-AI Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52,73,94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(16);
        usernameField.setPreferredSize(new Dimension(200, 30));
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(16);
        passwordField.setPreferredSize(new Dimension(200, 30));
        add(passwordField, gbc);

        // Confirm password field (initially hidden)
        gbc.gridx = 0; gbc.gridy++;
        confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setVisible(false);
        add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(16);
        confirmPasswordField.setPreferredSize(new Dimension(200, 30));
        confirmPasswordField.setVisible(false);
        add(confirmPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setBackground(new Color(52,73,94));
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

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(192,57,43));
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
    }

    private void performLogin(Runnable onLoginSuccess) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            statusLabel.setText("Please enter a username");
            statusLabel.setForeground(new Color(192,57,43));
            return;
        }
        
        if (password.isEmpty()) {
            statusLabel.setText("Please enter a password");
            statusLabel.setForeground(new Color(192,57,43));
            return;
        }
        
        if (authService.login(username, password)) {
            statusLabel.setText("Login successful!");
            statusLabel.setForeground(new Color(39, 174, 96));
            clearFields();
            onLoginSuccess.run();
        } else {
            statusLabel.setText(authService.getLastError());
            statusLabel.setForeground(new Color(192,57,43));
        }
    }

    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (username.isEmpty()) {
            statusLabel.setText("Please enter a username");
            statusLabel.setForeground(new Color(192,57,43));
            return;
        }
        
        if (password.isEmpty()) {
            statusLabel.setText("Please enter a password");
            statusLabel.setForeground(new Color(192,57,43));
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            statusLabel.setForeground(new Color(192,57,43));
            return;
        }
        
        if (authService.register(username, password, "user")) {
            statusLabel.setText("Registration successful! You can now login.");
            statusLabel.setForeground(new Color(39, 174, 96));
            switchToLoginMode();
            clearFields();
        } else {
            statusLabel.setText(authService.getLastError());
            statusLabel.setForeground(new Color(192,57,43));
        }
    }

    private void switchToRegistrationMode() {
        isRegistrationMode = true;
        confirmPasswordLabel.setVisible(true);
        confirmPasswordField.setVisible(true);
        loginButton.setText("Register");
        registerButton.setText("Back to Login");
        statusLabel.setText("Enter your details to create a new account");
        statusLabel.setForeground(new Color(52,73,94));
        clearFields();
        revalidate();
        repaint();
    }

    private void switchToLoginMode() {
        isRegistrationMode = false;
        confirmPasswordLabel.setVisible(false);
        confirmPasswordField.setVisible(false);
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
    }
}
