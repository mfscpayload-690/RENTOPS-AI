package ui.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Modern card-style panel with shadow effects and rounded corners
 */
public class CardPanel extends JPanel {

    private Color backgroundColor = Color.WHITE;
    private int cornerRadius = 15;
    private int shadowSize = 5;
    private int shadowOpacity = 20; // 0-255

    public CardPanel() {
        this(new FlowLayout());
    }

    public CardPanel(LayoutManager layout) {
        super(layout);
        setup();
    }

    private void setup() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(shadowSize, shadowSize, shadowSize * 2, shadowSize * 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint shadow first
        for (int i = shadowSize; i >= 0; i--) {
            int opacity = shadowOpacity * i / shadowSize;
            g2.setColor(new Color(0, 0, 0, opacity));
            g2.fill(new RoundRectangle2D.Float(
                    shadowSize - i,
                    shadowSize - i / 2,
                    getWidth() - (shadowSize * 2) + (i * 2),
                    getHeight() - (shadowSize * 2) + (i * 2),
                    cornerRadius + i,
                    cornerRadius + i));
        }

        // Paint main background
        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Float(
                shadowSize,
                shadowSize,
                getWidth() - (shadowSize * 2),
                getHeight() - (shadowSize * 2),
                cornerRadius,
                cornerRadius));

        g2.dispose();

        super.paintComponent(g);
    }

    // Customization methods
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setShadowSize(int size) {
        this.shadowSize = size;
        setBorder(BorderFactory.createEmptyBorder(size, size, size * 2, size * 2));
        repaint();
    }

    public void setShadowOpacity(int opacity) {
        if (opacity >= 0 && opacity <= 255) {
            this.shadowOpacity = opacity;
            repaint();
        }
    }
}
