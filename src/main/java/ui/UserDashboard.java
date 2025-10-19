package ui;

import dao.CarDAO;
import dao.BookingDAO;
import models.Car;
import models.Booking;
import services.AuthService;
import ui.components.*;
import utils.ModernTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE));

        // Header section
        JPanel headerPanel = ModernTheme.createHeaderPanel("Welcome to Rentops-AI", "Your smart car rental solution");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Main content with stats
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(ModernTheme.BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Create stats cards
        StatCard myBookingsCard = new StatCard("My Active Bookings", "0", ModernTheme.ACCENT_BLUE);
        StatCard availableCarsCard = new StatCard("Available Cars", "0", ModernTheme.SUCCESS_GREEN);
        StatCard totalSpentCard = new StatCard("Total Spent", "₹0", ModernTheme.ACCENT_ORANGE);
        StatCard carsRentedCard = new StatCard("Cars Rented", "0", ModernTheme.WARNING_YELLOW);

        // Layout stats in 2x2 grid
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(myBookingsCard, gbc);

        gbc.gridx = 1;
        contentPanel.add(availableCarsCard, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(totalSpentCard, gbc);

        gbc.gridx = 1;
        contentPanel.add(carsRentedCard, gbc);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Quick action buttons at bottom
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        actionsPanel.setBackground(ModernTheme.BG_DARK);

        ActionButton searchButton = new ActionButton("Search Available Cars", ActionButton.Type.SUCCESS);
        searchButton.setPreferredSize(new Dimension(220, 50));
        searchButton.addActionListener(e -> cardLayout.show(contentPanel, "search"));

        ActionButton bookingsButton = new ActionButton("View My Bookings", ActionButton.Type.PRIMARY);
        bookingsButton.setPreferredSize(new Dimension(220, 50));
        bookingsButton.addActionListener(e -> cardLayout.show(contentPanel, "bookings"));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonRow.setBackground(ModernTheme.BG_DARK);
        buttonRow.add(searchButton);
        buttonRow.add(bookingsButton);

        // Keyboard shortcut info
        JLabel shortcutInfo = new JLabel("Press Ctrl+H to view keyboard shortcuts");
        shortcutInfo.setFont(ModernTheme.FONT_REGULAR);
        shortcutInfo.setForeground(ModernTheme.TEXT_SECONDARY);

        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        shortcutInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(buttonRow);
        actionsPanel.add(Box.createVerticalStrut(20));
        actionsPanel.add(shortcutInfo);

        panel.add(actionsPanel, BorderLayout.SOUTH);

        // Load stats asynchronously
        loadUserStats(myBookingsCard, availableCarsCard, totalSpentCard, carsRentedCard);

        return panel;
    }

    private void loadUserStats(StatCard bookingsCard, StatCard availableCarsCard, StatCard spentCard, StatCard rentedCard) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int activeBookings = 0;
            private int availableCars = 0;
            private double totalSpent = 0.0;
            private int carsRented = 0;

            @Override
            protected Void doInBackground() throws Exception {
                // Get user's active bookings
                if (authService != null && authService.getCurrentUser() != null) {
                    int userId = authService.getCurrentUser().getId();
                    List<Booking> userBookings = bookingDAO.getBookingsByUserId(userId);
                    activeBookings = (int) userBookings.stream()
                        .filter(b -> "pending".equalsIgnoreCase(b.getStatus()) || "active".equalsIgnoreCase(b.getStatus()))
                        .count();
                    carsRented = userBookings.size();
                    totalSpent = userBookings.stream()
                        .map(Booking::getTotalPrice)
                        .filter(price -> price != null)
                        .mapToDouble(price -> price.doubleValue())
                        .sum();
                }
                // Get available cars count
                List<Car> cars = carDAO.getAvailableCars();
                availableCars = cars.size();
                return null;
            }

            @Override
            protected void done() {
                bookingsCard.updateValue(String.valueOf(activeBookings), "");
                availableCarsCard.updateValue(String.valueOf(availableCars), "");
                spentCard.updateValue("₹" + String.format("%,.2f", totalSpent), "");
                rentedCard.updateValue(String.valueOf(carsRented), "");
            }
        };
        worker.execute();
    }

    private JPanel createSearchCarsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE));

        // Header section
        JPanel headerPanel = ModernTheme.createHeaderPanel("Available Cars", "Browse and rent cars from our fleet");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, ModernTheme.PADDING_MEDIUM));
        contentPanel.setBackground(ModernTheme.BG_DARK);

        // Toolbar with search and actions
        JPanel toolbar = ModernTheme.createToolbar();
        
        JPanel searchBar = ModernTheme.createSearchBar("Search by make, model, or year...");
        searchBar.setPreferredSize(new Dimension(400, ModernTheme.INPUT_HEIGHT));
        toolbar.add(searchBar);
        
        toolbar.add(Box.createHorizontalStrut(15));
        
        ActionButton refreshButton = new ActionButton("Refresh", ActionButton.Type.SECONDARY);
        refreshButton.setPreferredSize(new Dimension(120, 40));
        toolbar.add(refreshButton);

        contentPanel.add(toolbar, BorderLayout.NORTH);

        // Modern table
        String[] columnNames = {"ID", "Code", "Make", "Model", "Year", "Total KM Driven", "Price/Day", "Status", "Specs"};
        
        ModernTable modernTable = new ModernTable(columnNames, false);
        DefaultTableModel tableModel = modernTable.getModel();
        JTable table = modernTable.getTable();
        table.removeColumn(table.getColumnModel().getColumn(0)); // hide ID
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(ModernTheme.BG_DARK);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, ModernTheme.PADDING_MEDIUM));
        actionPanel.setBackground(ModernTheme.BG_DARK);

        ActionButton viewDetailsButton = new ActionButton("View Details", ActionButton.Type.SECONDARY);
        viewDetailsButton.setPreferredSize(new Dimension(150, 45));

        ActionButton rentButton = new ActionButton("Rent Selected Car", ActionButton.Type.SUCCESS);
        rentButton.setPreferredSize(new Dimension(180, 45));

        actionPanel.add(viewDetailsButton);
        actionPanel.add(rentButton);

        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        // View Details button action
        viewDetailsButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                int carId = (int) tableModel.getValueAt(modelRow, 0);
                try {
                    Car car = carDAO.getById(carId);
                    if (car != null) {
                        new CarDetailsDialog(this, car).setVisible(true);
                    } else {
                        Toast.error(this, "Car details not found");
                    }
                } catch (Exception ex) {
                    Toast.error(this, "Error loading car details: " + ex.getMessage());
                }
            } else {
                Toast.info(this, "Please select a car first");
            }
        });

        // Rent button action
        rentButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                int carId = (int) tableModel.getValueAt(modelRow, 0);

                JPanel input = new JPanel(new GridLayout(0, 2, 8, 8));
                input.setBackground(ModernTheme.BG_CARD);
                JSpinner startPicker = new JSpinner(new SpinnerDateModel());
                JSpinner endPicker = new JSpinner(new SpinnerDateModel());
                startPicker.setEditor(new JSpinner.DateEditor(startPicker, "yyyy-MM-dd"));
                endPicker.setEditor(new JSpinner.DateEditor(endPicker, "yyyy-MM-dd"));
                
                JLabel startLabel = new JLabel("Start Date:");
                startLabel.setForeground(ModernTheme.TEXT_PRIMARY);
                JLabel endLabel = new JLabel("End Date:");
                endLabel.setForeground(ModernTheme.TEXT_PRIMARY);
                
                input.add(startLabel);
                input.add(startPicker);
                input.add(endLabel);
                input.add(endPicker);
                
                int res = JOptionPane.showConfirmDialog(this, input, "Create Booking", 
                                                         JOptionPane.OK_CANCEL_OPTION, 
                                                         JOptionPane.PLAIN_MESSAGE);
                if (res == JOptionPane.OK_OPTION) {
                    java.util.Date sd = (java.util.Date) startPicker.getValue();
                    java.util.Date ed = (java.util.Date) endPicker.getValue();
                    java.time.LocalDate start = sd.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    java.time.LocalDate end = ed.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    
                    // Validations
                    java.time.LocalDate today = java.time.LocalDate.now();
                    if (start.isBefore(today)) {
                        Toast.error(this, "Start date cannot be in the past");
                        return;
                    }
                    if (!end.isAfter(start)) {
                        Toast.error(this, "End date must be after start date");
                        return;
                    }
                    long days = java.time.temporal.ChronoUnit.DAYS.between(start, end);
                    if (days > 30) {
                        Toast.error(this, "Maximum rental period is 30 days");
                        return;
                    }
                    if (authService == null || authService.getCurrentUser() == null) {
                        Toast.error(this, "You must be logged in to book");
                        return;
                    }
                    
                    int userId = authService.getCurrentUser().getId();
                    boolean ok = bookingService.createBooking(userId, carId, start, end);
                    if (ok) {
                        Toast.success(this, "Booking created. Pending approval");
                        loadAvailableCars(tableModel);
                    } else {
                        Toast.error(this, "Failed to create booking (car unavailable or invalid dates)");
                    }
                }
            } else {
                Toast.info(this, "Please select a car to rent");
            }
        });

        // Refresh button action
        refreshButton.addActionListener(e -> loadAvailableCars(tableModel));

        // Double-click to view details
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        int carId = (int) tableModel.getValueAt(modelRow, 0);
                        try {
                            Car car = carDAO.getById(carId);
                            if (car != null) {
                                new CarDetailsDialog(UserDashboard.this, car).setVisible(true);
                            }
                        } catch (Exception ex) {
                            Toast.error(UserDashboard.this, "Error loading car details");
                        }
                    }
                }
            }
        });

        // Search functionality
        JTextField searchField = ModernTheme.getSearchField(searchBar);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String query = searchField.getText().toLowerCase().trim();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
                table.setRowSorter(sorter);
                if (query.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }
        });

        // Load available cars
        loadAvailableCars(tableModel);

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
        panel.setBackground(ModernTheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE));

        // Header section
        JPanel headerPanel = ModernTheme.createHeaderPanel("My Bookings", "Track and manage your car rentals");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout(0, ModernTheme.PADDING_MEDIUM));
        contentPanel.setBackground(ModernTheme.BG_DARK);

        // Toolbar
        JPanel toolbar = ModernTheme.createToolbar();
        
        ActionButton refreshButton = new ActionButton("Refresh", ActionButton.Type.SECONDARY);
        refreshButton.setPreferredSize(new Dimension(120, 40));
        toolbar.add(refreshButton);

        contentPanel.add(toolbar, BorderLayout.NORTH);

        // Modern table
        String[] columnNames = {"Booking ID", "Car", "Start Date", "End Date", "Status", "Total Price"};
        
        ModernTable modernTable = new ModernTable(columnNames, false);
        DefaultTableModel tableModel = modernTable.getModel();
        JTable table = modernTable.getTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(ModernTheme.BG_DARK);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, ModernTheme.PADDING_MEDIUM));
        actionPanel.setBackground(ModernTheme.BG_DARK);

        ActionButton cancelButton = new ActionButton("Cancel Booking", ActionButton.Type.DANGER);
        cancelButton.setPreferredSize(new Dimension(160, 45));

        actionPanel.add(cancelButton);
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        // Cancel button action
        cancelButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int modelRow = table.convertRowIndexToModel(row);
                int bookingId = (int) tableModel.getValueAt(modelRow, 0);
                int confirm = JOptionPane.showConfirmDialog(panel, 
                                                             "Cancel booking #" + bookingId + "?", 
                                                             "Confirm Cancellation", 
                                                             JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (bookingService.cancelBooking(bookingId)) {
                        Toast.success(panel, "Booking cancelled successfully");
                        loadUserBookings(tableModel);
                    } else {
                        Toast.error(panel, "Failed to cancel booking");
                    }
                }
            } else {
                Toast.info(panel, "Please select a booking to cancel");
            }
        });

        // Refresh button action
        refreshButton.addActionListener(e -> loadUserBookings(tableModel));

        // Initial load
        loadUserBookings(tableModel);

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE, 
                                                         ModernTheme.PADDING_LARGE));

        // Header section
        JPanel headerPanel = ModernTheme.createHeaderPanel("User Profile", "Manage your account settings");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Main content - Card panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ModernTheme.BG_DARK);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Profile info card
        JPanel profileCard = ModernTheme.createCardPanel();
        profileCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(ModernTheme.FONT_BOLD);
        usernameLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        profileCard.add(usernameLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        lblProfileUsernameValue = new JLabel("Unknown");
        lblProfileUsernameValue.setFont(ModernTheme.FONT_LARGE);
        lblProfileUsernameValue.setForeground(ModernTheme.TEXT_PRIMARY);
        profileCard.add(lblProfileUsernameValue, gbc);

        // Role row
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(ModernTheme.FONT_BOLD);
        roleLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        profileCard.add(roleLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        lblProfileRoleValue = new JLabel("User");
        lblProfileRoleValue.setFont(ModernTheme.FONT_LARGE);
        lblProfileRoleValue.setForeground(ModernTheme.TEXT_PRIMARY);
        profileCard.add(lblProfileRoleValue, gbc);

        // Organization row
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel orgLabel = new JLabel("Organization:");
        orgLabel.setFont(ModernTheme.FONT_BOLD);
        orgLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        profileCard.add(orgLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        lblProfileOrgValue = new JLabel("-");
        lblProfileOrgValue.setFont(ModernTheme.FONT_LARGE);
        lblProfileOrgValue.setForeground(ModernTheme.TEXT_PRIMARY);
        profileCard.add(lblProfileOrgValue, gbc);

        // Member Since row
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel memberLabel = new JLabel("Member Since:");
        memberLabel.setFont(ModernTheme.FONT_BOLD);
        memberLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        profileCard.add(memberLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        lblProfileMemberSinceValue = new JLabel("-");
        lblProfileMemberSinceValue.setFont(ModernTheme.FONT_LARGE);
        lblProfileMemberSinceValue.setForeground(ModernTheme.TEXT_PRIMARY);
        profileCard.add(lblProfileMemberSinceValue, gbc);

        profileCard.setMaximumSize(new Dimension(800, 250));
        profileCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(profileCard);
        contentPanel.add(Box.createVerticalStrut(ModernTheme.PADDING_LARGE));

        // Action buttons panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        actionsPanel.setBackground(ModernTheme.BG_DARK);
        actionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ActionButton refreshButton = new ActionButton("Refresh Profile", ActionButton.Type.SECONDARY);
        refreshButton.setPreferredSize(new Dimension(170, 45));
        refreshButton.addActionListener(e -> refreshProfileInfo());

        ActionButton logoutButton = new ActionButton("Logout", ActionButton.Type.DANGER);
        logoutButton.setPreferredSize(new Dimension(150, 45));
        logoutButton.addActionListener(e -> showLogoutDialog());

        actionsPanel.add(refreshButton);
        actionsPanel.add(logoutButton);
        contentPanel.add(actionsPanel);

        // Add content to panel with some bottom spacing
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(ModernTheme.BG_DARK);
        wrapperPanel.add(contentPanel, BorderLayout.NORTH);
        
        panel.add(wrapperPanel, BorderLayout.CENTER);

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
