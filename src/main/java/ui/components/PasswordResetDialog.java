package ui.components;

import dao.UserDAO;
import services.AuthService;
import utils.ModernTheme;

import javax.swing.*;
import java.awt.*;

public class PasswordResetDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private UserDAO userDAO;
    private AuthService authService;
    private Runnable onSuccess;

    public PasswordResetDialog(Frame parent) {
        this((Window) parent, null, null);
    }
    
    public PasswordResetDialog(Window parent, AuthService authService) {
        this(parent, authService, null);
    }
    
    public PasswordResetDialog(Window parent, AuthService authService, Runnable onSuccess) {
        super(parent, "Reset Password", Dialog.ModalityType.APPLICATION_MODAL);
        this.authService = authService;
        this.userDAO = new UserDAO();
        this.onSuccess = onSuccess;
        
        setSize(400, 300);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(ModernTheme.BG_PRIMARY);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ModernTheme.BG_PRIMARY);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        userLabel.setFont(ModernTheme.MEDIUM_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(ModernTheme.MEDIUM_FONT);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        newPassLabel.setFont(ModernTheme.MEDIUM_FONT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(newPassLabel, gbc);

        newPasswordField = new JPasswordField(20);
        newPasswordField.setFont(ModernTheme.MEDIUM_FONT);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(newPasswordField, gbc);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        confirmLabel.setFont(ModernTheme.MEDIUM_FONT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(confirmLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(ModernTheme.MEDIUM_FONT);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(confirmPasswordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ModernTheme.BG_PRIMARY);

        JButton resetButton = new JButton("Reset");
        resetButton.setFont(ModernTheme.MEDIUM_BOLD_FONT);
        resetButton.setBackground(ModernTheme.ACCENT_BLUE);
        resetButton.setForeground(ModernTheme.TEXT_PRIMARY);
        resetButton.addActionListener(e -> handleReset());
        buttonPanel.add(resetButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(ModernTheme.MEDIUM_FONT);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleReset() {
        String username = usernameField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean success = userDAO.resetPassword(username, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(this, "Password reset successfully!", "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reset password. User not found.", "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
