package ui;

import javax.swing.*;
import java.awt.*;

public class PaymentsPanel extends JPanel {
    public PaymentsPanel() {
        setBackground(new Color(245, 245, 240));
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("Payments Module");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(new Color(39, 174, 96));
        add(label);
    }
}
