package ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * PieChartPanel - simple pie chart for booking status distribution.
 */
public class PieChartPanel extends JPanel {
    private Map<String, Integer> data;
    private Color[] colors = {
            new Color(255, 193, 7),    // Pending - yellow
            new Color(64, 150, 255),   // Approved - blue
            new Color(255, 119, 0),    // Active - orange
            new Color(76, 175, 80),    // Completed - green
            new Color(244, 67, 54)     // Cancelled - red
    };
    private String[] labels = {"Pending", "Approved", "Active", "Completed", "Cancelled"};

    public PieChartPanel() {
        setPreferredSize(new Dimension(260, 260));
        setOpaque(false);
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString("No data", getWidth() / 2 - 20, getHeight() / 2);
            return;
        }
        int total = data.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawString("No bookings", getWidth() / 2 - 30, getHeight() / 2);
            return;
        }
        int w = Math.min(getWidth(), getHeight()) - 40;
        int x = (getWidth() - w) / 2;
        int y = (getHeight() - w) / 2;
        int startAngle = 0;
        for (int i = 0; i < labels.length; i++) {
            int value = data.getOrDefault(labels[i], 0);
            int angle = (int) Math.round(360.0 * value / total);
            g.setColor(colors[i]);
            ((Graphics2D) g).fillArc(x, y, w, w, startAngle, angle);
            startAngle += angle;
        }
        // Draw legend
        int legendX = x + w + 20;
        int legendY = y;
        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        for (int i = 0; i < labels.length; i++) {
            g.setColor(colors[i]);
            g.fillRect(legendX, legendY + i * 22, 16, 16);
            g.setColor(Color.WHITE);
            g.drawRect(legendX, legendY + i * 22, 16, 16);
            g.setColor(Color.LIGHT_GRAY);
            g.drawString(labels[i], legendX + 24, legendY + i * 22 + 13);
        }
    }
}
