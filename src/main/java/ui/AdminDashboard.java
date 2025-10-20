package ui;

import dao.CarDAO;
import dao.BookingDAO;
import dao.UserDAO;
import models.Car;
import models.Booking;
import models.User;
import ui.components.*;
import utils.ModernTheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminDashboard extends JPanel {

    private CarDAO carDAO;
    private BookingDAO bookingDAO;
    private services.BookingService bookingService;
    private UserDAO userDAO;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private services.AuthService authService;
    private CardLayout parentCardLayout;
    private JPanel parentCardPanel;
    // Top stats cards (kept as fields to refresh on demand)
    private StatCard pendingApprovalsStatCard;
    private StatCard usersStatCard;
    private StatCard carsStatCard;
    private StatCard activeBookingsStatCard;
    private StatCard availableCarsStatCard;
    private StatCard revenueStatCard;

    public AdminDashboard() {
        this(null, null, null);
    }

    public AdminDashboard(services.AuthService authService, CardLayout parentCardLayout, JPanel parentCardPanel) {
        this.carDAO = new CarDAO();
        this.bookingDAO = new BookingDAO();
        this.bookingService = new services.BookingService();
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

        String[] menuItems = {"Overview", "Users", "Cars", "Bookings"};
        String[] cardNames = {"overview", "users", "cars", "bookings"};

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = ModernTheme.createHeaderPanel(
            "Dashboard Overview",
            "Real-time statistics and system insights"
        );
        panel.add(header, BorderLayout.NORTH);



    // Main grid panel (2 rows x 3 cols)
    JPanel mainGrid = new JPanel(new GridBagLayout());
    mainGrid.setBackground(ModernTheme.BG_DARK);
    mainGrid.setBorder(BorderFactory.createEmptyBorder(24, 0, 20, 0));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(12, 12, 12, 12);
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;

    usersStatCard = new StatCard("Total Users", "Loading...", ModernTheme.ACCENT_BLUE);
    carsStatCard = new StatCard("Total Cars", "Loading...", ModernTheme.SUCCESS_GREEN);
    activeBookingsStatCard = new StatCard("Active Bookings", "Loading...", ModernTheme.ACCENT_ORANGE);
    availableCarsStatCard = new StatCard("Available Cars", "Loading...", new Color(155, 89, 182));
    revenueStatCard = new StatCard("Total Revenue", "Loading...", ModernTheme.ACCENT_BLUE_DARK);
    pendingApprovalsStatCard = new StatCard("Pending Approvals", "Loading...", ModernTheme.WARNING_YELLOW);

    gbc.gridx = 0; gbc.gridy = 0;
    mainGrid.add(usersStatCard, gbc);
    gbc.gridx = 1;
    mainGrid.add(carsStatCard, gbc);
    gbc.gridx = 2;
    mainGrid.add(activeBookingsStatCard, gbc);
    gbc.gridx = 0; gbc.gridy = 1;
    mainGrid.add(availableCarsStatCard, gbc);
    gbc.gridx = 1;
    mainGrid.add(revenueStatCard, gbc);
    gbc.gridx = 2;
    mainGrid.add(pendingApprovalsStatCard, gbc);

    panel.add(mainGrid, BorderLayout.CENTER);

    // Load statistics and chart asynchronously
    refreshTopStats();

    // Periodically refresh top stats (including chart) to reflect new bookings
    Timer periodic = new Timer(15_000, e -> refreshTopStats());
    periodic.setRepeats(true);
    periodic.start();

        return panel;
    }

    // New method to load statistics with modern cards
    private void loadStatisticsWithCards(StatCard usersCard, StatCard carsCard, StatCard bookingsCard, StatCard availableCard, StatCard revenueCard, StatCard pendingCard) {
        SwingWorker<Object[], Void> worker = new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() {
                int totalUsers = 0;
                int totalCars = 0;
                int activeBookings = 0;
                int availableCars = 0;
                int pendingApprovals = 0;
                java.math.BigDecimal totalRevenue = java.math.BigDecimal.ZERO;
                try {
                    java.util.List<User> users = userDAO.getAllUsers();
                    totalUsers = users.size();
                    java.util.List<Car> cars = carDAO.getAllCars();
                    totalCars = cars.size();
                    java.util.List<Car> available = carDAO.getAvailableCars();
                    availableCars = available.size();
                    java.util.List<Booking> bookings = bookingDAO.getAllBookings();
                    activeBookings = (int) bookings.stream()
                        .filter(b -> b.getStatus() != null && 
                                   (b.getStatus().equalsIgnoreCase("active") || 
                                    b.getStatus().equalsIgnoreCase("approved")))
                        .count();
                    pendingApprovals = (int) bookings.stream()
                        .filter(b -> b.getStatus() != null && b.getStatus().equalsIgnoreCase("pending"))
                        .count();
                    totalRevenue = bookingDAO.getTotalRevenue();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return new Object[]{totalUsers, totalCars, activeBookings, availableCars, totalRevenue, pendingApprovals};
            }

            @Override
            protected void done() {
                try {
                    Object[] stats = get();
                    usersCard.updateValue(String.valueOf((int) stats[0]), null);
                    carsCard.updateValue(String.valueOf((int) stats[1]), null);
                    bookingsCard.updateValue(String.valueOf((int) stats[2]), null);
                    availableCard.updateValue(String.valueOf((int) stats[3]), null);
                    java.math.BigDecimal rev = (java.math.BigDecimal) stats[4];
                    revenueCard.updateValue("₹" + new java.text.DecimalFormat("#,##0.00").format(rev), null);
                    pendingCard.updateValue(String.valueOf((int) stats[5]), null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void refreshTopStats() {
        if (usersStatCard != null && carsStatCard != null && activeBookingsStatCard != null && availableCarsStatCard != null && revenueStatCard != null && pendingApprovalsStatCard != null) {
            loadStatisticsWithCards(usersStatCard, carsStatCard, activeBookingsStatCard, availableCarsStatCard, revenueStatCard, pendingApprovalsStatCard);
        }
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with title and toolbar
        JPanel headerPanel = ModernTheme.createHeaderPanel("User Management", "Manage users, roles, and permissions");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Center panel with search and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ModernTheme.BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Search bar
        JPanel searchBar = ModernTheme.createSearchBar("Search users by name, role, or organization...");
        JTextField searchField = ModernTheme.getSearchField(searchBar);
        centerPanel.add(searchBar, BorderLayout.NORTH);

        // Modern table with StatusBadge for roles
        String[] columnNames = {"ID", "User Code", "Username", "Role", "Organization", "Created At"};
        ModernTable modernTable = new ModernTable(columnNames);
        JTable table = modernTable.getTable();
        DefaultTableModel model = modernTable.getModel();

        // Custom renderer for Role column to show StatusBadge
        table.getColumnModel().getColumn(3).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(true);
                
                if (isSelected) {
                    panel.setBackground(new Color(64, 150, 255, 50));
                } else {
                    panel.setBackground(row % 2 == 0 ? new Color(30, 30, 30) : new Color(35, 35, 35));
                }
                
                String role = value != null ? value.toString() : "";
                StatusBadge badge = role.equalsIgnoreCase("admin") 
                    ? StatusBadge.error(role) 
                    : StatusBadge.primary(role);
                panel.add(badge);
                
                return panel;
            }
        });

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

        JScrollPane scrollPane = new JScrollPane(modernTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        scrollPane.setBackground(ModernTheme.BG_DARK);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Modern button toolbar
        JPanel toolbar = ModernTheme.createToolbar();
        
        ActionButton addButton = new ActionButton("Add User", ActionButton.Type.SUCCESS);
        addButton.addActionListener(e -> showAddUserDialog(model));

        ActionButton editButton = new ActionButton("Edit User", ActionButton.Type.SECONDARY);
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                Toast.info(panel, "Edit user functionality coming soon!");
            } else {
                Toast.info(panel, "Select a user to edit.");
            }
        });

        ActionButton deleteButton = new ActionButton("Delete User", ActionButton.Type.DANGER);
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int userId = (int) model.getValueAt(table.convertRowIndexToModel(selectedRow), 0);
                String username = (String) model.getValueAt(table.convertRowIndexToModel(selectedRow), 2);
                int confirm = JOptionPane.showConfirmDialog(panel, "Delete user '" + username + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (userDAO.deleteUser(userId)) {
                        Toast.success(panel, "User deleted successfully.");
                        modernTable.removeSelectedRow();
                    } else {
                        Toast.error(panel, "Failed to delete user.");
                    }
                }
            } else {
                Toast.info(panel, "Select a user to delete.");
            }
        });

        ActionButton refreshButton = new ActionButton("Refresh", ActionButton.Type.GHOST);
        refreshButton.addActionListener(e -> {
            refreshButton.setLoading(true);
            loadUsersData(model, refreshButton);
        });

        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(refreshButton);
        panel.add(toolbar, BorderLayout.SOUTH);

        // Load users from database
        loadUsersData(model, refreshButton);

        return panel;
    }

    // Helper method to load users data
    private void loadUsersData(DefaultTableModel model, ActionButton refreshButton) {
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
                        String userCode = utils.DisplayCodeUtil.codeFromName(user.getUsername());
                        model.addRow(new Object[]{
                            user.getId(),
                            userCode,
                            user.getUsername(),
                            user.getRole(),
                            user.getOrganization() != null ? user.getOrganization() : "-",
                            formatDate(user.getCreatedAt())
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading users: " + e.getMessage());
                } finally {
                    if (refreshButton != null) {
                        refreshButton.setLoading(false);
                    }
                }
            }
        };
        worker.execute();
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
        panel.setBackground(ModernTheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with title and subtitle
        JPanel headerPanel = ModernTheme.createHeaderPanel("Car Management", "Manage your fleet, availability, and pricing");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Center panel with search and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ModernTheme.BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Search bar
        JPanel searchBar = ModernTheme.createSearchBar("Search cars by make, model, or license plate...");
        JTextField searchField = ModernTheme.getSearchField(searchBar);
        centerPanel.add(searchBar, BorderLayout.NORTH);

        // Modern table with StatusBadge for car status
        String[] columnNames = {"ID", "Car Code", "Make", "Model", "Year", "License Plate", "Status", "Total KM", "Price/Day"};
        ModernTable modernTable = new ModernTable(columnNames);
        JTable table = modernTable.getTable();
        DefaultTableModel model = modernTable.getModel();

        // Custom renderer for Status column to show StatusBadge
        table.getColumnModel().getColumn(6).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(true);
                
                if (isSelected) {
                    panel.setBackground(new Color(64, 150, 255, 50));
                } else {
                    panel.setBackground(row % 2 == 0 ? new Color(30, 30, 30) : new Color(35, 35, 35));
                }
                
                String status = value != null ? value.toString().toLowerCase() : "";
                StatusBadge badge;
                switch (status) {
                    case "available":
                        badge = StatusBadge.success(value.toString());
                        break;
                    case "rented":
                        badge = StatusBadge.warning(value.toString());
                        break;
                    case "maintenance":
                        badge = StatusBadge.error(value.toString());
                        break;
                    default:
                        badge = StatusBadge.neutral(value.toString());
                }
                panel.add(badge);
                
                return panel;
            }
        });

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

        JScrollPane scrollPane = new JScrollPane(modernTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        scrollPane.setBackground(ModernTheme.BG_DARK);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Add double-click listener to show car details dialog
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        row = table.convertRowIndexToModel(row);
                        int carId = (int) model.getValueAt(row, 0);
                        Car car = carDAO.getById(carId);
                        if (car != null) {
                            ui.components.CarDetailsDialog dialog = new ui.components.CarDetailsDialog(table, car);
                            dialog.setVisible(true);
                        }
                    }
                }
            }
        });

        // Modern button toolbar
        JPanel toolbar = ModernTheme.createToolbar();

        ActionButton addButton = new ActionButton("Add Car", ActionButton.Type.SUCCESS);
        addButton.addActionListener(e -> showAddCarDialog(model));

        ActionButton editButton = new ActionButton("Edit Car", ActionButton.Type.SECONDARY);
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int carId = (int) model.getValueAt(table.convertRowIndexToModel(selectedRow), 0);
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

        ActionButton deleteButton = new ActionButton("Delete Car", ActionButton.Type.DANGER);
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = table.convertRowIndexToModel(selectedRow);
                int carId = (int) model.getValueAt(modelRow, 0);
                String carName = model.getValueAt(modelRow, 2) + " " + model.getValueAt(modelRow, 3);
                int confirm = JOptionPane.showConfirmDialog(panel, "Delete car '" + carName + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (carDAO.deleteCar(carId)) {
                        Toast.success(panel, "Car deleted successfully.");
                        modernTable.removeSelectedRow();
                    } else {
                        Toast.error(panel, "Failed to delete car.");
                    }
                }
            } else {
                Toast.info(panel, "Select a car to delete.");
            }
        });

        ActionButton refreshButton = new ActionButton("Refresh", ActionButton.Type.GHOST);
        refreshButton.addActionListener(e -> {
            refreshButton.setLoading(true);
            loadCarsData(model, refreshButton);
        });

        toolbar.add(addButton);
        toolbar.add(editButton);
        toolbar.add(deleteButton);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(refreshButton);
        panel.add(toolbar, BorderLayout.SOUTH);

        // Load initial data from CarDAO
        loadCarsData(model);

        return panel;
    }

    // Dialog for adding a new car
    private void showAddCarDialog(DefaultTableModel model) {
        ui.components.CarFormDialog dialog = new ui.components.CarFormDialog(this, "Add New Car", null);
        dialog.setVisible(true);

        if (dialog.isApproved() && dialog.getResult() != null) {
            Car car = dialog.getResult();
            if (carDAO.addCar(car)) {
                Toast.success(this, "Car added successfully.");
                loadCarsData(model);
            } else {
                String msg = "Failed to add car.";
                Toast.error(this, msg);
            }
        }
    }

    // Dialog for editing a car
    private void showEditCarDialog(DefaultTableModel model, Car car, int row) {
        ui.components.CarFormDialog dialog = new ui.components.CarFormDialog(this, "Edit Car", car);
        dialog.setVisible(true);

        if (dialog.isApproved() && dialog.getResult() != null) {
            Car updatedCar = dialog.getResult();
            if (carDAO.updateCar(updatedCar)) {
                Toast.success(this, "Car updated successfully.");
                loadCarsData(model);
            } else {
                String msg = "Failed to update car.";
                Toast.error(this, msg);
            }
        }
    }

    private void loadCarsData(DefaultTableModel model) {
        loadCarsData(model, null);
    }

    private void loadCarsData(DefaultTableModel model, ActionButton refreshButton) {
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
                        String carCode = utils.DisplayCodeUtil.codeFromName(car.getModel());
                        model.addRow(new Object[]{
                            car.getId(),
                            carCode,
                            car.getMake(),
                            car.getModel(),
                            car.getYear(),
                            car.getLicensePlate(),
                            car.getStatus(),
                            car.getTotalKmDriven() + " KM",
                            "₹" + formatMoney(car.getPricePerDay())
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading cars: " + e.getMessage());
                } finally {
                    if (refreshButton != null) {
                        refreshButton.setLoading(false);
                    }
                }
            }
        };
        worker.execute();
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header with title and subtitle
        JPanel headerPanel = ModernTheme.createHeaderPanel("Booking Management", "Track and manage rental bookings");
        panel.add(headerPanel, BorderLayout.NORTH);

        // Center panel with table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(ModernTheme.BG_DARK);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Modern table with StatusBadge for booking status
        String[] columnNames = {"ID", "User", "Car", "Start Date", "End Date", "Status", "Total Price"};
        ModernTable modernTable = new ModernTable(columnNames);
        JTable table = modernTable.getTable();
        DefaultTableModel model = modernTable.getModel();

        // Custom renderer for Status column to show StatusBadge
        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                panel.setOpaque(true);
                
                if (isSelected) {
                    panel.setBackground(new Color(64, 150, 255, 50));
                } else {
                    panel.setBackground(row % 2 == 0 ? new Color(30, 30, 30) : new Color(35, 35, 35));
                }
                
                String status = value != null ? value.toString().toLowerCase() : "";
                StatusBadge badge;
                switch (status) {
                    case "pending":
                        badge = StatusBadge.warning(value.toString());
                        break;
                    case "approved":
                        badge = StatusBadge.info(value.toString());
                        break;
                    case "active":
                        badge = StatusBadge.primary(value.toString());
                        break;
                    case "completed":
                        badge = StatusBadge.success(value.toString());
                        break;
                    case "cancelled":
                        badge = StatusBadge.error(value.toString());
                        break;
                    default:
                        badge = StatusBadge.neutral(value.toString());
                }
                panel.add(badge);
                
                return panel;
            }
        });

        JScrollPane scrollPane = new JScrollPane(modernTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollPane.setBackground(ModernTheme.BG_DARK);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        // Modern button toolbar
        JPanel toolbar = ModernTheme.createToolbar();

        ActionButton approveBtn = new ActionButton("Approve", ActionButton.Type.SECONDARY);
        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int modelRow = table.convertRowIndexToModel(row);
                int id = (int) model.getValueAt(modelRow, 0);
                if (bookingService.approveBooking(id)) {
                    Toast.success(panel, "Booking approved.");
                    loadBookingsData(model, null);
                    refreshTopStats();
                } else {
                    Toast.error(panel, "Failed to approve booking.");
                }
            } else {
                Toast.info(panel, "Select a booking first.");
            }
        });

        ActionButton startBtn = new ActionButton("Start", ActionButton.Type.PRIMARY);
        startBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int modelRow = table.convertRowIndexToModel(row);
                int id = (int) model.getValueAt(modelRow, 0);
                if (bookingService.startBooking(id)) {
                    Toast.success(panel, "Booking started.");
                    loadBookingsData(model, null);
                    refreshTopStats();
                } else {
                    Toast.error(panel, "Failed to start booking.");
                }
            } else {
                Toast.info(panel, "Select a booking first.");
            }
        });

        ActionButton completeBtn = new ActionButton("Complete", ActionButton.Type.SUCCESS);
        completeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int modelRow = table.convertRowIndexToModel(row);
                int id = (int) model.getValueAt(modelRow, 0);
                if (bookingService.completeBooking(id)) {
                    Toast.success(panel, "Booking completed.");
                    loadBookingsData(model, null);
                    refreshTopStats();
                } else {
                    Toast.error(panel, "Failed to complete booking.");
                }
            } else {
                Toast.info(panel, "Select a booking first.");
            }
        });

        ActionButton cancelBtn = new ActionButton("Cancel", ActionButton.Type.DANGER);
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int modelRow = table.convertRowIndexToModel(row);
                int id = (int) model.getValueAt(modelRow, 0);
                int confirm = JOptionPane.showConfirmDialog(panel, "Cancel booking #" + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (bookingService.cancelBooking(id)) {
                        Toast.success(panel, "Booking cancelled.");
                        loadBookingsData(model, null);
                        refreshTopStats();
                    } else {
                        Toast.error(panel, "Failed to cancel booking.");
                    }
                }
            } else {
                Toast.info(panel, "Select a booking first.");
            }
        });

        ActionButton refreshBtn = new ActionButton("Refresh", ActionButton.Type.GHOST);
        refreshBtn.addActionListener(e -> {
            refreshBtn.setLoading(true);
            loadBookingsData(model, refreshBtn);
            // Also refresh top stats to reflect any changes
            refreshTopStats();
        });

        toolbar.add(approveBtn);
        toolbar.add(startBtn);
        toolbar.add(completeBtn);
        toolbar.add(cancelBtn);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(refreshBtn);
        panel.add(toolbar, BorderLayout.SOUTH);

        // Load bookings data
        loadBookingsData(model, refreshBtn);

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
        loadBookingsData(model, null);
    }

    private void loadBookingsData(DefaultTableModel model, ActionButton refreshButton) {
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
                        // Enrich with readable user and car names
                        models.User u = userDAO.getById(booking.getUserId());
                        models.Car c = carDAO.getById(booking.getCarId());
                        String userName = u != null ? u.getUsername() : ("User #" + booking.getUserId());
                        String carName = c != null ? (c.getMake() + " " + c.getModel()) : ("Car #" + booking.getCarId());
                        String userCode = utils.DisplayCodeUtil.codeFromName(userName);
                        String carCode = utils.DisplayCodeUtil.codeFromName(c != null ? c.getModel() : carName);
                        model.addRow(new Object[]{
                            booking.getId(),
                            userName + " (" + userCode + ")",
                            carName + " (" + carCode + ")",
                            booking.getStartDate(),
                            booking.getEndDate(),
                            booking.getStatus(),
                            "₹" + booking.getTotalPrice()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error loading bookings: " + e.getMessage());
                } finally {
                    if (refreshButton != null) {
                        refreshButton.setLoading(false);
                    }
                }
            }
        };
        worker.execute();
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
