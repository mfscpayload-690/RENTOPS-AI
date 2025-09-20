package ui;

import dao.CarDAO;
import dao.BookingDAO;
import dao.UserDAO;
import models.Car;
import models.Booking;
import models.User;
import ui.components.KeyboardShortcuts;
import ui.components.Toast;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDashboard extends JPanel {

    private CarDAO carDAO;
    private BookingDAO bookingDAO;
    private UserDAO userDAO;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private services.AuthService authService;
    private CardLayout parentCardLayout;
    private JPanel parentCardPanel;

    public AdminDashboard() {
        this(null, null, null);
    }

    public AdminDashboard(services.AuthService authService, CardLayout parentCardLayout, JPanel parentCardPanel) {
        this.carDAO = new CarDAO();
        this.bookingDAO = new BookingDAO();
        this.userDAO = new UserDAO();
        this.authService = authService;
        this.parentCardLayout = parentCardLayout;
        this.parentCardPanel = parentCardPanel;

        setBackground(new Color(245, 247, 250));
        setLayout(new BorderLayout());

        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create user info panel (top right)
        JPanel userInfoPanel = createUserInfoToolbar();
        add(userInfoPanel, BorderLayout.NORTH);

        // Create main content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245, 247, 250));

        // Add different panels
        contentPanel.add(createOverviewPanel(), "overview");
        contentPanel.add(createUsersPanel(), "users");
        contentPanel.add(createCarsPanel(), "cars");
        contentPanel.add(createBookingsPanel(), "bookings");
        contentPanel.add(createReportsPanel(), "reports");

        add(contentPanel, BorderLayout.CENTER);

        // Show overview by default
        cardLayout.show(contentPanel, "overview");

        // Initialize keyboard shortcuts
        initializeKeyboardShortcuts();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(52, 73, 94));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel title = new JLabel("Admin Panel");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);
        sidebar.add(Box.createVerticalStrut(30));

        String[] menuItems = {"Overview", "Users", "Cars", "Bookings", "Reports"};
        String[] cardNames = {"overview", "users", "cars", "bookings", "reports"};

        for (int i = 0; i < menuItems.length; i++) {
            final String cardName = cardNames[i];
            JButton button = new JButton(menuItems[i]);
            button.setBackground(new Color(41, 128, 185));
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

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("Admin Dashboard Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Statistics cards
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createStatCard("Total Users", "Loading...", new Color(52, 152, 219)), gbc);
        gbc.gridx = 1;
        panel.add(createStatCard("Total Cars", "Loading...", new Color(46, 204, 113)), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(createStatCard("Active Bookings", "Loading...", new Color(230, 126, 34)), gbc);
        gbc.gridx = 1;
        panel.add(createStatCard("Available Cars", "Loading...", new Color(155, 89, 182)), gbc);

        // Load statistics
        loadStatistics(panel);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        card.setPreferredSize(new Dimension(200, 100));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(127, 140, 141));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadStatistics(JPanel overviewPanel) {
        SwingWorker<int[], Void> worker = new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                int totalUsers = 0;
                int totalCars = 0;
                int activeBookings = 0;
                int availableCars = 0;
                try {
                    // Fetch users
                    java.util.List<User> users = userDAO.getAllUsers();
                    totalUsers = users.size();
                    // Fetch cars
                    java.util.List<Car> cars = carDAO.getAllCars();
                    totalCars = cars.size();
                    // Available cars
                    java.util.List<Car> available = carDAO.getAvailableCars();
                    availableCars = available.size();
                    // Fetch bookings
                    java.util.List<Booking> bookings = bookingDAO.getAllBookings();
                    activeBookings = (int) bookings.stream().filter(b -> b.getStatus() != null && b.getStatus().toLowerCase().contains("active")).count();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return new int[]{totalUsers, totalCars, activeBookings, availableCars};
            }

            @Override
            protected void done() {
                try {
                    int[] stats = get();
                    Component[] components = overviewPanel.getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JPanel) {
                            JPanel card = (JPanel) comp;
                            if (card.getComponentCount() >= 2) {
                                Component valueComp = card.getComponent(1);
                                if (valueComp instanceof JLabel) {
                                    JLabel titleComp = (JLabel) card.getComponent(0);
                                    String title = titleComp.getText();
                                    JLabel valueLabel = (JLabel) valueComp;
                                    switch (title) {
                                        case "Total Users":
                                            valueLabel.setText(String.valueOf(stats[0]));
                                            break;
                                        case "Total Cars":
                                            valueLabel.setText(String.valueOf(stats[1]));
                                            break;
                                        case "Active Bookings":
                                            valueLabel.setText(String.valueOf(stats[2]));
                                            break;
                                        case "Available Cars":
                                            valueLabel.setText(String.valueOf(stats[3]));
                                            break;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with title and search
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(245, 247, 250));
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(searchPanel);
        panel.add(header, BorderLayout.NORTH);

        // Users table
        String[] columnNames = {"ID", "Username", "Role", "Organization", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);

        // Enable filtering
        TableRowSorter<DefaultTableModel> userSorter = new TableRowSorter<>(model);
        table.setRowSorter(userSorter);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String text = searchField.getText();
                if (text == null || text.isBlank()) {
                    userSorter.setRowFilter(null);
                } else {
                    userSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel for add/delete
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addButton = new JButton("Add User");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddUserDialog(model));

        JButton deleteButton = new JButton("Delete User");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int userId = (int) model.getValueAt(selectedRow, 0);
                String username = (String) model.getValueAt(selectedRow, 1);
                int confirm = JOptionPane.showConfirmDialog(panel, "Delete user '" + username + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (userDAO.deleteUser(userId)) {
                        Toast.success(panel, "User deleted successfully.");
                        model.removeRow(selectedRow);
                    } else {
                        Toast.error(panel, "Failed to delete user.");
                    }
                }
            } else {
                Toast.info(panel, "Select a user to delete.");
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load users from database
        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() {
                return userDAO.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    model.setRowCount(0);
                    for (User user : users) {
                        model.addRow(new Object[]{
                            user.getId(),
                            user.getUsername(),
                            user.getRole(),
                            user.getOrganization() != null ? user.getOrganization() : "-",
                            formatDate(user.getCreatedAt())
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading users: " + e.getMessage());
                }
            }
        };
        worker.execute();

        return panel;
    }

    // Dialog for adding a new user
    private void showAddUserDialog(DefaultTableModel model) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"user", "admin"});
        JTextField orgField = new JTextField();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);
        panel.add(new JLabel("Organization:"));
        panel.add(orgField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleBox.getSelectedItem();
            String org = orgField.getText().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.error(this, "Username and password required.");
                return;
            }
            if (userDAO.register(username, password, role, org)) {
                Toast.success(this, "User added successfully.");
                // Reload users
                SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
                    @Override
                    protected List<User> doInBackground() {
                        return userDAO.getAllUsers();
                    }

                    @Override
                    protected void done() {
                        try {
                            List<User> users = get();
                            model.setRowCount(0);
                            for (User user : users) {
                                model.addRow(new Object[]{
                                    user.getId(),
                                    user.getUsername(),
                                    user.getRole(),
                                    user.getCreatedAt()
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                worker.execute();
            } else {
                Toast.error(this, "Failed to add user: " + userDAO.getLastError());
            }
        }
    }

    private JPanel createCarsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with title and search
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("Car Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(245, 247, 250));
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(searchPanel);
        panel.add(header, BorderLayout.NORTH);

        // Cars table
        String[] columnNames = {"ID", "Make", "Model", "Year", "License Plate", "Status", "Price/Day"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);

        // Enable filtering
        TableRowSorter<DefaultTableModel> carSorter = new TableRowSorter<>(model);
        table.setRowSorter(carSorter);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String text = searchField.getText();
                if (text == null || text.isBlank()) {
                    carSorter.setRowFilter(null);
                } else {
                    carSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton addButton = new JButton("Add Car");
        JButton editButton = new JButton("Edit Car");
        JButton deleteButton = new JButton("Delete Car");
        JButton refreshButton = new JButton("Refresh");

        addButton.setBackground(new Color(46, 204, 113));
        editButton.setBackground(new Color(52, 152, 219));
        deleteButton.setBackground(new Color(231, 76, 60));
        refreshButton.setBackground(new Color(149, 165, 166));

        addButton.setForeground(Color.WHITE);
        editButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setForeground(Color.WHITE);

        addButton.setFocusPainted(false);
        editButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);
        refreshButton.setFocusPainted(false);

        // Add Car
        addButton.addActionListener(e -> showAddCarDialog(model));

        // Edit Car
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int carId = (int) model.getValueAt(selectedRow, 0);
                Car car = carDAO.getById(carId);
                if (car != null) {
                    showEditCarDialog(model, car, selectedRow);
                } else {
                    Toast.error(panel, "Car not found.");
                }
            } else {
                Toast.info(panel, "Select a car to edit.");
            }
        });

        // Delete Car
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int carId = (int) model.getValueAt(selectedRow, 0);
                String carName = model.getValueAt(selectedRow, 1) + " " + model.getValueAt(selectedRow, 2);
                int confirm = JOptionPane.showConfirmDialog(panel, "Delete car '" + carName + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (carDAO.deleteCar(carId)) {
                        Toast.success(panel, "Car deleted successfully.");
                        model.removeRow(selectedRow);
                    } else {
                        Toast.error(panel, "Failed to delete car.");
                    }
                }
            } else {
                Toast.info(panel, "Select a car to delete.");
            }
        });

        // Refresh
        refreshButton.addActionListener(e -> loadCarsData(model));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data from CarDAO
        loadCarsData(model);

        return panel;
    }

    // Dialog for adding a new car
    private void showAddCarDialog(DefaultTableModel model) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField makeField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField licenseField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"available", "rented", "maintenance"});
        JTextField specsField = new JTextField();
        JTextField priceField = new JTextField();
        panel.add(new JLabel("Make:"));
        panel.add(makeField);
        panel.add(new JLabel("Model:"));
        panel.add(modelField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("License Plate:"));
        panel.add(licenseField);
        panel.add(new JLabel("Status:"));
        panel.add(statusBox);
        panel.add(new JLabel("Specs:"));
        panel.add(specsField);
        panel.add(new JLabel("Price/Day:"));
        panel.add(priceField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Car", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate basic required fields
                String make = makeField.getText().trim();
                String modelName = modelField.getText().trim();

                if (make.isEmpty() || modelName.isEmpty()) {
                    Toast.error(this, "Make and model cannot be empty");
                    return;
                }

                // Parse year with validation
                int year;
                try {
                    year = Integer.parseInt(yearField.getText().trim());
                    if (year < 1900 || year > 2100) {
                        Toast.error(this, "Year must be between 1900 and 2100");
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.error(this, "Invalid year format. Please enter a valid number.");
                    return;
                }

                String license = licenseField.getText().trim();
                String status = (String) statusBox.getSelectedItem();
                String specs = specsField.getText().trim();

                // Price handling with special validation for currency symbols and formatting
                String priceText = priceField.getText().trim();

                // Handle empty price field
                if (priceText.isEmpty()) {
                    priceText = "0.00";
                }

                // Clean the price text by removing currency symbols and other non-numeric characters
                // except decimal point and digits
                priceText = priceText.replaceAll("[^0-9.]", "");

                // Parse price with proper error handling
                java.math.BigDecimal price;
                try {
                    price = new java.math.BigDecimal(priceText);
                } catch (NumberFormatException e) {
                    Toast.error(this, "Invalid price format. Please enter numbers only (e.g., 50.00)");
                    return;
                }

                Car car = new Car(0, make, modelName, year, license, status, specs, price);
                if (carDAO.addCar(car)) {
                    Toast.success(this, "Car added successfully.");
                    loadCarsData(model);
                } else {
                    Toast.error(this, "Failed to add car.");
                }
            } catch (Exception ex) {
                Toast.error(this, "Invalid input: " + ex.getMessage());
            }
        }
    }

    // Dialog for editing a car
    private void showEditCarDialog(DefaultTableModel model, Car car, int row) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField makeField = new JTextField(car.getMake());
        JTextField modelField = new JTextField(car.getModel());
        JTextField yearField = new JTextField(String.valueOf(car.getYear()));
        JTextField licenseField = new JTextField(car.getLicensePlate());
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"available", "rented", "maintenance"});
        statusBox.setSelectedItem(car.getStatus());
        JTextField specsField = new JTextField(car.getSpecs());
        JTextField priceField = new JTextField(car.getPricePerDay().toString());
        panel.add(new JLabel("Make:"));
        panel.add(makeField);
        panel.add(new JLabel("Model:"));
        panel.add(modelField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("License Plate:"));
        panel.add(licenseField);
        panel.add(new JLabel("Status:"));
        panel.add(statusBox);
        panel.add(new JLabel("Specs:"));
        panel.add(specsField);
        panel.add(new JLabel("Price/Day:"));
        panel.add(priceField);
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Car", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate basic required fields
                String make = makeField.getText().trim();
                String modelName = modelField.getText().trim();

                if (make.isEmpty() || modelName.isEmpty()) {
                    Toast.error(this, "Make and model cannot be empty");
                    return;
                }

                // Parse year with validation
                int year;
                try {
                    year = Integer.parseInt(yearField.getText().trim());
                    if (year < 1900 || year > 2100) {
                        Toast.error(this, "Year must be between 1900 and 2100");
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.error(this, "Invalid year format. Please enter a valid number.");
                    return;
                }

                String license = licenseField.getText().trim();
                String status = (String) statusBox.getSelectedItem();
                String specs = specsField.getText().trim();

                // Price handling with special validation for currency symbols and formatting
                String priceText = priceField.getText().trim();

                // Handle empty price field
                if (priceText.isEmpty()) {
                    priceText = "0.00";
                }

                // Clean the price text by removing currency symbols and other non-numeric characters
                // except decimal point and digits
                priceText = priceText.replaceAll("[^0-9.]", "");

                // Parse price with proper error handling
                java.math.BigDecimal price;
                try {
                    price = new java.math.BigDecimal(priceText);
                } catch (NumberFormatException e) {
                    Toast.error(this, "Invalid price format. Please enter numbers only (e.g., 50.00)");
                    return;
                }

                Car updatedCar = new Car(car.getId(), make, modelName, year, license, status, specs, price);
                if (carDAO.updateCar(updatedCar)) {
                    Toast.success(this, "Car updated successfully.");
                    loadCarsData(model);
                } else {
                    Toast.error(this, "Failed to update car.");
                }
            } catch (Exception ex) {
                Toast.error(this, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void loadCarsData(DefaultTableModel model) {
        SwingWorker<List<Car>, Void> worker = new SwingWorker<List<Car>, Void>() {
            @Override
            protected List<Car> doInBackground() throws Exception {
                return carDAO.getAllCars();
            }

            @Override
            protected void done() {
                try {
                    List<Car> cars = get();
                    model.setRowCount(0); // Clear existing data
                    for (Car car : cars) {
                        model.addRow(new Object[]{
                            car.getId(),
                            car.getMake(),
                            car.getModel(),
                            car.getYear(),
                            car.getLicensePlate(),
                            car.getStatus(),
                            "₹" + formatMoney(car.getPricePerDay())
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

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with title and refresh
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(245, 247, 250));
        JLabel title = new JLabel("Booking Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        header.add(title, BorderLayout.WEST);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(149, 165, 166));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        // model not yet defined here; listener is added after model creation
        header.add(refreshBtn, BorderLayout.EAST);
        panel.add(header, BorderLayout.NORTH);

        // Bookings table
        String[] columnNames = {"ID", "User ID", "Car ID", "Start Date", "End Date", "Status", "Total Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Wire refresh now that model exists
        refreshBtn.addActionListener(e -> loadBookingsData(model));
        // Load bookings data
        loadBookingsData(model);

        return panel;
    }

    // Utilities
    private String formatDate(java.time.LocalDateTime dt) {
        if (dt == null) {
            return "-";
        }
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy, HH:mm");
        return dt.format(fmt);
    }

    private String formatMoney(java.math.BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return new java.text.DecimalFormat("#,##0.00").format(amount);
    }

    private void loadBookingsData(DefaultTableModel model) {
        SwingWorker<List<Booking>, Void> worker = new SwingWorker<List<Booking>, Void>() {
            @Override
            protected List<Booking> doInBackground() throws Exception {
                return bookingDAO.getAllBookings();
            }

            @Override
            protected void done() {
                try {
                    List<Booking> bookings = get();
                    model.setRowCount(0);
                    for (Booking booking : bookings) {
                        model.addRow(new Object[]{
                            booking.getId(),
                            booking.getUserId(),
                            booking.getCarId(),
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

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("Future AI/NLP analytics integration point");
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        subtitle.setForeground(new Color(127, 140, 141));
        gbc.gridy++;
        panel.add(subtitle, gbc);

        // Report buttons
        gbc.gridy++;
        JButton monthlyReport = new JButton("Monthly Revenue Report");
        monthlyReport.setBackground(new Color(52, 152, 219));
        monthlyReport.setForeground(Color.WHITE);
        monthlyReport.setFocusPainted(false);
        panel.add(monthlyReport, gbc);

        gbc.gridy++;
        JButton carUtilization = new JButton("Car Utilization Report");
        carUtilization.setBackground(new Color(46, 204, 113));
        carUtilization.setForeground(Color.WHITE);
        carUtilization.setFocusPainted(false);
        panel.add(carUtilization, gbc);

        gbc.gridy++;
        JButton userActivity = new JButton("User Activity Report");
        userActivity.setBackground(new Color(155, 89, 182));
        userActivity.setForeground(Color.WHITE);
        userActivity.setFocusPainted(false);
        panel.add(userActivity, gbc);

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

    private JPanel createUserInfoToolbar() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        panel.setOpaque(false);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshBtn.addActionListener(e -> rebuildUserInfo(panel));

        rebuildUserInfo(panel);

        container.add(panel, BorderLayout.CENTER);
        container.add(refreshBtn, BorderLayout.EAST);
        return container;
    }

    private void rebuildUserInfo(JPanel target) {
        target.removeAll();
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

        JLabel nameLabel = new JLabel(username);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(52, 73, 94));

        String resourceName = "/icons/" + ("admin".equalsIgnoreCase(role) ? "Admin.png" : "User.png");
        ImageIcon icon = null;
        java.net.URL resourceUrl = getClass().getResource(resourceName);
        if (resourceUrl != null) {
            icon = new ImageIcon(resourceUrl);
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
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setToolTipText(role.substring(0, 1).toUpperCase() + role.substring(1));

        target.add(iconLabel);
        target.add(nameLabel);
        target.revalidate();
        target.repaint();
    }
}
