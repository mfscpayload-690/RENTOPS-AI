package ui.components;

import utils.ModernTheme;
import javax.swing.*;
import java.awt.*;

public class StatusBadge extends JLabel {
    public enum Type {
        SUCCESS, ERROR, WARNING, INFO, PENDING
    }

    public static StatusBadge create(String text, Type type) {
        StatusBadge badge = new StatusBadge(text);
        badge.setOpaque(true);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        switch (type) {
            case SUCCESS:
                badge.setBackground(ModernTheme.SUCCESS_GREEN.darker());
                badge.setForeground(Color.WHITE);
                break;
            case ERROR:
                badge.setBackground(ModernTheme.ERROR_RED);
                badge.setForeground(Color.WHITE);
                break;
            case WARNING:
                badge.setBackground(ModernTheme.WARNING_YELLOW);
                badge.setForeground(ModernTheme.SURFACE_DARK);
                break;
            case INFO:
                badge.setBackground(ModernTheme.ACCENT_BLUE);
                badge.setForeground(Color.WHITE);
                break;
            case PENDING:
                badge.setBackground(ModernTheme.ACCENT_ORANGE);
                badge.setForeground(Color.WHITE);
                break;
        }

        return badge;
    }

    private StatusBadge(String text) {
        super(text.toUpperCase());
    }
    
    // Convenience methods
    public static StatusBadge success(String text) {
        return create(text, Type.SUCCESS);
    }
    
    public static StatusBadge error(String text) {
        return create(text, Type.ERROR);
    }
    
    public static StatusBadge warning(String text) {
        return create(text, Type.WARNING);
    }
    
    public static StatusBadge info(String text) {
        return create(text, Type.INFO);
    }
    
    public static StatusBadge primary(String text) {
        return create(text, Type.INFO);
    }
    
    public static StatusBadge pending(String text) {
        return create(text, Type.PENDING);
    }
    
    public static StatusBadge neutral(String text) {
        return create(text, Type.PENDING);
    }
}
