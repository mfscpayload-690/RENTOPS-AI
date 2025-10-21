package ui.components;

import models.Car;
import dao.CarDAO;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class CarFormDialog extends JDialog {
    private Car car;
    private JTextField makeField, modelField, yearField, priceField, licensePlateField, specsField;
    private JComboBox<String> statusCombo;
    private boolean saved = false;
    
    public CarFormDialog(Component parent, String title, Car car) {
        super((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        this.car = car;
        setSize(450, 400);
        setLocationRelativeTo(parent);
        
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
        
        add(panel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> save());
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);
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
            
            if (car.getId() == 0) {
                carDAO.addCar(car);
            } else {
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
