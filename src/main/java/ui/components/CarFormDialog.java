package ui.components;

import models.Car;
import dao.CarDAO;
import utils.ImageUtils;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CarFormDialog extends JDialog {
    private Car car;
    private JTextField makeField, modelField, yearField, priceField, licensePlateField, specsField;
    private JComboBox<String> statusCombo;
    private MultiImagePanel exteriorImagePanel;
    private MultiImagePanel interiorImagePanel;
    private boolean saved = false;
    
    public CarFormDialog(Component parent, String title, Car car) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        this.car = car;
        setSize(700, 700);
        setLocationRelativeTo(parent);
        
        // Main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Basic Info
        JPanel basicInfoPanel = createBasicInfoPanel();
        tabbedPane.addTab("Basic Info", basicInfoPanel);
        
        // Tab 2: Images
        JPanel imagesPanel = createImagesPanel();
        tabbedPane.addTab("Images", imagesPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> save());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Make:"));
        makeField = new JTextField(car != null ? car.getMake() : "");
        panel.add(makeField);
        
        panel.add(new JLabel("Model:"));
        modelField = new JTextField(car != null ? car.getModel() : "");
        panel.add(modelField);
        
        panel.add(new JLabel("Year:"));
        yearField = new JTextField(car != null ? String.valueOf(car.getYear()) : "");
        panel.add(yearField);
        
        panel.add(new JLabel("License Plate:"));
        licensePlateField = new JTextField(car != null ? car.getLicensePlate() : "");
        panel.add(licensePlateField);
        
        panel.add(new JLabel("Price/Day:"));
        priceField = new JTextField(car != null ? String.valueOf(car.getPricePerDay()) : "");
        panel.add(priceField);
        
        panel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"available", "rented", "maintenance"});
        statusCombo.setSelectedItem(car != null ? car.getStatus() : "available");
        panel.add(statusCombo);
        
        panel.add(new JLabel("Specs:"));
        specsField = new JTextField(car != null ? car.getSpecs() : "");
        panel.add(specsField);
        
        return panel;
    }
    
    private JPanel createImagesPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Exterior images
        exteriorImagePanel = new MultiImagePanel("exterior");
        if (car != null && car.getExteriorImages() != null) {
            exteriorImagePanel.setImagePaths(car.getExteriorImages());
        }
        panel.add(exteriorImagePanel);
        
        // Interior images
        interiorImagePanel = new MultiImagePanel("interior");
        if (car != null && car.getInteriorImages() != null) {
            interiorImagePanel.setImagePaths(car.getInteriorImages());
        }
        panel.add(interiorImagePanel);
        
        return panel;
    }
    
    private void save() {
        try {
            CarDAO carDAO = new CarDAO();
            if (car == null) {
                // Create new car with default values
                car = new Car(0, "", "", 0, "", "available", "", BigDecimal.ZERO);
            }
            car.setMake(makeField.getText());
            car.setModel(modelField.getText());
            car.setYear(Integer.parseInt(yearField.getText()));
            car.setLicensePlate(licensePlateField.getText());
            car.setPricePerDay(new BigDecimal(priceField.getText()));
            car.setStatus((String) statusCombo.getSelectedItem());
            car.setSpecs(specsField.getText());
            
            // Save car first to get ID
            if (car.getId() == 0) {
                carDAO.addCar(car);
            } else {
                carDAO.updateCar(car);
            }
            
            // Save images if car ID is available
            if (car.getId() > 0) {
                List<String> exteriorPaths = exteriorImagePanel.getImagePaths();
                List<String> interiorPaths = interiorImagePanel.getImagePaths();
                
                // Create directories for car images
                ImageUtils.createCarImageDirectories(car.getId());
                
                // Save exterior images
                List<String> savedExteriorPaths = new ArrayList<>();
                for (int i = 0; i < exteriorPaths.size(); i++) {
                    String tempPath = exteriorPaths.get(i);
                    String savedPath = ImageUtils.saveCarImage(
                        new File(tempPath), car.getId(), "exterior", i);
                    if (savedPath != null) {
                        savedExteriorPaths.add(savedPath);
                    }
                }
                
                // Save interior images
                List<String> savedInteriorPaths = new ArrayList<>();
                for (int i = 0; i < interiorPaths.size(); i++) {
                    String tempPath = interiorPaths.get(i);
                    String savedPath = ImageUtils.saveCarImage(
                        new File(tempPath), car.getId(), "interior", i);
                    if (savedPath != null) {
                        savedInteriorPaths.add(savedPath);
                    }
                }
                
                // Update car with saved image paths
                car.setExteriorImages(savedExteriorPaths);
                car.setInteriorImages(savedInteriorPaths);
                carDAO.updateCar(car);
            }
            
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public boolean isApproved() {
        return saved;
    }
    
    public Car getResult() {
        return car;
    }
}
