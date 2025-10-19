package ui.components;

import utils.ModernTheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ActionButton - Modern styled button for primary, secondary, and danger actions.
 * 
 * Button Types:
 * - PRIMARY: Orange accent (main actions like Save, Submit)
 * - SECONDARY: Blue accent (alternative actions like Edit, View)
 * - SUCCESS: Green (approve, confirm actions)
 * - DANGER: Red (delete, cancel actions)
 * - GHOST: Transparent with border (tertiary actions)
 * 
 * Features:
 * - Smooth hover/press animations
 * - Loading state with spinner
 * - Icon support
 * - Rounded corners
 * - Focus states
 * 
 * Usage:
 * <pre>
 * ActionButton saveBtn = new ActionButton("Save", ActionButton.Type.PRIMARY);
 * saveBtn.addActionListener(e -> save());
 * 
 * ActionButton deleteBtn = new ActionButton("Delete", ActionButton.Type.DANGER, "🗑");
 * deleteBtn.setLoading(true); // Show spinner
 * </pre>
 * 
 * Inspired by: Stripe buttons, GitHub buttons, Material Design
 */
public class ActionButton extends JButton {
    
    public enum Type {
        PRIMARY,    // Orange
        SECONDARY,  // Blue
        SUCCESS,    // Green
        DANGER,     // Red
        GHOST       // Transparent
    }
    
    private Type buttonType;
    private Color normalColor;
    private Color hoverColor;
    private Color pressColor;
    private boolean isLoading = false;
    private JLabel iconLabel;
    private String originalText;
    
    /**
     * Create an action button with text and type.
     * 
     * @param text Button text
     * @param type Button type (PRIMARY, SECONDARY, etc.)
     */
    public ActionButton(String text, Type type) {
        this(text, type, null);
    }
    
    /**
     * Create an action button with text, type, and icon.
     * 
     * @param text Button text
     * @param type Button type
     * @param icon Icon character (emoji or symbol)
     */
    public ActionButton(String text, Type type, String icon) {
        super(text);
        this.buttonType = type;
        this.originalText = text;
        
        // Set colors based on type
        switch (type) {
            case PRIMARY:
                normalColor = ModernTheme.ACCENT_ORANGE;
                hoverColor = ModernTheme.ACCENT_ORANGE_HOVER;
                pressColor = ModernTheme.ACCENT_ORANGE.darker();
                break;
            case SECONDARY:
                normalColor = ModernTheme.ACCENT_BLUE;
                hoverColor = ModernTheme.ACCENT_BLUE.brighter();
                pressColor = ModernTheme.ACCENT_BLUE.darker();
                break;
            case SUCCESS:
                normalColor = ModernTheme.SUCCESS_GREEN;
                hoverColor = ModernTheme.SUCCESS_GREEN.brighter();
                pressColor = ModernTheme.SUCCESS_GREEN.darker();
                break;
            case DANGER:
                normalColor = ModernTheme.ERROR_RED;
                hoverColor = ModernTheme.ERROR_RED.brighter();
                pressColor = ModernTheme.ERROR_RED.darker();
                break;
            case GHOST:
                normalColor = ModernTheme.BG_CARD;
                hoverColor = new Color(60, 60, 60);
                pressColor = new Color(70, 70, 70);
                break;
        }
        
        setupButton();
        
        // Add icon if provided
        if (icon != null && !icon.isEmpty()) {
            setLayout(new BorderLayout(8, 0));
            iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            iconLabel.setForeground(Color.WHITE);
            
            JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
            contentPanel.setOpaque(false);
            contentPanel.add(iconLabel);
            contentPanel.add(new JLabel(text));
            
            setText("");
            add(contentPanel, BorderLayout.CENTER);
        }
    }
    
    /**
     * Setup button appearance and behavior.
     */
    private void setupButton() {
        setFont(ModernTheme.FONT_BUTTON);
        setForeground(buttonType == Type.GHOST ? ModernTheme.TEXT_PRIMARY : Color.WHITE);
        setBackground(normalColor);
        setFocusPainted(false);
        setContentAreaFilled(buttonType != Type.GHOST);
        setBorderPainted(buttonType == Type.GHOST);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, ModernTheme.BUTTON_HEIGHT));
        
        if (buttonType == Type.GHOST) {
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
        } else {
            setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        }
        
        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled() && !isLoading) {
                    setBackground(hoverColor);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (isEnabled() && !isLoading) {
                    setBackground(normalColor);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled() && !isLoading) {
                    setBackground(pressColor);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isEnabled() && !isLoading) {
                    setBackground(hoverColor);
                }
            }
        });
    }
    
    /**
     * Set loading state (shows spinner, disables button).
     * 
     * @param loading True to show loading state
     */
    public void setLoading(boolean loading) {
        this.isLoading = loading;
        setEnabled(!loading);
        
        if (loading) {
            setText("⟳ " + originalText); // Loading spinner
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
        } else {
            setText(originalText);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }
    
    /**
     * Check if button is in loading state.
     */
    public boolean isLoading() {
        return isLoading;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (buttonType != Type.GHOST) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw rounded rectangle background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 
                             ModernTheme.BORDER_RADIUS, ModernTheme.BORDER_RADIUS);
            
            // Draw subtle inner shadow when pressed
            if (getModel().isPressed()) {
                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 
                                 ModernTheme.BORDER_RADIUS, ModernTheme.BORDER_RADIUS);
            }
            
            g2.dispose();
        }
        
        super.paintComponent(g);
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        if (buttonType == Type.GHOST) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(ModernTheme.BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 
                             ModernTheme.BORDER_RADIUS, ModernTheme.BORDER_RADIUS);
            
            g2.dispose();
        }
    }
}
