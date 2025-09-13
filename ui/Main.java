package ui;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RentopsAIMainFrame().setVisible(true));
    }
}

class RentopsAIMainFrame extends JFrame {
    public RentopsAIMainFrame() {
        setTitle("Rentops-AI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        services.AuthService authService = new services.AuthService();

        LoginPanel loginPanel = new LoginPanel(authService, () -> {
            if (authService.isAdmin()) {
                cardLayout.show(cardPanel, "admin");
            } else {
                cardLayout.show(cardPanel, "user");
            }
        });
        AdminDashboard adminDashboard = new AdminDashboard();
        UserDashboard userDashboard = new UserDashboard();

        cardPanel.add(loginPanel, "login");
        cardPanel.add(adminDashboard, "admin");
        cardPanel.add(userDashboard, "user");

        cardLayout.show(cardPanel, "login");

        JPanel mainBgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0,new Color(245,247,250),0,getHeight(),new Color(230,235,245));
                g2.setPaint(gp);
                g2.fillRect(0,0,getWidth(),getHeight());
            }
        };
        mainBgPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        mainBgPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainBgPanel, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 30, 40));
        menuBar.setForeground(Color.WHITE);
        menuBar.setFont(new Font("Segoe UI", Font.BOLD, 17));
        String[] menus = {"File", "Users", "Cars", "Rentals", "Reports", "Help"};
        for (String m : menus) {
            JMenu menu = new JMenu(m);
            menu.setForeground(Color.WHITE);
            menu.setFont(new Font("Segoe UI", Font.BOLD, 17));
            menuBar.add(menu);
        }
        menuBar.setBorder(BorderFactory.createMatteBorder(0,0,2,0,new Color(52,73,94)));
        return menuBar;
    }
}
