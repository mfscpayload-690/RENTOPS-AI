package utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import javax.swing.*;
import java.awt.*;

/**
 * ModernTheme - Centralized theme management for RENTOPS-AI
 * 
 * This class provides:
 * - FlatLaf dark theme initialization
 * - Consistent color palette matching the reference UI
 * - Font configurations
 * - Helper methods for theming components
 * 
 * Usage:
 * Call ModernTheme.initialize() at application startup (in Main.java)
 * before creating any UI components.
 */
public class ModernTheme {
    
    // ========== COLOR PALETTE (matching reference UI) ==========
    
    // Background colors
    public static final Color BG_DARK = new Color(30, 30, 30);           // Main dark background
    public static final Color BG_DARKER = new Color(20, 20, 20);         // Sidebar/panel background
    public static final Color BG_CARD = new Color(45, 45, 45);           // Card/dialog background
    public static final Color BG_INPUT = new Color(55, 55, 55);          // Input field background
    
    // Accent colors
    public static final Color ACCENT_ORANGE = new Color(255, 119, 0);    // Primary action (buttons)
    public static final Color ACCENT_ORANGE_HOVER = new Color(255, 140, 40); // Button hover
    public static final Color ACCENT_BLUE = new Color(64, 150, 255);     // Secondary accent
    public static final Color ACCENT_BLUE_DARK = new Color(45, 85, 135); // Sidebar selected
    
    // Text colors
    public static final Color TEXT_PRIMARY = new Color(240, 240, 240);   // Main text
    public static final Color TEXT_SECONDARY = new Color(180, 180, 180); // Labels, hints
    public static final Color TEXT_DISABLED = new Color(120, 120, 120);  // Disabled text
    
    // Border and divider colors
    public static final Color BORDER_COLOR = new Color(70, 70, 70);
    public static final Color DIVIDER_COLOR = new Color(60, 60, 60);
    
    // Status colors
    public static final Color SUCCESS_GREEN = new Color(76, 175, 80);
    public static final Color ERROR_RED = new Color(244, 67, 54);
    public static final Color WARNING_YELLOW = new Color(255, 193, 7);
    
    // ========== FONTS ==========
    
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_LARGE = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 15);
    
    // Aliases for backward compatibility
    public static final Font PRIMARY_FONT = FONT_REGULAR;
    public static final Font MEDIUM_FONT = FONT_REGULAR;
    public static final Font MEDIUM_BOLD_FONT = FONT_BOLD;
    public static final Color CARD_BG = BG_CARD;
    public static final Color BG_PRIMARY = BG_DARK;
    public static final Color SURFACE_DARK = BG_DARKER;
    
    // ========== DIMENSIONS ==========
    
    public static final int BORDER_RADIUS = 8;
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT = 36;
    public static final int SIDEBAR_WIDTH = 220;
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;
    
    /**
     * Initialize the modern theme for the entire application.
     * MUST be called before creating any Swing components.
     * 
     * @return true if theme was successfully applied
     */
    public static boolean initialize() {
        try {
            // Enable animated theme changes
            FlatAnimatedLafChange.showSnapshot();
            
            // Set FlatLaf Dark theme
            FlatDarkLaf.setup();
            
            // Customize FlatLaf properties for a more modern look
            UIManager.put("Button.arc", BORDER_RADIUS);
            UIManager.put("Component.arc", BORDER_RADIUS);
            UIManager.put("ProgressBar.arc", BORDER_RADIUS);
            UIManager.put("TextComponent.arc", BORDER_RADIUS);
            UIManager.put("CheckBox.arc", 4);
            
            // Set custom colors
            UIManager.put("Panel.background", BG_DARK);
            UIManager.put("Button.background", ACCENT_ORANGE);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", ACCENT_ORANGE_HOVER);
            UIManager.put("TextField.background", BG_INPUT);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("Label.foreground", TEXT_SECONDARY);
            UIManager.put("TabbedPane.selectedBackground", ACCENT_BLUE_DARK);
            
            // Font settings
            setUIFont(FONT_REGULAR);
            
            // Update all existing components
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
            
            System.out.println("✓ Modern theme initialized successfully");
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Failed to initialize modern theme: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
    
    /**
     * Set the default font for all UI components.
     * 
     * @param font The font to apply globally
     */
    public static void setUIFont(Font font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }
    
    /**
     * Create a styled primary button (orange accent).
     * 
     * @param text Button text
     * @return Configured JButton
     */
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(ACCENT_ORANGE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, BUTTON_HEIGHT));
        return button;
    }
    
    /**
     * Create a styled secondary button (blue/gray accent).
     * 
     * @param text Button text
     * @return Configured JButton
     */
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setBackground(ACCENT_BLUE_DARK);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, BUTTON_HEIGHT));
        return button;
    }
    
    /**
     * Create a modern styled text field.
     * 
     * @param placeholder Placeholder text
     * @return Configured JTextField
     */
    public static JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_REGULAR);
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(300, INPUT_HEIGHT));
        
        // Placeholder support (Java 25+)
        try {
            field.putClientProperty("JTextField.placeholderText", placeholder);
        } catch (Exception ignored) {
            // Fallback for older Java versions
        }
        
        return field;
    }
    
    /**
     * Create a modern styled password field.
     * 
     * @param placeholder Placeholder text
     * @return Configured JPasswordField
     */
    public static JPasswordField createModernPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_REGULAR);
        field.setBackground(BG_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(300, INPUT_HEIGHT));
        
        // Placeholder support
        try {
            field.putClientProperty("JPasswordField.placeholderText", placeholder);
        } catch (Exception ignored) {
        }
        
        return field;
    }
    
    /**
     * Create a styled label.
     * 
     * @param text Label text
     * @param size Font size (FONT_REGULAR, FONT_TITLE, etc.)
     * @return Configured JLabel
     */
    public static JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }
    
    /**
     * Create a card-style panel with rounded borders.
     * 
     * @return Configured JPanel
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)
        ));
        return panel;
    }
    
    /**
     * Apply hover effect to a button.
     * 
     * @param button The button to enhance
     * @param normalColor Normal background color
     * @param hoverColor Hover background color
     */
    public static void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });
    }
    
    /**
     * Create a modern search bar with icon.
     * 
     * @param placeholder Search placeholder text
     * @return Configured search field panel
     */
    public static JPanel createSearchBar(String placeholder) {
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(BG_INPUT);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchPanel.setMaximumSize(new Dimension(400, INPUT_HEIGHT));
        
        // Search icon
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchIcon.setForeground(TEXT_SECONDARY);
        
        // Text field
        JTextField searchField = new JTextField();
        searchField.setFont(FONT_REGULAR);
        searchField.setBackground(BG_INPUT);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(TEXT_PRIMARY);
        searchField.setBorder(null);
        
        try {
            searchField.putClientProperty("JTextField.placeholderText", placeholder);
        } catch (Exception ignored) {
        }
        
        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        // Store reference to field for external access
        searchPanel.putClientProperty("searchField", searchField);
        
        return searchPanel;
    }
    
    /**
     * Get the search field from a search bar panel.
     */
    public static JTextField getSearchField(JPanel searchBar) {
        return (JTextField) searchBar.getClientProperty("searchField");
    }
    
    /**
     * Create a modern header panel with title and optional subtitle.
     * 
     * @param title Main title
     * @param subtitle Optional subtitle (null if none)
     * @return Configured header panel
     */
    public static JPanel createHeaderPanel(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(PADDING_LARGE, 0, PADDING_MEDIUM, 0));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        
        if (subtitle != null && !subtitle.isEmpty()) {
            panel.add(Box.createVerticalStrut(4));
            JLabel subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(FONT_REGULAR);
            subtitleLabel.setForeground(TEXT_SECONDARY);
            subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(subtitleLabel);
        }
        
        return panel;
    }
    
    /**
     * Create a toolbar panel for action buttons.
     * 
     * @return Configured toolbar panel
     */
    public static JPanel createToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(BG_DARK);
        toolbar.setBorder(BorderFactory.createEmptyBorder(PADDING_SMALL, 0, PADDING_MEDIUM, 0));
        return toolbar;
    }
}
