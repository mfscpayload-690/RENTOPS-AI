package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import services.AuthService;
// import ui.components.KeyboardShortcuts;
// import utils.DatabaseSeeder;

public class Main {

    public static void main(String[] args) {
        // Seed cars before launching UI (non-blocking safety)
        /*
        try {
            DatabaseSeeder.seedCarsIfNeeded();
        } catch (Exception e) {
            System.err.println("Startup seeding failed: " + e.getMessage());
        }
         */
        SwingUtilities.invokeLater(() -> new RentopsAIMainFrame().setVisible(true));
    }
}

class RentopsAIMainFrame extends JFrame {

    private AuthService authService;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public RentopsAIMainFrame() {
        this.authService = new AuthService();

        setTitle("Rentops-AI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(createMenuBar());
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        initializePanels();

        // Check for existing session and auto-login
        if (authService.restoreSession()) {
            // Auto-login successful
            if (authService.isAdmin()) {
                cardLayout.show(cardPanel, "admin");
            } else {
                cardLayout.show(cardPanel, "user");
            }
        } else {
            // No existing session, show login
            cardLayout.show(cardPanel, "login");
        }

        JPanel mainBgPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 247, 250), 0, getHeight(), new Color(230, 235, 245));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainBgPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        mainBgPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainBgPanel, BorderLayout.CENTER);
    }

    private void initializePanels() {
        // Create login panel first; dashboards are created only after login/restore
        LoginPanel loginPanel = new LoginPanel(authService, () -> {
            // On successful login, instantiate the appropriate dashboard so it picks up current user
            System.out.println("[DEBUG] Login success callback invoked. isAdmin=" + authService.isAdmin() + " user=" + (authService.getCurrentUser() != null ? authService.getCurrentUser().getUsername() : "null"));
            if (authService.isAdmin()) {
                AdminDashboard adminDashboard = new AdminDashboard(authService, cardLayout, cardPanel);
                cardPanel.add(adminDashboard, "admin");
                cardLayout.show(cardPanel, "admin");
                System.out.println("[DEBUG] Switched to admin dashboard");
            } else {
                UserDashboard userDashboard = new UserDashboard(authService, cardLayout, cardPanel);
                cardPanel.add(userDashboard, "user");
                cardLayout.show(cardPanel, "user");
                System.out.println("[DEBUG] Switched to user dashboard");
            }
            cardPanel.revalidate();
            cardPanel.repaint();
        });

        cardPanel.add(loginPanel, "login");

        // If session restore succeeds, create the corresponding dashboard now
        if (authService.isLoggedIn()) {
            if (authService.isAdmin()) {
                AdminDashboard adminDashboard = new AdminDashboard(authService, cardLayout, cardPanel);
                cardPanel.add(adminDashboard, "admin");
            } else {
                UserDashboard userDashboard = new UserDashboard(authService, cardLayout, cardPanel);
                cardPanel.add(userDashboard, "user");
            }
        }

        // Initialize keyboard shortcuts for the main frame
        // KeyboardShortcuts.initialize(getRootPane());
        // Show a hint about keyboard shortcuts after a short delay
        // Timer hintTimer = new Timer(1500, e -> KeyboardShortcuts.showShortcutHint(this));
        // hintTimer.setRepeats(false);
        // hintTimer.start();
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
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 73, 94)));
        return menuBar;
    }
}
