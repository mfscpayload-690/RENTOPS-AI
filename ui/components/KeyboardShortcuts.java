package ui.components;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

/**
 * Manages keyboard shortcuts for the application
 */
public class KeyboardShortcuts {

    // Map to store all registered keyboard shortcuts
    private static final Map<KeyStroke, Action> SHORTCUTS = new HashMap<>();

    /**
     * Initialize global keyboard shortcuts for a component
     *
     * @param component The component to register shortcuts for (typically a
     * JFrame)
     */
    public static void initialize(JComponent component) {
        // Register common shortcuts
        registerShortcut(component, "Search", KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                e -> {
                    focusSearchField(component);
                });

        registerShortcut(component, "Logout", KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                e -> {
                    triggerLogout(component);
                });

        registerShortcut(component, "Help", KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()),
                e -> {
                    showHelp(component);
                });

        registerShortcut(component, "Dashboard", KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
                e -> {
                    showDashboard(component);
                });

        registerShortcut(component, "Escape", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                e -> {
                    handleEscape(component);
                });
    }

    /**
     * Register a keyboard shortcut
     */
    public static void registerShortcut(JComponent component, String name, KeyStroke keyStroke, final ActionListener actionListener) {
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionListener.actionPerformed(e);
            }
        };

        SHORTCUTS.put(keyStroke, action);

        // Register with input and action maps
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, name);
        component.getActionMap().put(name, action);
    }

    /**
     * Focus the search field
     */
    private static void focusSearchField(Component component) {
        JTextField searchField = findComponent(component, JTextField.class, c
                -> c.getName() != null && c.getName().toLowerCase().contains("search"));

        if (searchField != null) {
            searchField.requestFocusInWindow();
        } else {
            // Try to find any search field
            JTextField anyField = findComponent(component, JTextField.class, c -> true);
            if (anyField != null) {
                anyField.requestFocusInWindow();
            }
        }
    }

    /**
     * Trigger logout functionality
     */
    private static void triggerLogout(Component component) {
        JButton logoutButton = findComponent(component, JButton.class, c
                -> c.getText() != null && c.getText().toLowerCase().contains("logout"));

        if (logoutButton != null) {
            logoutButton.doClick();
        }
    }

    /**
     * Show help
     */
    private static void showHelp(Component component) {
        ModernDialog.showInfo(component, "Keyboard Shortcuts",
                "<html><b>Ctrl+F</b> - Focus search field<br>"
                + "<b>Ctrl+L</b> - Logout<br>"
                + "<b>Ctrl+H</b> - Show this help<br>"
                + "<b>Home</b> - Go to dashboard<br>"
                + "<b>Esc</b> - Close dialogs / cancel actions</html>");
    }

    /**
     * Show dashboard
     */
    private static void showDashboard(Component component) {
        JButton homeButton = findComponent(component, JButton.class, c
                -> (c.getText() != null && c.getText().toLowerCase().contains("dashboard"))
                || (c.getName() != null && c.getName().toLowerCase().contains("home")));

        if (homeButton != null) {
            homeButton.doClick();
        }
    }

    /**
     * Handle escape key
     */
    private static void handleEscape(Component component) {
        // First try to find and close visible dialogs
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window.isVisible() && window instanceof JDialog) {
                window.dispose();
                return;
            }
        }
    }

    /**
     * Find a component matching the given criteria
     */
    private static <T extends Component> T findComponent(Component component, Class<T> clazz, java.util.function.Predicate<T> predicate) {
        if (clazz.isInstance(component) && predicate.test(clazz.cast(component))) {
            return clazz.cast(component);
        }

        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                Component child = container.getComponent(i);
                T found = findComponent(child, clazz, predicate);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    /**
     * Show a toast notification with shortcut information
     */
    public static void showShortcutHint(Component component) {
        Toast.info(component, "Press Ctrl+H to see keyboard shortcuts");
    }
}
