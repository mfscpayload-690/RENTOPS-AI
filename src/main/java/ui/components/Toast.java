package ui.components;

import javax.swing.*;
import java.awt.*;

public class Toast {
    public static void show(Component parent, String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE)
        );
    }
    
    public static void info(Component parent, String message) {
        show(parent, message);
    }
    
    public static void error(Component parent, String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE)
        );
    }
    
    public static void success(Component parent, String message) {
        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE)
        );
    }
}
