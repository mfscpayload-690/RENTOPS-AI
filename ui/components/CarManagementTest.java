package ui.components;

import javax.swing.*;
import ui.CarsPanel;

public class CarManagementTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("RENTOPS-AI - Car Management Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);

            // Create and add the CarsPanel
            CarsPanel carsPanel = new CarsPanel();
            frame.add(carsPanel);

            frame.setVisible(true);

            System.out.println("Car Management Test Application Started");
            System.out.println("Features available:");
            System.out.println("1. Add Car - Creates car with multi-image upload capability");
            System.out.println("2. Edit Car - Modify existing car details and images");
            System.out.println("3. View Details - View car details with image carousel");
            System.out.println("4. Delete Car - Remove car from database");
            System.out.println("5. Refresh - Reload cars from database");
            System.out.println("");
            System.out.println("To test multi-image feature:");
            System.out.println("1. Click 'Add Car' button");
            System.out.println("2. Fill in car details in 'Details' tab");
            System.out.println("3. Switch to 'Images' tab");
            System.out.println("4. Upload exterior and interior images");
            System.out.println("5. Click OK to save");
            System.out.println("6. Select the car and click 'View Details' to see images");
        });
    }
}
