package ui;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setBackground(new Color(245, 247, 250));
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("Dashboard Module");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(new Color(44, 62, 80));
        add(label);
    }
}
