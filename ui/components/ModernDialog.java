package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern dialog for displaying messages and getting user confirmation
 */
public class ModernDialog extends JDialog {

    public enum Type {
        INFO, SUCCESS, WARNING, ERROR, QUESTION
    }

    private ModernButton primaryButton;
    private ModernButton secondaryButton;
    private JLabel messageLabel;
    private JLabel titleLabel;
    private JPanel contentPanel;
    private boolean result = false;

    public ModernDialog(Frame owner, String title, String message, Type type) {
        super(owner, true);
        initializeDialog(title, message, type);
    }

    public ModernDialog(Dialog owner, String title, String message, Type type) {
        super(owner, true);
        initializeDialog(title, message, type);
    }

    private void initializeDialog(String title, String message, Type type) {
        setUndecorated(true);
        setResizable(false);

        // Set dialog background color
        setBackground(new Color(0, 0, 0, 0));

        // Create main panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw shadow
                for (int i = 5; i > 0; i--) {
                    float alpha = 0.1f - (i * 0.01f);
                    g2.setColor(new Color(0, 0, 0, alpha > 0 ? (int) (alpha * 255) : 1));
                    g2.fill(new RoundRectangle2D.Double(i, i, getWidth() - (i * 2), getHeight() - (i * 2), 15, 15));
                }

                // Draw background
                g2.setColor(new Color(245, 247, 250));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));

                g2.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create header panel
        JPanel headerPanel = createHeaderPanel(title, type);

        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        messageLabel = new JLabel("<html><body width='300px'>" + message + "</body></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = createButtonPanel(type);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to dialog
        getContentPane().add(mainPanel);

        // Set size
        pack();

        // Center on screen
        setLocationRelativeTo(getOwner());
    }

    private JPanel createHeaderPanel(String title, Type type) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // Title label
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Color based on type
        Color headerColor;
        switch (type) {
            case INFO:
                headerColor = new Color(41, 128, 185);
                break;
            case SUCCESS:
                headerColor = new Color(39, 174, 96);
                break;
            case WARNING:
                headerColor = new Color(243, 156, 18);
                break;
            case ERROR:
                headerColor = new Color(231, 76, 60);
                break;
            case QUESTION:
                headerColor = new Color(52, 152, 219);
                break;
            default:
                headerColor = new Color(52, 73, 94);
        }

        titleLabel.setForeground(headerColor);

        // Add components to header
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add close button
        JLabel closeButton = new JLabel("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 24));
        closeButton.setForeground(new Color(150, 150, 150));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setForeground(new Color(231, 76, 60));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setForeground(new Color(150, 150, 150));
            }
        });

        headerPanel.add(closeButton, BorderLayout.EAST);

        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(230, 230, 230));
        headerPanel.add(separator, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JPanel createButtonPanel(Type type) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create buttons based on dialog type
        if (type == Type.QUESTION) {
            secondaryButton = new ModernButton("Cancel");
            secondaryButton.setDefaultColor(new Color(190, 190, 190));
            secondaryButton.setHoverColor(new Color(160, 160, 160));
            secondaryButton.setPressedColor(new Color(140, 140, 140));

            primaryButton = new ModernButton("OK");

            secondaryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    result = false;
                    dispose();
                }
            });

            buttonPanel.add(secondaryButton);

        } else {
            primaryButton = new ModernButton("OK");

            // Set color based on type
            switch (type) {
                case INFO:
                    primaryButton.setDefaultColor(new Color(41, 128, 185));
                    break;
                case SUCCESS:
                    primaryButton.setDefaultColor(new Color(39, 174, 96));
                    break;
                case WARNING:
                    primaryButton.setDefaultColor(new Color(243, 156, 18));
                    break;
                case ERROR:
                    primaryButton.setDefaultColor(new Color(231, 76, 60));
                    break;
                default:
                    primaryButton.setDefaultColor(new Color(52, 152, 219));
            }
        }

        primaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = true;
                dispose();
            }
        });

        buttonPanel.add(primaryButton);

        return buttonPanel;
    }

    /**
     * Get user's response (for question dialogs)
     *
     * @return true if primary button was clicked, false otherwise
     */
    public boolean getResult() {
        return result;
    }

    /**
     * Set custom button text
     */
    public void setButtonText(String primary, String secondary) {
        if (primaryButton != null) {
            primaryButton.setText(primary);
        }
        if (secondaryButton != null) {
            secondaryButton.setText(secondary);
        }
    }

    /**
     * Show an information dialog
     */
    public static void showInfo(Component parent, String title, String message) {
        Frame frame = JOptionPane.getFrameForComponent(parent);
        ModernDialog dialog = new ModernDialog(frame, title, message, Type.INFO);
        dialog.setVisible(true);
    }

    /**
     * Show a success dialog
     */
    public static void showSuccess(Component parent, String title, String message) {
        Frame frame = JOptionPane.getFrameForComponent(parent);
        ModernDialog dialog = new ModernDialog(frame, title, message, Type.SUCCESS);
        dialog.setVisible(true);
    }

    /**
     * Show a warning dialog
     */
    public static void showWarning(Component parent, String title, String message) {
        Frame frame = JOptionPane.getFrameForComponent(parent);
        ModernDialog dialog = new ModernDialog(frame, title, message, Type.WARNING);
        dialog.setVisible(true);
    }

    /**
     * Show an error dialog
     */
    public static void showError(Component parent, String title, String message) {
        Frame frame = JOptionPane.getFrameForComponent(parent);
        ModernDialog dialog = new ModernDialog(frame, title, message, Type.ERROR);
        dialog.setVisible(true);
    }

    /**
     * Show a confirmation dialog
     *
     * @return true if confirmed, false otherwise
     */
    public static boolean showConfirm(Component parent, String title, String message) {
        Frame frame = JOptionPane.getFrameForComponent(parent);
        ModernDialog dialog = new ModernDialog(frame, title, message, Type.QUESTION);
        dialog.setVisible(true);
        return dialog.getResult();
    }
}
