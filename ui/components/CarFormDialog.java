package ui.components;

import models.Car;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * A resizable dialog for adding or editing car information.
 */
public class CarFormDialog extends JDialog {

    private JTextField makeField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField licenseField;
    private JComboBox<String> statusBox;
    private JTextArea specsField;
    private JTextField priceField;
    private JTextField totalKmDrivenField;
    private MultiImagePanel exteriorImagePanel;
    private MultiImagePanel interiorImagePanel;
    private boolean approved = false;
    private Car result = null;

    /**
     * Create a new car form dialog
     *
     * @param parent The parent component
     * @param title Dialog title
     * @param existingCar Car to edit (or null if adding new)
     */
    public CarFormDialog(Component parent, String title, Car existingCar) {
        super(SwingUtilities.getWindowAncestor(parent), title, ModalityType.APPLICATION_MODAL);

        // Enable resizing and set appropriate size
        setResizable(true);
        setMinimumSize(new Dimension(600, 500));
        setPreferredSize(new Dimension(750, 600));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create tabbed pane for form and images
        JTabbedPane tabbedPane = new JTabbedPane();

        // Car Details Tab
        JPanel detailsTab = createDetailsTab();
        tabbedPane.addTab("Car Details", detailsTab);

        // Images Tab
        JPanel imagesTab = createImagesTab();
        tabbedPane.addTab("Images", imagesTab);

        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        // Load existing car data if editing
        loadExistingCarData(existingCar);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateAndCreateResult(existingCar)) {
                    approved = true;
                    dispose();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Creates the car details tab
     */
    private JPanel createDetailsTab() {
        JPanel detailsPanel = new JPanel(new BorderLayout());

        // Main form area with improved layout for long fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Initialize form fields
        makeField = new JTextField(20);
        modelField = new JTextField(20);
        yearField = new JTextField(20);
        licenseField = new JTextField(20);
        statusBox = new JComboBox<>(new String[]{"available", "rented", "maintenance"});
        specsField = new JTextArea(5, 20);
        specsField.setLineWrap(true);
        specsField.setWrapStyleWord(true);
        JScrollPane specsScrollPane = new JScrollPane(specsField);
        specsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        priceField = new JTextField(20);
        totalKmDrivenField = new JTextField(20);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Make:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(makeField, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Model:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(modelField, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Year:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(yearField, gbc);

        // Row 4
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("License Plate:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(licenseField, gbc);

        // Row 5
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(statusBox, gbc);

        // Row 6
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Specs:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        formPanel.add(specsScrollPane, gbc);

        // Row 7
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Total KM Driven:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(totalKmDrivenField, gbc);

        // Row 8
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.2;
        formPanel.add(new JLabel("Price/Day:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        formPanel.add(priceField, gbc);

        detailsPanel.add(formPanel, BorderLayout.CENTER);
        return detailsPanel;
    }

    /**
     * Creates the images tab
     */
    private JPanel createImagesTab() {
        JPanel imagesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        imagesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create image panels
        exteriorImagePanel = new MultiImagePanel("exterior", 5);
        interiorImagePanel = new MultiImagePanel("interior", 5);

        imagesPanel.add(exteriorImagePanel);
        imagesPanel.add(interiorImagePanel);

        return imagesPanel;
    }

    /**
     * Loads existing car data into the form
     */
    private void loadExistingCarData(Car existingCar) {
        if (existingCar != null) {
            makeField.setText(existingCar.getMake());
            modelField.setText(existingCar.getModel());
            yearField.setText(String.valueOf(existingCar.getYear()));
            licenseField.setText(existingCar.getLicensePlate());
            statusBox.setSelectedItem(existingCar.getStatus());
            specsField.setText(existingCar.getSpecs());
            priceField.setText(existingCar.getPricePerDay().toString());
            totalKmDrivenField.setText(String.valueOf(existingCar.getTotalKmDriven()));

            // Load existing images
            exteriorImagePanel.setImagePaths(existingCar.getExteriorImages());
            interiorImagePanel.setImagePaths(existingCar.getInteriorImages());
        }
    }

    /**
     * Validate input fields and create result car object
     *
     * @param existingCar Car to update (or null if adding new)
     * @return true if validation successful
     */
    private boolean validateAndCreateResult(Car existingCar) {
        try {
            // Validate basic required fields
            String make = makeField.getText().trim();
            String modelName = modelField.getText().trim();

            if (make.isEmpty() || modelName.isEmpty()) {
                showError("Make and model cannot be empty");
                return false;
            }

            // Parse year with validation
            int year;
            try {
                year = Integer.parseInt(yearField.getText().trim());
                if (year < 1900 || year > 2100) {
                    showError("Year must be between 1900 and 2100");
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Invalid year format. Please enter a valid number.");
                return false;
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
            BigDecimal price;
            try {
                price = new BigDecimal(priceText);
            } catch (NumberFormatException e) {
                showError("Invalid price format. Please enter numbers only (e.g., 50.00)");
                return false;
            }

            // Parse total KM driven with validation
            int totalKmDriven;
            try {
                totalKmDriven = Integer.parseInt(totalKmDrivenField.getText().trim());
                if (totalKmDriven < 0) {
                    showError("Total KM Driven cannot be negative");
                    return false;
                }
            } catch (NumberFormatException e) {
                showError("Invalid Total KM Driven format. Please enter a valid number.");
                return false;
            }

            // Create result car
            int id = (existingCar != null) ? existingCar.getId() : 0;
            result = new Car(id, make, modelName, year, license, status, specs, price, totalKmDriven);

            // Add uploaded images to the car object
            List<String> exteriorImagePaths = exteriorImagePanel.getImagePaths();
            List<String> interiorImagePaths = interiorImagePanel.getImagePaths();

            result.setExteriorImages(exteriorImagePaths);
            result.setInteriorImages(interiorImagePaths);

            return true;
        } catch (Exception ex) {
            showError("Invalid input: " + ex.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Check if the dialog was approved (OK button clicked)
     *
     * @return true if approved
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * Get the resulting car object
     *
     * @return Car object
     */
    public Car getResult() {
        return result;
    }
}
