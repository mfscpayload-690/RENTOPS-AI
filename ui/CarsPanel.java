package ui;

import javax.swing.*;
import java.awt.*;

public class CarsPanel extends JPanel {
    public CarsPanel() {
        setBackground(new Color(235, 240, 245));
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("Cars Module");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(new Color(52, 73, 94));
        add(label);
    }
}
