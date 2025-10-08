package ui;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    public AdminPanel() {
        setBackground(new Color(250, 240, 245));
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("Admin Module");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(new Color(142, 68, 173));
        add(label);
    }
}
