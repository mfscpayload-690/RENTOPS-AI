package ui.components;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Modern button with smooth animations, rounded corners, and hover effects
 */
public class ModernButton extends JButton {

    private Color defaultColor = new Color(41, 128, 185);
    private Color hoverColor = new Color(52, 152, 219);
    private Color pressedColor = new Color(26, 86, 126);
    private Color currentColor;
    private int cornerRadius = 10;
    private boolean isHover = false;
    private boolean isPressed = false;
    private Timer transitionTimer;
    private float transitionStep = 0.1f;
    private boolean isTransitionRunning = false;
    private Color targetColor;

    public ModernButton() {
        this("");
    }

    public ModernButton(String text) {
        super(text);
        setup();
    }

    public ModernButton(Action a) {
        super(a);
        setup();
    }

    public ModernButton(Icon icon) {
        super(icon);
        setup();
    }

    public ModernButton(String text, Icon icon) {
        super(text, icon);
        setup();
    }

    private void setup() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        currentColor = defaultColor;

        // Configure animation timer
        transitionTimer = new Timer(20, e -> {
            if (targetColor != null && isTransitionRunning) {
                currentColor = transitionColor(currentColor, targetColor);
                if (isColorClose(currentColor, targetColor)) {
                    currentColor = targetColor;
                    isTransitionRunning = false;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            }
        });

        // Add mouse listeners for hover and press effects
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHover = true;
                startColorTransition(isPressed ? pressedColor : hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHover = false;
                startColorTransition(isPressed ? pressedColor : defaultColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                startColorTransition(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                startColorTransition(isHover ? hoverColor : defaultColor);
            }
        });
    }

    private void startColorTransition(Color target) {
        targetColor = target;
        isTransitionRunning = true;
        if (!transitionTimer.isRunning()) {
            transitionTimer.start();
        }
    }

    private Color transitionColor(Color current, Color target) {
        int r = transition(current.getRed(), target.getRed());
        int g = transition(current.getGreen(), target.getGreen());
        int b = transition(current.getBlue(), target.getBlue());
        return new Color(r, g, b);
    }

    private int transition(int current, int target) {
        if (current == target) {
            return current;
        }
        int step = (int) (Math.abs(current - target) * transitionStep);
        step = Math.max(step, 1);
        return current < target ? Math.min(current + step, target) : Math.max(current - step, target);
    }

    private boolean isColorClose(Color c1, Color c2) {
        return Math.abs(c1.getRed() - c2.getRed()) <= 3
                && Math.abs(c1.getGreen() - c2.getGreen()) <= 3
                && Math.abs(c1.getBlue() - c2.getBlue()) <= 3;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint button background
        g2.setColor(currentColor);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        // Add slight shadow effect
        if (!isPressed) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fill(new RoundRectangle2D.Float(0, getHeight() - 3, getWidth(), 3, cornerRadius, cornerRadius));
        }

        g2.dispose();

        // Paint the text and icon
        super.paintComponent(g);
    }

    // Customization methods
    public void setDefaultColor(Color color) {
        this.defaultColor = color;
        if (!isHover && !isPressed) {
            startColorTransition(color);
        }
    }

    public void setHoverColor(Color color) {
        this.hoverColor = color;
        if (isHover && !isPressed) {
            startColorTransition(color);
        }
    }

    public void setPressedColor(Color color) {
        this.pressedColor = color;
        if (isPressed) {
            startColorTransition(color);
        }
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setTransitionSpeed(float speed) {
        if (speed > 0 && speed <= 1) {
            this.transitionStep = speed;
        }
    }
}
