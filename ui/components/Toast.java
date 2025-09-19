package ui.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Toast notification component that displays briefly and fades out
 */
public class Toast extends JPanel {

    public enum Type {
        INFO, SUCCESS, WARNING, ERROR
    }

    private String message;
    private Type type;
    private final Timer fadeTimer;
    private float opacity = 0.9f;
    private int displayTime = 3000; // milliseconds
    private Color backgroundColor;
    private Color textColor = Color.WHITE;
    private JWindow window;

    private static final Color INFO_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color WARNING_COLOR = new Color(243, 156, 18);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);

    public Toast(String message, Type type) {
        this.message = message;
        this.type = type;

        // Set colors based on type
        switch (type) {
            case INFO:
                backgroundColor = INFO_COLOR;
                break;
            case SUCCESS:
                backgroundColor = SUCCESS_COLOR;
                break;
            case WARNING:
                backgroundColor = WARNING_COLOR;
                break;
            case ERROR:
                backgroundColor = ERROR_COLOR;
                break;
            default:
                backgroundColor = INFO_COLOR;
        }

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        fadeTimer = new Timer(50, e -> {
            opacity -= 0.025f;
            if (opacity <= 0) {
                opacity = 0;
                window.dispose();
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
    }

    public void show(Component parent) {
        window = new JWindow();
        window.setBackground(new Color(0, 0, 0, 0));
        window.setAlwaysOnTop(true);
        window.setContentPane(this);
        window.pack();

        // Center on parent
        Point p = parent.getLocationOnScreen();
        Dimension parentSize = parent.getSize();
        Dimension windowSize = window.getSize();

        int x = p.x + (parentSize.width - windowSize.width) / 2;
        int y = p.y + parentSize.height - windowSize.height - 50;

        window.setLocation(x, y);
        window.setVisible(true);

        // Start fade timer after display time
        Timer displayTimer = new Timer(displayTime, e -> {
            fadeTimer.start();
            ((Timer) e.getSource()).stop();
        });
        displayTimer.setRepeats(false);
        displayTimer.start();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int width = fm.stringWidth(message) + 80;
        return new Dimension(Math.max(width, 200), 60);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set composite for transparency
        g2.setComposite(AlphaComposite.SrcOver.derive(opacity));

        // Draw rounded background with shadow
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fill(new RoundRectangle2D.Double(3, 3, getWidth() - 6, getHeight() - 6, 20, 20));

        g2.setColor(backgroundColor);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() - 3, 20, 20));

        // Draw icon based on type
        int iconSize = 24;
        g2.setColor(textColor);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));

        int iconX = 15;
        int iconY = (getHeight() - iconSize) / 2;

        // Draw text
        g2.setColor(textColor);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        FontMetrics fm = g2.getFontMetrics();
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(message, iconX + iconSize + 10, textY);

        g2.dispose();
    }

    /**
     * Show a toast notification
     *
     * @param parent Parent component
     * @param message Message to display
     * @param type Type of notification
     */
    public static void makeText(Component parent, String message, Type type) {
        new Toast(message, type).show(parent);
    }

    /**
     * Show an info toast notification
     */
    public static void info(Component parent, String message) {
        makeText(parent, message, Type.INFO);
    }

    /**
     * Show a success toast notification
     */
    public static void success(Component parent, String message) {
        makeText(parent, message, Type.SUCCESS);
    }

    /**
     * Show a warning toast notification
     */
    public static void warning(Component parent, String message) {
        makeText(parent, message, Type.WARNING);
    }

    /**
     * Show an error toast notification
     */
    public static void error(Component parent, String message) {
        makeText(parent, message, Type.ERROR);
    }
}
