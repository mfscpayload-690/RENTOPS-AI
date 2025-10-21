package ui.components;

import utils.ModernTheme;
import javax.swing.*;

public class ActionButton extends JButton {
    public enum Style {
        PRIMARY, SUCCESS, DANGER, WARNING, SECONDARY
    }
    
    public static enum Type {
        PRIMARY, SUCCESS, DANGER, WARNING, SECONDARY, GHOST
    }
    
    public ActionButton(String text, Style style) {
        this(text, style.name());
    }
    
    public ActionButton(String text, Type type) {
        this(text, type.name());
    }
    
    private ActionButton(String text, String styleType) {
        super(text);
        setFont(ModernTheme.MEDIUM_FONT);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(true);
        
        switch (styleType) {
            case "PRIMARY":
                setBackground(ModernTheme.ACCENT_BLUE);
                break;
            case "SUCCESS":
                setBackground(ModernTheme.SUCCESS_GREEN);
                break;
            case "DANGER":
                setBackground(ModernTheme.ERROR_RED);
                break;
            case "WARNING":
                setBackground(ModernTheme.WARNING_YELLOW);
                break;
            case "SECONDARY":
                setBackground(ModernTheme.BG_INPUT);
                break;
            case "GHOST":
                setBackground(ModernTheme.BG_CARD);
                setBorderPainted(true);
                setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR));
                break;
        }
        
        setForeground(ModernTheme.TEXT_PRIMARY);
    }
    
    public void setLoading(boolean loading) {
        setEnabled(!loading);
        setText(loading ? "Loading..." : getText());
    }
}
