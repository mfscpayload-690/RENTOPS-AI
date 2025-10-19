package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import services.AuthService;
import utils.DatabaseSeeder;
import utils.ModernTheme;

/**
 * Main entry point for RENTOPS-AI application
 * 
 * Initialization sequence:
 * 1. Apply modern dark theme (FlatLaf)
 * 2. Seed database with initial data if needed
 * 3. Launch main frame with login screen
 * 4. Auto-restore previous session if available
 */
public class Main {

    public static void main(String[] args) {
        // STEP 1: Initialize modern theme BEFORE creating any UI components
        System.out.println("Initializing modern theme...");
        ModernTheme.initialize();
        
        // STEP 2: Seed cars before launching UI (non-blocking safety)
        try {
            DatabaseSeeder.seedCarsIfNeeded();
        } catch (Exception e) {
            System.err.println("Startup seeding failed: " + e.getMessage());
        }
        
        // STEP 3: Launch UI on EDT
        SwingUtilities.invokeLater(() -> new RentopsAIMainFrame().setVisible(true));
    }
}

class RentopsAIMainFrame extends JFrame {

    private AuthService authService;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public RentopsAIMainFrame() {
        this.authService = new AuthService();

        setTitle("Rentops-AI - Modern Car Rental Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800); // Increased size for modern layout
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(ModernTheme.BG_DARK);

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

        add(cardPanel, BorderLayout.CENTER);
    }

    private void initializePanels() {
        // Create modern login panel first
        ModernLoginPanel loginPanel = new ModernLoginPanel(authService, () -> {
            // On successful login, instantiate the appropriate dashboard
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
    }
}
