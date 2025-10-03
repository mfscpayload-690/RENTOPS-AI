package ui;

import dao.CarDAO;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import models.Car;
import ui.components.CarDetailsDialog;
import ui.components.CarFormDialog;

public class CarsPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable carsTable;

    public CarsPanel() {
        setBackground(new Color(235, 240, 245));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Car Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(52, 73, 94));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Car");
        JButton editButton = new JButton("Edit Car");
        JButton viewButton = new JButton("View Details");
        JButton deleteButton = new JButton("Delete Car");
        JButton refreshButton = new JButton("Refresh");

        addButton.setPreferredSize(new Dimension(100, 35));
        editButton.setPreferredSize(new Dimension(100, 35));
        viewButton.setPreferredSize(new Dimension(120, 35));
        deleteButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.setPreferredSize(new Dimension(100, 35));

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(refreshButton);

        headerPanel.add(buttonsPanel, BorderLayout.EAST);

        // Table setup
        String[] columnNames = {"ID", "Make", "Model", "Year", "License Plate", "Status", "Price/Day", "KM Driven"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        carsTable = new JTable(tableModel);
        carsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carsTable.setRowHeight(25);
        carsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(carsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Cars List"));

        // Add components
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        addButton.addActionListener(e -> showAddCarDialog());
        editButton.addActionListener(e -> showEditCarDialog());
        viewButton.addActionListener(e -> showCarDetailsDialog());
        deleteButton.addActionListener(e -> deleteSelectedCar());
        refreshButton.addActionListener(e -> loadCarsData());

        // Load initial data
        loadCarsData();
    }

    private void showAddCarDialog() {
        CarFormDialog dialog = new CarFormDialog(this, "Add New Car", null);
        dialog.setVisible(true);

        if (dialog.isApproved()) {
            Car newCar = dialog.getResult();
            try {
                CarDAO dao = new CarDAO();

                // Get the temp image paths before saving
                List<String> tempExteriorPaths = new ArrayList<>(newCar.getExteriorImages());
                List<String> tempInteriorPaths = new ArrayList<>(newCar.getInteriorImages());

                // Clear images from car object temporarily (since we don't have car ID yet)
                newCar.setExteriorImages(new ArrayList<>());
                newCar.setInteriorImages(new ArrayList<>());

                // Add car to database first to get the car ID
                dao.addCar(newCar);

                // Get the newly created car with ID
                List<Car> allCars = dao.getAllCars();
                Car savedCar = null;
                for (Car car : allCars) {
                    if (car.getMake().equals(newCar.getMake())
                            && car.getModel().equals(newCar.getModel())
                            && car.getLicensePlate().equals(newCar.getLicensePlate())) {
                        savedCar = car;
                        break;
                    }
                }

                if (savedCar != null && (!tempExteriorPaths.isEmpty() || !tempInteriorPaths.isEmpty())) {
                    // Save images to proper directory using car ID
                    List<String> finalExteriorPaths = new ArrayList<>();
                    List<String> finalInteriorPaths = new ArrayList<>();

                    // Save exterior images
                    for (int i = 0; i < tempExteriorPaths.size(); i++) {
                        String tempPath = tempExteriorPaths.get(i);
                        String savedPath = utils.ImageUtils.saveCarImage(new java.io.File(tempPath), savedCar.getId(), "exterior", i);
                        if (savedPath != null) {
                            finalExteriorPaths.add(savedPath);
                        }
                    }

                    // Save interior images
                    for (int i = 0; i < tempInteriorPaths.size(); i++) {
                        String tempPath = tempInteriorPaths.get(i);
                        String savedPath = utils.ImageUtils.saveCarImage(new java.io.File(tempPath), savedCar.getId(), "interior", i);
                        if (savedPath != null) {
                            finalInteriorPaths.add(savedPath);
                        }
                    }

                    // Update car record with final image paths
                    savedCar.setExteriorImages(finalExteriorPaths);
                    savedCar.setInteriorImages(finalInteriorPaths);
                    dao.updateCar(savedCar);
                }

                loadCarsData(); // Refresh table
                JOptionPane.showMessageDialog(this, "Car added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to add car: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void showEditCarDialog() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int carId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            CarDAO dao = new CarDAO();
            Car car = dao.getById(carId);

            CarFormDialog dialog = new CarFormDialog(this, "Edit Car", car);
            dialog.setVisible(true);

            if (dialog.isApproved()) {
                Car updatedCar = dialog.getResult();

                // Get the temp image paths
                List<String> tempExteriorPaths = new ArrayList<>(updatedCar.getExteriorImages());
                List<String> tempInteriorPaths = new ArrayList<>(updatedCar.getInteriorImages());

                // Save images to proper directory
                List<String> finalExteriorPaths = new ArrayList<>();
                List<String> finalInteriorPaths = new ArrayList<>();

                // Save exterior images
                for (int i = 0; i < tempExteriorPaths.size(); i++) {
                    String tempPath = tempExteriorPaths.get(i);
                    // Check if it's already a saved path (starts with ui/components/images)
                    if (tempPath.contains("ui" + java.io.File.separator + "components" + java.io.File.separator + "images")) {
                        finalExteriorPaths.add(tempPath);
                    } else {
                        // It's a new file that needs to be saved
                        String savedPath = utils.ImageUtils.saveCarImage(new java.io.File(tempPath), updatedCar.getId(), "exterior", i);
                        if (savedPath != null) {
                            finalExteriorPaths.add(savedPath);
                        }
                    }
                }

                // Save interior images
                for (int i = 0; i < tempInteriorPaths.size(); i++) {
                    String tempPath = tempInteriorPaths.get(i);
                    // Check if it's already a saved path
                    if (tempPath.contains("ui" + java.io.File.separator + "components" + java.io.File.separator + "images")) {
                        finalInteriorPaths.add(tempPath);
                    } else {
                        // It's a new file that needs to be saved
                        String savedPath = utils.ImageUtils.saveCarImage(new java.io.File(tempPath), updatedCar.getId(), "interior", i);
                        if (savedPath != null) {
                            finalInteriorPaths.add(savedPath);
                        }
                    }
                }

                // Update car with final image paths
                updatedCar.setExteriorImages(finalExteriorPaths);
                updatedCar.setInteriorImages(finalInteriorPaths);

                dao.updateCar(updatedCar);
                loadCarsData(); // Refresh table
                JOptionPane.showMessageDialog(this, "Car updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to edit car: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCarDetailsDialog() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int carId = (Integer) tableModel.getValueAt(selectedRow, 0);
        try {
            CarDAO dao = new CarDAO();
            Car car = dao.getById(carId);

            CarDetailsDialog dialog = new CarDetailsDialog(this, car);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load car details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedCar() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int carId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String carInfo = tableModel.getValueAt(selectedRow, 1) + " " + tableModel.getValueAt(selectedRow, 2);

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + carInfo + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                CarDAO dao = new CarDAO();
                dao.deleteCar(carId);
                loadCarsData(); // Refresh table
                JOptionPane.showMessageDialog(this, "Car deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to delete car: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadCarsData() {
        try {
            CarDAO dao = new CarDAO();
            List<Car> cars = dao.getAllCars();

            // Clear existing data
            tableModel.setRowCount(0);

            // Add cars to table
            for (Car car : cars) {
                Object[] rowData = {
                    car.getId(),
                    car.getMake(),
                    car.getModel(),
                    car.getYear(),
                    car.getLicensePlate(),
                    car.getStatus(),
                    "₹" + car.getPricePerDay(),
                    car.getTotalKmDriven() + " km"
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load cars: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
