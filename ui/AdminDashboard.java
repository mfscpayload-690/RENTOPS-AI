package ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JPanel {
    public AdminDashboard() {
        setBackground(new Color(245,247,250));
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("Admin Dashboard");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(new Color(142, 68, 173));
        add(label);
        // TODO: Add admin management panels (cars, bookings, payments, reports)
        // AI/NLP integration point: analytics, natural language queries
    }
}
