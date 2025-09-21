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
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JPanel {

    private CarDAO carDAO;
    private BookingDAO bookingDAO;
    private services.BookingService bookingService;
    private AuthService authService;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private CardLayout parentCardLayout;
    private JPanel parentCardPanel;

    // Profile value labels for refresh
    private JLabel lblProfileUsernameValue;
    private JLabel lblProfileRoleValue;
    private JLabel lblProfileOrgValue;
    private JLabel lblProfileMemberSinceValue;

    // Top bar user info references for refresh
    private JLabel topUserNameLabel;
    private JLabel topUserIconLabel;

    public UserDashboard() {
        this(null, null, null);
    }

    public UserDashboard(AuthService authService, CardLayout parentCardLayout, JPanel parentCardPanel) {
        this.carDAO = new CarDAO();
        this.bookingDAO = new BookingDAO();
        this.authService = authService;
        this.bookingService = new services.BookingService();
        this.parentCardLayout = parentCardLayout;
        this.parentCardPanel = parentCardPanel;

        setBackground(new Color(245, 247, 250));
        setLayout(new BorderLayout());

        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create user info panel (top right)
        JPanel userInfoPanel = createUserInfoPanel();
        add(userInfoPanel, BorderLayout.NORTH);

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

    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        panel.setOpaque(false);
        topUserNameLabel = new JLabel();
        topUserNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topUserNameLabel.setForeground(new Color(41, 128, 185));
        topUserIconLabel = new JLabel();

        // Initial fill from authService
        refreshTopUserInfo();

        // Refresh button to re-sync user info
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFocusPainted(false);
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.addActionListener(e -> refreshTopUserInfo());

        panel.add(btnRefresh);
        panel.add(topUserIconLabel);
        panel.add(topUserNameLabel);
        return panel;
    }

    private void refreshTopUserInfo() {
        String username = "Unknown";
        String role = "user";
        if (authService != null && authService.getCurrentUser() != null) {
            if (authService.getCurrentUser().getUsername() != null && !authService.getCurrentUser().getUsername().isEmpty()) {
                username = authService.getCurrentUser().getUsername();
            }
            if (authService.getCurrentUser().getRole() != null && !authService.getCurrentUser().getRole().isEmpty()) {
                role = authService.getCurrentUser().getRole();
            }
        }
        if (topUserNameLabel != null) {
            topUserNameLabel.setText(username);
        }
        if (topUserIconLabel != null) {
            ImageIcon icon = getRoleIcon(role);
            topUserIconLabel.setIcon(icon);
            String tip = role != null && !role.isEmpty() ? role.substring(0, 1).toUpperCase() + role.substring(1) : "User";
            topUserIconLabel.setToolTipText(tip);
        }
    }

    private ImageIcon getRoleIcon(String role) {
        String res = "/icons/" + ("admin".equalsIgnoreCase(role) ? "Admin.png" : "User.png");
        ImageIcon icon = null;
        java.net.URL url = getClass().getResource(res);
        if (url != null) {
            icon = new ImageIcon(url);
            if (icon.getIconWidth() > 32 || icon.getIconHeight() > 32) {
                Image img = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            }
        }
        if (icon == null || icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            Icon fallback = UIManager.getIcon("OptionPane.informationIcon");
            Image img = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
            fallback.paintIcon(null, img.getGraphics(), 0, 0);
            icon = new ImageIcon(img);
        }
        return icon;
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
        String[] columnNames = {"ID", "Code", "Make", "Model", "Year", "Total KM Driven", "Price/Day", "Status", "Specs"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID

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
                int modelRow = table.convertRowIndexToModel(selectedRow);
                int carId = (int) model.getValueAt(modelRow, 0);

                JPanel input = new JPanel(new GridLayout(0, 2, 8, 8));
                JSpinner startPicker = new JSpinner(new SpinnerDateModel());
                JSpinner endPicker = new JSpinner(new SpinnerDateModel());
                startPicker.setEditor(new JSpinner.DateEditor(startPicker, "yyyy-MM-dd"));
                endPicker.setEditor(new JSpinner.DateEditor(endPicker, "yyyy-MM-dd"));
                input.add(new JLabel("Start Date:"));
                input.add(startPicker);
                input.add(new JLabel("End Date:"));
                input.add(endPicker);
                int res = JOptionPane.showConfirmDialog(this, input, "Create Booking", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (res == JOptionPane.OK_OPTION) {
                    java.util.Date sd = (java.util.Date) startPicker.getValue();
                    java.util.Date ed = (java.util.Date) endPicker.getValue();
                    java.time.LocalDate start = sd.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    java.time.LocalDate end = ed.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    // Validations: no past dates, end after start, max window 30 days
                    java.time.LocalDate today = java.time.LocalDate.now();
                    if (start.isBefore(today)) {
                        Toast.error(this, "Start date cannot be in the past.");
                        return;
                    }
                    if (!end.isAfter(start)) {
                        Toast.error(this, "End date must be after start date.");
                        return;
                    }
                    long days = java.time.temporal.ChronoUnit.DAYS.between(start, end);
                    if (days > 30) {
                        Toast.error(this, "Maximum rental period is 30 days.");
                        return;
                    }
                    if (authService == null || authService.getCurrentUser() == null) {
                        JOptionPane.showMessageDialog(this, "You must be logged in to book.");
                        return;
                    }
                    int userId = authService.getCurrentUser().getId();
                    boolean ok = bookingService.createBooking(userId, carId, start, end);
                    if (ok) {
                        Toast.success(this, "Booking created. Pending approval.");
                        // reload tables
                        loadAvailableCars(model);
                    } else {
                        Toast.error(this, "Failed to create booking (car unavailable or invalid dates).");
                    }
                }
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
                        String carCode = utils.DisplayCodeUtil.codeFromName(car.getModel());
                        model.addRow(new Object[]{
                            car.getId(),
                            carCode,
                            car.getMake(),
                            car.getModel(),
                            car.getYear(),
                            car.getTotalKmDriven() + " KM",
                            "₹" + car.getPricePerDay() + "/day",
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
        DefaultTableModel bookingsModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(bookingsModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(new Color(245, 247, 250));
        JButton cancelBtn = new JButton("Cancel Booking");
        JButton refreshBtn = new JButton("Refresh");
        cancelBtn.setBackground(new Color(231, 76, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        actions.add(cancelBtn);
        actions.add(refreshBtn);
        panel.add(actions, BorderLayout.SOUTH);

        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int modelRow = table.convertRowIndexToModel(row);
                int bookingId = (int) bookingsModel.getValueAt(modelRow, 0);
                int confirm = JOptionPane.showConfirmDialog(panel, "Cancel booking #" + bookingId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (bookingService.cancelBooking(bookingId)) {
                        Toast.success(panel, "Booking cancelled.");
                        loadUserBookings(bookingsModel);
                    } else {
                        Toast.error(panel, "Failed to cancel booking.");
                    }
                }
            } else {
                Toast.info(panel, "Select a booking to cancel.");
            }
        });

        refreshBtn.addActionListener(e -> loadUserBookings(bookingsModel));

        // Initial load
        loadUserBookings(bookingsModel);

        return panel;
    }

    private void loadUserBookings(DefaultTableModel model) {
        SwingWorker<List<models.Booking>, Void> worker = new SwingWorker<List<models.Booking>, Void>() {
            @Override
            protected List<models.Booking> doInBackground() throws Exception {
                if (authService != null && authService.getCurrentUser() != null) {
                    int userId = authService.getCurrentUser().getId();
                    return bookingDAO.getBookingsByUserId(userId);
                }
                return java.util.Collections.emptyList();
            }

            @Override
            protected void done() {
                try {
                    List<models.Booking> bookings = get();
                    model.setRowCount(0);
                    for (models.Booking booking : bookings) {
                        models.Car car = carDAO.getById(booking.getCarId());
                        String carName = car != null ? car.getMake() + " " + car.getModel() + " " + car.getYear() : "Car ID " + booking.getCarId();
                        model.addRow(new Object[]{
                            booking.getId(),
                            carName,
                            booking.getStartDate(),
                            booking.getEndDate(),
                            booking.getStatus(),
                            "₹" + booking.getTotalPrice()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading bookings: " + e.getMessage());
                }
            }
        };
        worker.execute();
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
        // Labels
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        lblProfileUsernameValue = new JLabel("Unknown");
        panel.add(lblProfileUsernameValue, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        lblProfileRoleValue = new JLabel("User");
        panel.add(lblProfileRoleValue, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Organization:"), gbc);
        gbc.gridx = 1;
        lblProfileOrgValue = new JLabel("-");
        panel.add(lblProfileOrgValue, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Member Since:"), gbc);
        gbc.gridx = 1;
        lblProfileMemberSinceValue = new JLabel("-");
        panel.add(lblProfileMemberSinceValue, gbc);

        // Actions: Refresh + Logout
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 20, 20, 20);

        JPanel actionsPanel = new JPanel(new FlowLayout());
        actionsPanel.setBackground(new Color(245, 247, 250));

        JButton refreshButton = new JButton("Refresh Profile");
        refreshButton.setBackground(new Color(52, 152, 219));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(170, 40));
        refreshButton.addActionListener(e -> refreshProfileInfo());

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.addActionListener(e -> showLogoutDialog());

        actionsPanel.add(refreshButton);
        actionsPanel.add(Box.createHorizontalStrut(16));
        actionsPanel.add(logoutButton);

        panel.add(actionsPanel, gbc);

        // Initialize with current values
        refreshProfileInfo();

        return panel;
    }

    private void refreshProfileInfo() {
        String username = "Unknown";
        String role = "user";
        String organization = "-";
        String memberSince = "-";
        if (authService != null && authService.getCurrentUser() != null) {
            if (authService.getCurrentUser().getUsername() != null && !authService.getCurrentUser().getUsername().isEmpty()) {
                username = authService.getCurrentUser().getUsername();
            }
            if (authService.getCurrentUser().getRole() != null && !authService.getCurrentUser().getRole().isEmpty()) {
                role = authService.getCurrentUser().getRole();
            }
            if (authService.getCurrentUser().getOrganization() != null && !authService.getCurrentUser().getOrganization().isEmpty()) {
                organization = authService.getCurrentUser().getOrganization();
            }
            java.time.LocalDateTime createdAt = authService.getCurrentUser().getCreatedAt();
            if (createdAt != null) {
                java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy, HH:mm");
                memberSince = createdAt.format(fmt);
            }
        }

        if (lblProfileUsernameValue != null) {
            lblProfileUsernameValue.setText(username);
        }
        if (lblProfileRoleValue != null) {
            String cap = role.substring(0, 1).toUpperCase() + role.substring(1);
            lblProfileRoleValue.setText(cap);
        }
        if (lblProfileOrgValue != null) {
            lblProfileOrgValue.setText(organization);
        }
        if (lblProfileMemberSinceValue != null) {
            lblProfileMemberSinceValue.setText(memberSince);
        }
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
