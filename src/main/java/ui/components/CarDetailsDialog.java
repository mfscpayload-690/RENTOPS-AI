package ui.components;

import models.Car;
import javax.swing.*;
import java.awt.*;

public class CarDetailsDialog extends JDialog {
    public CarDetailsDialog(Component parent, Car car) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), "Car Details", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        
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
        
        add(panel, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
}
