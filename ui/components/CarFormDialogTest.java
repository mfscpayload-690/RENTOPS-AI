package ui.components;

import javax.swing.*;
import models.Car;

public class CarFormDialogTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Frame");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLocationRelativeTo(null);

            JButton testButton = new JButton("Test Car Form Dialog");
            testButton.addActionListener(e -> {
                // Test with new car
                CarFormDialog dialog = new CarFormDialog(frame, "Add New Car", null);
                dialog.setVisible(true);

                if (dialog.isApproved()) {
                    Car newCar = dialog.getResult();
                    System.out.println("New car created: " + newCar.getMake() + " " + newCar.getModel());
                } else {
                    System.out.println("Dialog was cancelled");
                }
            });

            frame.add(testButton);
            frame.setVisible(true);
        });
    }
}
