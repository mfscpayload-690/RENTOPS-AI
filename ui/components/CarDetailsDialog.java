package ui.components;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import models.Car;

/**
 * A dialog to display detailed information about a car.
 */
public class CarDetailsDialog extends JDialog {

    /**
     * Create a new car details dialog
     *
     * @param parent The parent component
     * @param car The car to display details for
     */
    public CarDetailsDialog(Component parent, Car car) {
        super(SwingUtilities.getWindowAncestor(parent), "Car Details", ModalityType.APPLICATION_MODAL);

        setResizable(true);
        setMinimumSize(new Dimension(450, 400));
        setPreferredSize(new Dimension(500, 400));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Car info panel with detailed fields
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Format price
        String priceText = "₹" + new DecimalFormat("#,##0.00").format(car.getPricePerDay());

        // Title for the dialog with car name
        JLabel titleLabel = new JLabel(car.getMake() + " " + car.getModel() + " (" + car.getYear() + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Set background color based on car status
        Color statusColor = new Color(46, 204, 113); // Green for available
        if (car.getStatus().equalsIgnoreCase("maintenance")) {
            statusColor = new Color(231, 76, 60); // Red for maintenance
        } else if (car.getStatus().equalsIgnoreCase("rented")) {
            statusColor = new Color(241, 196, 15); // Yellow for rented
        }

        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(statusColor);
        statusPanel.setPreferredSize(new Dimension(80, 30));
        JLabel statusLabel = new JLabel(car.getStatus().toUpperCase());
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusPanel.add(statusLabel);

        headerPanel.add(statusPanel, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Add basic info
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        infoPanel.add(createBoldLabel("License Plate:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        infoPanel.add(new JLabel(car.getLicensePlate()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(createBoldLabel("Price per Day:"), gbc);

        gbc.gridx = 1;
        infoPanel.add(new JLabel(priceText), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(createBoldLabel("Total KM Driven:"), gbc);

        gbc.gridx = 1;
        infoPanel.add(new JLabel(car.getTotalKmDriven() + " KM"), gbc);

        // Add specs section with textarea for better display
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        infoPanel.add(createBoldLabel("Specifications:"), gbc);

        gbc.gridx = 1;
        JTextArea specsArea = new JTextArea(car.getSpecs());
        specsArea.setLineWrap(true);
        specsArea.setWrapStyleWord(true);
        specsArea.setEditable(false);
        specsArea.setBackground(UIManager.getColor("Panel.background"));
        specsArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        specsArea.setRows(6);

        JScrollPane specsScroll = new JScrollPane(specsArea);
        specsScroll.setBorder(BorderFactory.createEmptyBorder());
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        infoPanel.add(specsScroll, gbc);

        // Add button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(closeButton);

        // Add panels to content panel
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(infoPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
        pack();
        setLocationRelativeTo(parent);
    }

    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        Font font = label.getFont();
        Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
        label.setFont(boldFont);
        return label;
    }
}
