package ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class MainContentPanel extends JPanel {
    private CardLayout cardLayout;
    private HashMap<String, JPanel> panels;

    public MainContentPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        panels = new HashMap<>();

        addPanel("Dashboard", new DashboardPanel());
        addPanel("Cars", new CarsPanel());
        addPanel("Rentals", new RentalsPanel());
        addPanel("Payments", new PaymentsPanel());
        addPanel("Admin", new AdminPanel());

        cardLayout.show(this, "Dashboard");
    }

    public void showPanel(String name) {
        cardLayout.show(this, name);
    }

    private void addPanel(String name, JPanel panel) {
        panels.put(name, panel);
        add(panel, name);
    }
}
