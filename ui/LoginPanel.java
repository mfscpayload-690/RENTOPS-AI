package ui;

import services.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

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
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(16);
        add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        loginButton = new JButton("Login");
        add(loginButton, gbc);
        gbc.gridx = 1;
        registerButton = new JButton("Register");
        add(registerButton, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(192,57,43));
        add(statusLabel, gbc);

        loginButton.addActionListener(evt -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (authService.login(username, password)) {
                statusLabel.setText("");
                onLoginSuccess.run();
            } else {
                statusLabel.setText("Invalid credentials");
            }
        });

        registerButton.addActionListener(evt -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (authService.register(username, password, "user")) {
                statusLabel.setText("Registration successful. Please login.");
            } else {
                statusLabel.setText("Registration failed");
            }
        });
    }
}
