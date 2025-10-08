package ui;

import javax.swing.*;
import java.awt.*;

public class RentalsPanel extends JPanel {
    public RentalsPanel() {
        setBackground(new Color(240, 245, 250));
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("Rentals Module");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(new Color(41, 128, 185));
        add(label);
    }
}
