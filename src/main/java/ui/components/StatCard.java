package ui.components;

import utils.ModernTheme;
import javax.swing.*;
import java.awt.*;

public class StatCard extends JPanel {
    private JLabel valueLabel;
    
    public StatCard(String title, String value, Color accentColor) {
        setLayout(new BorderLayout(10, 10));
        setBackground(ModernTheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ModernTheme.MEDIUM_FONT);
        titleLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        add(titleLabel, BorderLayout.NORTH);
        
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font(ModernTheme.PRIMARY_FONT.getName(), Font.BOLD, 32));
        valueLabel.setForeground(accentColor != null ? accentColor : ModernTheme.TEXT_PRIMARY);
        add(valueLabel, BorderLayout.CENTER);
    }
    
    public void updateValue(String newValue) {
        valueLabel.setText(newValue);
    }
    
    public void updateValue(String newValue, String subtitle) {
        valueLabel.setText(newValue);
        // Subtitle ignored for now - simple implementation
    }
}
