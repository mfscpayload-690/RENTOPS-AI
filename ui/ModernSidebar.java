package ui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Modern sidebar navigation panel with hover effects and proper styling
 */
public class ModernSidebar extends JPanel {

    // Color scheme
    private static final Color BACKGROUND = new Color(24, 32, 44);
    private static final Color ACCENT_COLOR = new Color(47, 128, 237);
    private static final Color HOVER_COLOR = new Color(42, 58, 80);
    private static final Color SELECTED_COLOR = new Color(47, 128, 237, 120);
    private static final Color TEXT_COLOR = new Color(255, 255, 255);
    private static final Color SECONDARY_TEXT_COLOR = new Color(220, 220, 220);

    // UI Properties
    private static final int BUTTON_HEIGHT = 46;
    private static final int BUTTON_RADIUS = 12;
    private static final int SPACING = 6;
    private static final int SIDE_PADDING = 16;
    private static final int ICON_TEXT_GAP = 12;

    private final CardLayout contentCardLayout;
    private final JPanel contentPanel;
    private String activeCard = "overview"; // Default active card

    /**
     * Create a new modern sidebar
     *
     * @param cardLayout The card layout for the main content
     * @param contentPanel The panel containing the cards
     * @param title The title of the sidebar
     * @param menuItems The menu items to display
     * @param cardNames The card names corresponding to menu items
     * @param menuIcons The icons for each menu item
     */
    public ModernSidebar(CardLayout cardLayout, JPanel contentPanel, String title,
            String[] menuItems, String[] cardNames, String[] menuIcons) {
        this.contentCardLayout = cardLayout;
        this.contentPanel = contentPanel;

        setBackground(BACKGROUND);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, SIDE_PADDING, 20, SIDE_PADDING));
        setPreferredSize(new Dimension(240, 0));

        // Add top title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.NORTH);

        // Create menu panel (center)
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        // Add menu items
        for (int i = 0; i < menuItems.length; i++) {
            final String cardName = cardNames[i];

            NavButton btn = new NavButton(menuIcons[i], menuItems[i], cardName.equals(activeCard));
            btn.addActionListener(_e -> {
                contentCardLayout.show(contentPanel, cardName);
                setActiveButton(menuPanel, cardName);
                activeCard = cardName;
            });

            menuPanel.add(btn);
            menuPanel.add(Box.createVerticalStrut(SPACING));
        }

        // Add a glue to push the footer to the bottom
        menuPanel.add(Box.createVerticalGlue());

        // Create logout button with special styling
        NavButton logoutBtn = new NavButton("🚪", "Logout", false);
        logoutBtn.setBackground(new Color(235, 87, 87, 120));
        logoutBtn.addActionListener(_e -> showLogoutConfirmation());

        menuPanel.add(logoutBtn);

        add(menuPanel, BorderLayout.CENTER);
    }

    private void showLogoutConfirmation() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            // Here you would handle the actual logout process

            // For the sample, we'll just close the window
            if (parentWindow != null) {
                parentWindow.dispose();
            }
        }
    }

    private void setActiveButton(JPanel menuPanel, String cardName) {
        for (Component c : menuPanel.getComponents()) {
            if (c instanceof NavButton btn) {
                btn.setActive(btn.getCardName().equals(cardName));
            }
        }
    }

    /**
     * Custom navigation button with modern styling
     */
    private class NavButton extends JButton {

        private boolean isActive = false;
        private boolean isHover = false;
        private final String icon;
        private final String text;
        private final String cardName;

        public NavButton(String icon, String text, boolean active) {
            this.icon = icon;
            this.text = text;
            this.isActive = active;
            this.cardName = text.toLowerCase().replace(" ", "");

            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            setHorizontalAlignment(LEFT);
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setMaximumSize(new Dimension(Short.MAX_VALUE, BUTTON_HEIGHT));
            setPreferredSize(new Dimension(getPreferredSize().width, BUTTON_HEIGHT));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHover = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHover = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            isActive = active;
            repaint();
        }

        public String getCardName() {
            return cardName;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            if (isActive) {
                g2.setColor(SELECTED_COLOR);
            } else if (isHover) {
                g2.setColor(HOVER_COLOR);
            } else if (getBackground() != null) {
                g2.setColor(getBackground());
            }

            if (isActive || isHover || getBackground() != null) {
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), BUTTON_RADIUS, BUTTON_RADIUS));
            }

            // Left accent bar if active
            if (isActive) {
                g2.setColor(ACCENT_COLOR);
                g2.fillRoundRect(0, 6, 4, getHeight() - 12, 2, 2);
            }

            // Icon
            g2.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
            FontMetrics iconFm = g2.getFontMetrics();

            // Text
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            FontMetrics textFm = g2.getFontMetrics();

            int iconWidth = iconFm.stringWidth(icon);

            int iconX = SIDE_PADDING;
            int iconY = (getHeight() - iconFm.getHeight()) / 2 + iconFm.getAscent();

            int textX = iconX + iconWidth + ICON_TEXT_GAP;
            int textY = (getHeight() - textFm.getHeight()) / 2 + textFm.getAscent();

            // Draw text with shadow effect
            if (isActive) {
                g2.setColor(TEXT_COLOR);
            } else {
                g2.setColor(SECONDARY_TEXT_COLOR);
            }

            g2.drawString(icon, iconX, iconY);
            g2.drawString(text, textX, textY);

            g2.dispose();
        }
    }
}
