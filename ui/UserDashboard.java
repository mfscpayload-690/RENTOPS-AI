package ui;

import dao.CarDAO;
import dao.BookingDAO;
import models.Car;
import models.Booking;
import services.AuthService;
import ui.components.KeyboardShortcuts;
import ui.components.Toast;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JPanel {

    private CarDAO carDAO;
    private BookingDAO bookingDAO;
    private AuthService authService;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private CardLayout parentCardLayout;
    private JPanel parentCardPanel;

    public UserDashboard() {
        this(null, null, null);
    }

    public UserDashboard(AuthService authService, CardLayout parentCardLayout, JPanel parentCardPanel) {
        this.carDAO = new CarDAO();
        this.bookingDAO = new BookingDAO();
        this.authService = authService;
        this.parentCardLayout = parentCardLayout;
        this.parentCardPanel = parentCardPanel;

        setBackground(new Color(245, 247, 250));
        setLayout(new BorderLayout());

        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create main content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 250));

        // Add different panels
        contentPanel.add(createWelcomePanel(), "welcome");
        contentPanel.add(createSearchCarsPanel(), "search");
        contentPanel.add(createMyBookingsPanel(), "bookings");
        contentPanel.add(createProfilePanel(), "profile");

        add(contentPanel, BorderLayout.CENTER);

        // Show welcome by default
        cardLayout.show(contentPanel, "welcome");

        // Initialize keyboard shortcuts
        initializeKeyboardShortcuts();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(41, 128, 185));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel title = new JLabel("User Panel");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(30));

        String[] menuItems = {"Welcome", "Search Cars", "My Bookings", "Profile"};
        String[] cardNames = {"welcome", "search", "bookings", "profile"};

        for (int i = 0; i < menuItems.length; i++) {
            final String cardName = cardNames[i];
            JButton button = new JButton(menuItems[i]);
            button.setBackground(new Color(52, 152, 219));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.addActionListener(e -> cardLayout.show(contentPanel, cardName));

            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(10));
        }

        // Add logout button at the bottom
        sidebar.add(Box.createVerticalGlue()); // Push logout button to bottom

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(180, 40));
        logoutButton.addActionListener(e -> showLogoutDialog());

        sidebar.add(logoutButton);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("Welcome to Rentops-AI");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(41, 128, 185));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("Your smart car rental solution");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(new Color(127, 140, 141));
        gbc.gridy++;
        panel.add(subtitle, gbc);

        // Quick action buttons
        gbc.gridy++;
        gbc.insets = new Insets(40, 20, 20, 20);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton searchButton = new JButton("Search Available Cars");
        searchButton.setBackground(new Color(46, 204, 113));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchButton.setPreferredSize(new Dimension(200, 50));
        searchButton.addActionListener(e -> cardLayout.show(contentPanel, "search"));

        JButton bookingsButton = new JButton("View My Bookings");
        bookingsButton.setBackground(new Color(52, 152, 219));
        bookingsButton.setForeground(Color.WHITE);
        bookingsButton.setFocusPainted(false);
        bookingsButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bookingsButton.setPreferredSize(new Dimension(200, 50));
        bookingsButton.addActionListener(e -> cardLayout.show(contentPanel, "bookings"));

        buttonPanel.add(searchButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(bookingsButton);

        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createSearchCarsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Available Cars");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(41, 128, 185));
        panel.add(title, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(245, 247, 250));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Cars table
        String[] columnNames = {"Make", "Model", "Year", "Price/Day", "Status", "Specs"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(245, 247, 250));

        JButton rentButton = new JButton("Rent Selected Car");
        rentButton.setBackground(new Color(46, 204, 113));
        rentButton.setForeground(Color.WHITE);
        rentButton.setFocusPainted(false);
        rentButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        rentButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                JOptionPane.showMessageDialog(this, "Booking functionality will be implemented soon!");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a car to rent.");
            }
        });

        bottomPanel.add(rentButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Load available cars
        loadAvailableCars(model);

        return panel;
    }

    private void loadAvailableCars(DefaultTableModel model) {
        SwingWorker<List<Car>, Void> worker = new SwingWorker<List<Car>, Void>() {
            @Override
            protected List<Car> doInBackground() throws Exception {
                return carDAO.getAvailableCars();
            }

            @Override
            protected void done() {
                try {
                    List<Car> cars = get();
                    model.setRowCount(0);
                    for (Car car : cars) {
                        model.addRow(new Object[]{
                            car.getMake(),
                            car.getModel(),
                            car.getYear(),
                            "$" + car.getPricePerDay() + "/day",
                            car.getStatus(),
                            car.getSpecs()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading cars: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("My Bookings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(41, 128, 185));
        panel.add(title, BorderLayout.NORTH);

        // Bookings table
        String[] columnNames = {"Booking ID", "Car", "Start Date", "End Date", "Status", "Total Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Sample data (would load from database based on current user)
        model.addRow(new Object[]{"1", "Toyota Corolla 2022", "2023-12-01", "2023-12-05", "Completed", "$200.00"});
        model.addRow(new Object[]{"2", "Honda Civic 2023", "2023-12-15", "2023-12-20", "Confirmed", "$275.00"});

        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("User Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(41, 128, 185));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Profile information
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("Current User"), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("User"), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Member Since:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel("January 2023"), gbc);

        // Logout button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 20, 20, 20);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setPreferredSize(new Dimension(150, 40));

        logoutButton.addActionListener(e -> showLogoutDialog());

        panel.add(logoutButton, gbc);

        return panel;
    }

    private void showLogoutDialog() {
        if (authService != null && parentCardLayout != null && parentCardPanel != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            LogoutDialog dialog = new LogoutDialog(parentFrame, authService, parentCardLayout, parentCardPanel);
            dialog.setVisible(true);
        } else {
            // Fallback if not properly initialized
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION
            );
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0); // Simple exit as fallback
            }
        }
    }

    /**
     * Initialize keyboard shortcuts for the dashboard
     */
    private void initializeKeyboardShortcuts() {
        KeyboardShortcuts.initialize(this);

        // Delay showing the toast until the component is visible
        Timer toastTimer = new Timer(1500, e -> {
            if (isShowing() && isVisible()) {
                Toast.info(this, "Press Ctrl+H to view keyboard shortcuts");
            }
        });
        toastTimer.setRepeats(false);
        toastTimer.start();
    }
}
