package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import services.AuthService;
import ui.components.PasswordResetDialog;
import ui.components.Toast;
import utils.ModernTheme;

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
public class ModernLoginPanel extends JPanel {

    private final AuthService authService;
    private final Runnable onLoginSuccess;
    
    // UI Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton sidebarLoginButton;
    private JButton sidebarRegisterButton;
    private JLabel statusLabel;
    private JCheckBox rememberMeCheckBox;
    private JLabel forgotPasswordLabel;
    private JLabel titleLabel;
    
    // Registration mode fields
    private boolean isRegistrationMode = false;
    private JPasswordField confirmPasswordField;
    private JLabel confirmPasswordLabel;
    private JComboBox<String> roleComboBox;
    private JLabel roleLabel;
    private JTextField organizationField;
    private JLabel organizationLabel;
    private JPanel mainCardPanel;
    private JPanel formPanel;

    public ModernLoginPanel(AuthService authService, Runnable onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
        
        setLayout(new BorderLayout());
        setBackground(ModernTheme.BG_DARK);
        
        // Create the split layout: sidebar + main content
        add(createSidePanel(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);
    }

    /**
     * Create the left sidebar with "Log In" and "Register" buttons
     */
    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(ModernTheme.BG_DARKER);
        sidePanel.setPreferredSize(new Dimension(ModernTheme.SIDEBAR_WIDTH, 0));
        sidePanel.setBorder(new EmptyBorder(ModernTheme.PADDING_LARGE, 
                                            ModernTheme.PADDING_MEDIUM, 
                                            ModernTheme.PADDING_LARGE, 
                                            ModernTheme.PADDING_MEDIUM));

        // Welcome label at top
        JLabel welcomeLabel = new JLabel("Welcome");
        welcomeLabel.setFont(ModernTheme.FONT_TITLE);
        welcomeLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        sidePanel.add(Box.createVerticalStrut(40));
        sidePanel.add(welcomeLabel);
        sidePanel.add(Box.createVerticalStrut(30));

        // "Log In" button
        sidebarLoginButton = createSidebarButton("Log In", true);
        sidebarLoginButton.addActionListener(e -> switchToLoginMode());
        sidePanel.add(sidebarLoginButton);
        sidePanel.add(Box.createVerticalStrut(15));

        // "Register" button
        sidebarRegisterButton = createSidebarButton("Register", false);
        sidebarRegisterButton.addActionListener(e -> switchToRegistrationMode());
        sidePanel.add(sidebarRegisterButton);

        sidePanel.add(Box.createVerticalGlue());

        return sidePanel;
    }

    /**
     * Create a styled sidebar button
     */
    private JButton createSidebarButton(String text, boolean isSelected) {
        JButton button = new JButton(text);
        button.setFont(ModernTheme.FONT_BUTTON);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, ModernTheme.BUTTON_HEIGHT));
        button.setPreferredSize(new Dimension(180, ModernTheme.BUTTON_HEIGHT));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isSelected) {
            button.setBackground(ModernTheme.ACCENT_BLUE_DARK);
            button.setForeground(ModernTheme.TEXT_PRIMARY);
        } else {
            button.setBackground(ModernTheme.BG_DARKER);
            button.setForeground(ModernTheme.TEXT_SECONDARY);
        }

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.getBackground().equals(ModernTheme.ACCENT_BLUE_DARK)) {
                    button.setBackground(new Color(35, 35, 35));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(ModernTheme.ACCENT_BLUE_DARK)) {
                    button.setBackground(ModernTheme.BG_DARKER);
                }
            }
        });

        return button;
    }

    /**
     * Create the main content area with the login/register card
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ModernTheme.BG_DARK);

        // Create card panel (centered)
        mainCardPanel = new JPanel();
        mainCardPanel.setLayout(new BoxLayout(mainCardPanel, BoxLayout.Y_AXIS));
        mainCardPanel.setBackground(ModernTheme.BG_CARD);
        mainCardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1),
            new EmptyBorder(ModernTheme.PADDING_LARGE * 2, 
                           ModernTheme.PADDING_LARGE * 2, 
                           ModernTheme.PADDING_LARGE * 2, 
                           ModernTheme.PADDING_LARGE * 2)
        ));

        // Title
        titleLabel = new JLabel("Admin Log In");
        titleLabel.setFont(ModernTheme.FONT_TITLE);
        titleLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainCardPanel.add(titleLabel);
        mainCardPanel.add(Box.createVerticalStrut(30));

        // Form panel
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(ModernTheme.BG_CARD);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username/Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(ModernTheme.FONT_REGULAR);
        emailLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(8));

        usernameField = ModernTheme.createModernTextField("Enter your email");
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(400, ModernTheme.INPUT_HEIGHT));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(ModernTheme.FONT_REGULAR);
        passwordLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(8));

        passwordField = ModernTheme.createModernPasswordField("Enter your password");
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(400, ModernTheme.INPUT_HEIGHT));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(8));

        // Confirm password (for registration, initially hidden)
        confirmPasswordLabel = new JLabel("Confirm Password");
        confirmPasswordLabel.setFont(ModernTheme.FONT_REGULAR);
        confirmPasswordLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        confirmPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordLabel.setVisible(false);
        formPanel.add(confirmPasswordLabel);
        
        confirmPasswordField = ModernTheme.createModernPasswordField("Confirm your password");
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordField.setMaximumSize(new Dimension(400, ModernTheme.INPUT_HEIGHT));
        confirmPasswordField.setVisible(false);
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createVerticalStrut(8));

        // Role selection (for registration, initially hidden)
        roleLabel = new JLabel("Role");
        roleLabel.setFont(ModernTheme.FONT_REGULAR);
        roleLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleLabel.setVisible(false);
        formPanel.add(roleLabel);
        formPanel.add(Box.createVerticalStrut(8));
        
        roleComboBox = new JComboBox<>(new String[]{"user", "admin"});
        roleComboBox.setFont(ModernTheme.FONT_REGULAR);
        roleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        roleComboBox.setMaximumSize(new Dimension(400, ModernTheme.INPUT_HEIGHT));
        roleComboBox.setVisible(false);
        formPanel.add(roleComboBox);
        formPanel.add(Box.createVerticalStrut(8));

        // Organization field (for registration, initially hidden)
        organizationLabel = new JLabel("Organization (Optional)");
        organizationLabel.setFont(ModernTheme.FONT_REGULAR);
        organizationLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        organizationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        organizationLabel.setVisible(false);
        formPanel.add(organizationLabel);
        formPanel.add(Box.createVerticalStrut(8));
        
        organizationField = ModernTheme.createModernTextField("Your organization");
        organizationField.setAlignmentX(Component.LEFT_ALIGNMENT);
        organizationField.setMaximumSize(new Dimension(400, ModernTheme.INPUT_HEIGHT));
        organizationField.setVisible(false);
        formPanel.add(organizationField);
        formPanel.add(Box.createVerticalStrut(8));

        mainCardPanel.add(formPanel);
        mainCardPanel.add(Box.createVerticalStrut(15));

        // Remember me + Forgot password row
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        optionsPanel.setBackground(ModernTheme.BG_CARD);
        optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        rememberMeCheckBox = new JCheckBox("Remember me");
        rememberMeCheckBox.setFont(ModernTheme.FONT_REGULAR);
        rememberMeCheckBox.setForeground(ModernTheme.TEXT_SECONDARY);
        rememberMeCheckBox.setBackground(ModernTheme.BG_CARD);
        rememberMeCheckBox.setFocusPainted(false);
        optionsPanel.add(rememberMeCheckBox);

        optionsPanel.add(Box.createHorizontalStrut(80));

        forgotPasswordLabel = new JLabel("Forgot password?");
        forgotPasswordLabel.setFont(ModernTheme.FONT_REGULAR);
        forgotPasswordLabel.setForeground(ModernTheme.ACCENT_BLUE);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Window owner = SwingUtilities.getWindowAncestor(ModernLoginPanel.this);
                PasswordResetDialog dlg = new PasswordResetDialog(owner, authService, () -> {
                    // On successful reset: show toast and focus password field
                    Toast.success(ModernLoginPanel.this, "Password reset successfully! Please log in.");
                    SwingUtilities.invokeLater(() -> {
                        switchToLoginMode();
                        usernameField.setText("");
                        passwordField.setText("");
                        passwordField.requestFocusInWindow();
                    });
                });
                dlg.setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setForeground(ModernTheme.ACCENT_BLUE.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setForeground(ModernTheme.ACCENT_BLUE);
            }
        });
        optionsPanel.add(forgotPasswordLabel);

        mainCardPanel.add(optionsPanel);
        mainCardPanel.add(Box.createVerticalStrut(25));

        // Login button
        loginButton = ModernTheme.createPrimaryButton("Log In");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(400, ModernTheme.BUTTON_HEIGHT));
        loginButton.addActionListener(e -> {
            if (isRegistrationMode) {
                performRegistration();
            } else {
                performLogin();
            }
        });
        ModernTheme.addHoverEffect(loginButton, ModernTheme.ACCENT_ORANGE, ModernTheme.ACCENT_ORANGE_HOVER);
        mainCardPanel.add(loginButton);
        mainCardPanel.add(Box.createVerticalStrut(15));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(ModernTheme.FONT_REGULAR);
        statusLabel.setForeground(ModernTheme.ERROR_RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainCardPanel.add(statusLabel);
        mainCardPanel.add(Box.createVerticalStrut(15));

        // "Do you have an account? Register" link
        JLabel registerPromptLabel = new JLabel("Do you have an account? Register");
        registerPromptLabel.setFont(ModernTheme.FONT_REGULAR);
        registerPromptLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        registerPromptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainCardPanel.add(registerPromptLabel);

        // Add Enter key support
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (isRegistrationMode) {
                        performRegistration();
                    } else {
                        performLogin();
                    }
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);
        organizationField.addKeyListener(enterKeyListener);

        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.add(mainCardPanel, gbc);

        return mainPanel;
    }

    /**
     * Switch to login mode
     */
    private void switchToLoginMode() {
        isRegistrationMode = false;
        titleLabel.setText("Admin Log In");
        loginButton.setText("Log In");
        
        // Hide registration fields
        confirmPasswordLabel.setVisible(false);
        confirmPasswordField.setVisible(false);
        roleLabel.setVisible(false);
        roleComboBox.setVisible(false);
        organizationLabel.setVisible(false);
        organizationField.setVisible(false);
        
        // Show login-specific options
        rememberMeCheckBox.setVisible(true);
        forgotPasswordLabel.setVisible(true);
        
        // Update sidebar button styles
        sidebarLoginButton.setBackground(ModernTheme.ACCENT_BLUE_DARK);
        sidebarLoginButton.setForeground(ModernTheme.TEXT_PRIMARY);
        sidebarRegisterButton.setBackground(ModernTheme.BG_DARKER);
        sidebarRegisterButton.setForeground(ModernTheme.TEXT_SECONDARY);
        
        statusLabel.setText(" ");
        clearFields();
        revalidate();
        repaint();
    }

    /**
     * Switch to registration mode
     */
    private void switchToRegistrationMode() {
        isRegistrationMode = true;
        titleLabel.setText("Create Account");
        loginButton.setText("Register");
        
        // Show registration fields
        confirmPasswordLabel.setVisible(true);
        confirmPasswordField.setVisible(true);
        roleLabel.setVisible(true);
        roleComboBox.setVisible(true);
        organizationLabel.setVisible(true);
        organizationField.setVisible(true);
        
        // Hide login-specific options
        rememberMeCheckBox.setVisible(false);
        forgotPasswordLabel.setVisible(false);
        
        // Update sidebar button styles
        sidebarRegisterButton.setBackground(ModernTheme.ACCENT_BLUE_DARK);
        sidebarRegisterButton.setForeground(ModernTheme.TEXT_PRIMARY);
        sidebarLoginButton.setBackground(ModernTheme.BG_DARKER);
        sidebarLoginButton.setForeground(ModernTheme.TEXT_SECONDARY);
        
        statusLabel.setText("Enter your details to create a new account");
        statusLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        clearFields();
        revalidate();
        repaint();
    }

    /**
     * Perform login action
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Please enter a username");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter a password");
            return;
        }

        if (authService.login(username, password)) {
            showSuccess("Login successful!");
            clearFields();
            
            // Small delay for user to see success message
            Timer timer = new Timer(500, e -> onLoginSuccess.run());
            timer.setRepeats(false);
            timer.start();
        } else {
            showError(authService.getLastError());
        }
    }

    /**
     * Perform registration action
     */
    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();
        String organization = organizationField.getText().trim();

        if (username.isEmpty()) {
            showError("Please enter a username");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter a password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        String orgToSave = organization.isEmpty() ? null : organization;

        if (authService.register(username, password, selectedRole, orgToSave)) {
            showSuccess("Registration successful! You can now login.");
            Timer timer = new Timer(1500, e -> {
                switchToLoginMode();
                clearFields();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            showError(authService.getLastError());
        }
    }

    /**
     * Display error message
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(ModernTheme.ERROR_RED);
    }

    /**
     * Display success message
     */
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(ModernTheme.SUCCESS_GREEN);
    }

    /**
     * Clear all input fields
     */
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        organizationField.setText("");
        rememberMeCheckBox.setSelected(false);
    }
}
