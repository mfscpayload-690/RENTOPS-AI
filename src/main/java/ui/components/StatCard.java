package ui.components;

import utils.ModernTheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * StatCard - Modern statistics card component for dashboard metrics.
 * 
 * Features:
 * - Floating card design with subtle shadow
 * - Large value display with icon
 * - Trend indicator (up/down percentage)
 * - Customizable accent colors
 * - Smooth hover animations
 * 
 * Usage:
 * <pre>
 * StatCard card = new StatCard("Total Users", "1,234", "+12.5%", ModernTheme.ACCENT_BLUE);
 * </pre>
 * 
 * Inspired by: Stripe Dashboard, Linear, Notion analytics cards
 */
public class StatCard extends JPanel {
    
    private JLabel titleLabel;
    private JLabel valueLabel;
    private JLabel trendLabel;
    private JLabel iconLabel;
    private Color accentColor;
    private boolean isLoading = false;
    
    /**
     * Create a stat card with title, value, and trend.
     * 
     * @param title Card title (e.g., "Total Revenue")
     * @param value Main value (e.g., "₹45,234")
     * @param trend Trend indicator (e.g., "+12.5%" or "-3.2%")
     * @param accentColor Color for value and icon
     */
    public StatCard(String title, String value, String trend, Color accentColor) {
        this.accentColor = accentColor;
        
        setLayout(new BorderLayout(12, 12));
        setBackground(ModernTheme.BG_CARD);
        setBorder(createModernBorder());
        setPreferredSize(new Dimension(240, 140));
        setMaximumSize(new Dimension(300, 160));
        
        // Header panel (title + icon)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        titleLabel = new JLabel(title);
        titleLabel.setFont(ModernTheme.FONT_REGULAR);
        titleLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Simple icon placeholder (colored circle)
        iconLabel = new JLabel("●");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        iconLabel.setForeground(accentColor);
        headerPanel.add(iconLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center panel (value)
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentColor);
        add(valueLabel, BorderLayout.CENTER);
        
        // Footer panel (trend)
        if (trend != null && !trend.trim().isEmpty()) {
            trendLabel = new JLabel(trend);
            trendLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Color based on trend direction
            if (trend.startsWith("+")) {
                trendLabel.setForeground(ModernTheme.SUCCESS_GREEN);
                trendLabel.setText("▲ " + trend);
            } else if (trend.startsWith("-")) {
                trendLabel.setForeground(ModernTheme.ERROR_RED);
                trendLabel.setText("▼ " + trend);
            } else {
                trendLabel.setForeground(ModernTheme.TEXT_SECONDARY);
            }
            
            add(trendLabel, BorderLayout.SOUTH);
        }
        
        // Hover effect
        addHoverAnimation();
    }
    
    /**
     * Simplified constructor without trend.
     */
    public StatCard(String title, String value, Color accentColor) {
        this(title, value, null, accentColor);
    }
    
    /**
     * Update the displayed value and trend.
     */
    public void updateValue(String newValue, String newTrend) {
        valueLabel.setText(newValue);
        if (trendLabel != null && newTrend != null) {
            trendLabel.setText(newTrend);
            
            // Update trend color
            if (newTrend.startsWith("+")) {
                trendLabel.setForeground(ModernTheme.SUCCESS_GREEN);
                trendLabel.setText("▲ " + newTrend);
            } else if (newTrend.startsWith("-")) {
                trendLabel.setForeground(ModernTheme.ERROR_RED);
                trendLabel.setText("▼ " + newTrend);
            }
        }
        isLoading = false;
        repaint();
    }
    
    /**
     * Show loading state.
     */
    public void setLoading(boolean loading) {
        this.isLoading = loading;
        if (loading) {
            valueLabel.setText("...");
        }
        repaint();
    }
    
    /**
     * Create a modern card border with subtle shadow effect.
     */
    private EmptyBorder createModernBorder() {
        return new EmptyBorder(ModernTheme.PADDING_MEDIUM, 
                               ModernTheme.PADDING_MEDIUM, 
                               ModernTheme.PADDING_MEDIUM, 
                               ModernTheme.PADDING_MEDIUM);
    }
    
    /**
     * Add subtle hover animation (lift effect).
     */
    private void addHoverAnimation() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor.brighter(), 2),
                    createModernBorder()
                ));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBorder(createModernBorder());
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Enable anti-aliasing for smooth rounded corners
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rounded rectangle background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 
                         ModernTheme.BORDER_RADIUS, ModernTheme.BORDER_RADIUS);
        
        // Draw subtle border
        g2.setColor(ModernTheme.BORDER_COLOR);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 
                         ModernTheme.BORDER_RADIUS, ModernTheme.BORDER_RADIUS);
        
        g2.dispose();
    }
}
