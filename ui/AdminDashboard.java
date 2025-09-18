package ui;

import dao.CarDAO;
import dao.BookingDAO;
import dao.UserDAO;
import models.Car;
import models.Booking;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    public AdminDashboard() {
        this.carDAO = new CarDAO();
        this.bookingDAO = new BookingDAO();
        this.userDAO = new UserDAO();
        
        setBackground(new Color(245,247,250));
        setLayout(new BorderLayout());

        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create main content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(245,247,250));
        
        // Add different panels
        contentPanel.add(createOverviewPanel(), "overview");
        contentPanel.add(createUsersPanel(), "users");
        contentPanel.add(createCarsPanel(), "cars");
        contentPanel.add(createBookingsPanel(), "bookings");
        contentPanel.add(createReportsPanel(), "reports");
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Show overview by default
        cardLayout.show(contentPanel, "overview");
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

        return sidebar;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245,247,250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("Admin Dashboard Overview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
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
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // This would typically load from database
                return null;
            }

            @Override
            protected void done() {
                // Update the statistics (simplified for now)
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
                                    case "Total Users": valueLabel.setText("25"); break;
                                    case "Total Cars": valueLabel.setText("15"); break;
                                    case "Active Bookings": valueLabel.setText("8"); break;
                                    case "Available Cars": valueLabel.setText("7"); break;
                                }
                            }
                        }
                    }
                }
            }
        };
        worker.execute();
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245,247,250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        panel.add(title, BorderLayout.NORTH);

        // Users table
        String[] columnNames = {"ID", "Username", "Role", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load users (simplified)
        model.addRow(new Object[]{"1", "admin", "admin", "2023-01-01"});
        model.addRow(new Object[]{"2", "john_doe", "user", "2023-01-15"});
        model.addRow(new Object[]{"3", "jane_smith", "user", "2023-02-01"});

        return panel;
    }

    private JPanel createCarsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245,247,250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Car Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        panel.add(title, BorderLayout.NORTH);

        // Cars table
        String[] columnNames = {"ID", "Make", "Model", "Year", "License Plate", "Status", "Price/Day"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(245,247,250));
        
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
        
        refreshButton.addActionListener(e -> loadCarsData(model));
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        loadCarsData(model);

        return panel;
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
                            "$" + car.getPricePerDay()
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
        panel.setBackground(new Color(245,247,250));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Booking Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        panel.add(title, BorderLayout.NORTH);

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

        // Load bookings data
        loadBookingsData(model);

        return panel;
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
                            "$" + booking.getTotalPrice()
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
        panel.setBackground(new Color(245,247,250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(52, 73, 94));
        gbc.gridx = 0; gbc.gridy = 0;
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
}
