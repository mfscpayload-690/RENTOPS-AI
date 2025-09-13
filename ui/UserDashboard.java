package ui;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JPanel {
    public UserDashboard() {
        setBackground(new Color(245,247,250));
        setLayout(new GridBagLayout());
        JLabel label = new JLabel("User Dashboard");
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(new Color(41, 128, 185));
        add(label);
        // TODO: Add user panels (search cars, bookings, history)
        // AI/NLP integration point: semantic search, Q&A
    }
}
