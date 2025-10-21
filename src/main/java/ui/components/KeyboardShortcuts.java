package ui.components;

import javax.swing.*;
import java.awt.event.*;

public class KeyboardShortcuts {
    public static void install(JComponent component, String key, Runnable action) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        if (keyStroke != null) {
            component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, key);
            component.getActionMap().put(key, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    action.run();
                }
            });
        }
    }
    
    public static void initialize(JComponent component) {
        // Default keyboard shortcuts - can be customized
        install(component, "F5", () -> {
            // Refresh action
        });
    }
}
