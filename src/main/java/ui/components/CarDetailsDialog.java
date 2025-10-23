package ui.components;

import models.Car;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CarDetailsDialog extends JDialog {
    public CarDetailsDialog(Component parent, Car car) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), "Car Details", true);
        setSize(900, 700);
        setLocationRelativeTo(parent);
        
        // Main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Basic Info
        tabbedPane.addTab("Basic Info", createBasicInfoPanel(car));
        
        // Tab 2: Exterior Images
        List<String> exteriorImages = car.getExteriorImages();
        if (exteriorImages != null && !exteriorImages.isEmpty()) {
            tabbedPane.addTab("Exterior Images (" + exteriorImages.size() + ")", 
                new ImageCarouselPanel(exteriorImages, 800, 500));
        } else {
            JPanel noExteriorPanel = new JPanel();
            noExteriorPanel.add(new JLabel("No exterior images available"));
            tabbedPane.addTab("Exterior Images (0)", noExteriorPanel);
        }
        
        // Tab 3: Interior Images
        List<String> interiorImages = car.getInteriorImages();
        if (interiorImages != null && !interiorImages.isEmpty()) {
            tabbedPane.addTab("Interior Images (" + interiorImages.size() + ")", 
                new ImageCarouselPanel(interiorImages, 800, 500));
        } else {
            JPanel noInteriorPanel = new JPanel();
            noInteriorPanel.add(new JLabel("No interior images available"));
            tabbedPane.addTab("Interior Images (0)", noInteriorPanel);
        }
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Close button
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createBasicInfoPanel(Car car) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("ID:"));
        panel.add(new JLabel(String.valueOf(car.getId())));
        
        panel.add(new JLabel("Make:"));
        panel.add(new JLabel(car.getMake()));
        
        panel.add(new JLabel("Model:"));
        panel.add(new JLabel(car.getModel()));
        
        panel.add(new JLabel("Year:"));
        panel.add(new JLabel(String.valueOf(car.getYear())));
        
        panel.add(new JLabel("License Plate:"));
        panel.add(new JLabel(car.getLicensePlate()));
        
        panel.add(new JLabel("Price/Day:"));
        panel.add(new JLabel("Rs. " + car.getPricePerDay()));
        
        panel.add(new JLabel("Status:"));
        panel.add(new JLabel(car.getStatus()));
        
        panel.add(new JLabel("Specs:"));
        panel.add(new JLabel(car.getSpecs()));
        
        panel.add(new JLabel("Total KM Driven:"));
        panel.add(new JLabel(String.valueOf(car.getTotalKmDriven()) + " km"));
        
        return panel;
    }
}
