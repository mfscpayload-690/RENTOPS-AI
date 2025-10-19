package ui.components;

import utils.ModernTheme;
import javax.swing.*;
import java.awt.*;

/**
 * StatusBadge - Modern badge component for displaying status/category tags.
 * 
 * Features:
 * - Rounded pill-shaped badges
 * - Predefined status colors (Active, Pending, Complete, Error, etc.)
 * - Customizable colors
 * - Subtle background with colored text
 * - Small, medium, large sizes
 * 
 * Usage:
 * <pre>
 * StatusBadge badge = StatusBadge.success("Active");
 * StatusBadge badge = StatusBadge.warning("Pending");
 * StatusBadge badge = StatusBadge.error("Failed");
 * StatusBadge badge = new StatusBadge("Custom", Color.BLUE, Size.MEDIUM);
 * </pre>
 * 
 * Inspired by: GitHub labels, Linear status badges, Stripe status pills
 */
public class StatusBadge extends JLabel {
    
    public enum Size {
        SMALL(10, 6, 11),
        MEDIUM(12, 8, 12),
        LARGE(14, 10, 14);
        
        final int fontSize;
        final int vPadding;
        final int hPadding;
        
        Size(int fontSize, int vPadding, int hPadding) {
            this.fontSize = fontSize;
            this.vPadding = vPadding;
            this.hPadding = hPadding;
        }
    }
    
    private Color badgeColor;
    private Color bgColor;
    
    /**
     * Create a status badge with text, color, and size.
     * 
     * @param text Badge text
     * @param color Badge color (text and border)
     * @param size Badge size
     */
    public StatusBadge(String text, Color color, Size size) {
        super(text);
        this.badgeColor = color;
        this.bgColor = new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            30  // 30 alpha for subtle background
        );
        
        setupBadge(size);
    }
    
    /**
     * Create a medium-sized badge.
     */
    public StatusBadge(String text, Color color) {
        this(text, color, Size.MEDIUM);
    }
    
    /**
     * Setup badge appearance.
     */
    private void setupBadge(Size size) {
        setFont(new Font("Segoe UI", Font.BOLD, size.fontSize));
        setForeground(badgeColor);
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorder(BorderFactory.createEmptyBorder(
            size.vPadding, size.hPadding, size.vPadding, size.hPadding
        ));
    }
    
    // ========== Factory methods for common statuses ==========
    
    /**
     * Create a success badge (green).
     */
    public static StatusBadge success(String text) {
        return new StatusBadge(text, ModernTheme.SUCCESS_GREEN);
    }
    
    /**
     * Create a warning badge (yellow/orange).
     */
    public static StatusBadge warning(String text) {
        return new StatusBadge(text, ModernTheme.WARNING_YELLOW);
    }
    
    /**
     * Create an error badge (red).
     */
    public static StatusBadge error(String text) {
        return new StatusBadge(text, ModernTheme.ERROR_RED);
    }
    
    /**
     * Create an info badge (blue).
     */
    public static StatusBadge info(String text) {
        return new StatusBadge(text, ModernTheme.ACCENT_BLUE);
    }
    
    /**
     * Create a primary badge (orange).
     */
    public static StatusBadge primary(String text) {
        return new StatusBadge(text, ModernTheme.ACCENT_ORANGE);
    }
    
    /**
     * Create a neutral badge (gray).
     */
    public static StatusBadge neutral(String text) {
        return new StatusBadge(text, ModernTheme.TEXT_SECONDARY);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded pill background
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        
        // Draw border
        g2.setColor(badgeColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
        
        g2.dispose();
        
        super.paintComponent(g);
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        // Make it pill-shaped by ensuring height is consistent
        size.height = Math.max(size.height, 28);
        return size;
    }
}
