package ui;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {
    private final String[] navNames = {"Dashboard", "Cars", "Rentals", "Payments", "Admin"};
    private final String[] navIcons = {"🏠", "🚗", "📝", "💳", "👤"};
    public SidebarPanel(MainContentPanel mainContent) {
        setLayout(new GridBagLayout());
        setBackground(new Color(24, 32, 44));
        setPreferredSize(new Dimension(200, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 12, 8, 12);

        for (int i = 0; i < navNames.length; i++) {
            ModernSidebarButton btn = new ModernSidebarButton(navIcons[i] + "  " + navNames[i]);
            final String panelName = navNames[i];
            btn.addActionListener(evt -> mainContent.showPanel(panelName));
            gbc.gridy = i;
            add(btn, gbc);
        }

        // Separator
        gbc.gridy = navNames.length;
        gbc.insets = new Insets(20, 12, 0, 12);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 70, 90));
        add(sep, gbc);

        // Spacer
        gbc.gridy = navNames.length + 1;
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }

    // Modern button with rounded corners, hover animation, and shadow
    private static class ModernSidebarButton extends JButton {
        private Color normalBg = new Color(36, 46, 60);
        private Color hoverBg = new Color(52, 73, 94);
        private Color pressedBg = new Color(30, 40, 54);
        public ModernSidebarButton(String text) {
            super(text);
            setFocusPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setBackground(normalBg);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(hoverBg);
                    repaint();
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(normalBg);
                    repaint();
                }
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    setBackground(pressedBg);
                    repaint();
                }
                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    setBackground(hoverBg);
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            // Shadow
            g2.setColor(new Color(0,0,0,30));
            g2.fillRoundRect(4, getHeight()-8, getWidth()-8, 8, 12, 12);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
