package ui.components;

import services.AuthService;

import javax.swing.*;
import java.awt.*;

public class PasswordResetDialog extends JDialog {
    private final AuthService authService;
    private final Runnable onSuccessCallback;

    private JTextField usernameField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton resetButton;
    private JButton cancelButton;
    private JLabel messageLabel;

    public PasswordResetDialog(Window owner, AuthService authService) {
        this(owner, authService, null);
    }

    public PasswordResetDialog(Window owner, AuthService authService, Runnable onSuccessCallback) {
        super(owner, "Forgot Password", ModalityType.APPLICATION_MODAL);
        this.authService = authService;
        this.onSuccessCallback = onSuccessCallback;
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JLabel title = new JLabel("Reset your password");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(title, gbc);

        row++; gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1; usernameField = new JTextField(20); panel.add(usernameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("New Password"), gbc);
        gbc.gridx = 1; newPasswordField = new JPasswordField(20); panel.add(newPasswordField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Confirm Password"), gbc);
        gbc.gridx = 1; confirmPasswordField = new JPasswordField(20); panel.add(confirmPasswordField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(new Color(200, 80, 80));
        panel.add(messageLabel, gbc);

        row++;
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        resetButton = new JButton("Reset Password");
        cancelButton = new JButton("Cancel");
        buttons.add(cancelButton);
        buttons.add(resetButton);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; panel.add(buttons, gbc);

        setContentPane(panel);
        getRootPane().setDefaultButton(resetButton);
        pack();
        setLocationRelativeTo(getOwner());

        cancelButton.addActionListener(e -> dispose());
        resetButton.addActionListener(e -> onReset());
    }

    private void onReset() {
        String username = usernameField.getText().trim();
        String pwd = new String(newPasswordField.getPassword());
        String cpwd = new String(confirmPasswordField.getPassword());

        if (username.isEmpty()) { messageLabel.setText("Please enter your username"); return; }
        if (pwd.length() < 4) { messageLabel.setText("Password must be at least 4 characters"); return; }
        if (!pwd.equals(cpwd)) { messageLabel.setText("Passwords do not match"); return; }

        resetButton.setEnabled(false);
        try {
            boolean ok = authService.resetPassword(username, pwd);
            if (ok) {
                dispose();
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                } else {
                    JOptionPane.showMessageDialog(getOwner(), "Password has been reset. Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                messageLabel.setText(authService.getLastError());
            }
        } finally {
            resetButton.setEnabled(true);
        }
    }
}
