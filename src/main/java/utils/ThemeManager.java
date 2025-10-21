package utils;

import java.awt.*;
import java.io.*;
import java.util.Properties;
import javax.swing.*;

/**
 * Theme manager that applies FlatLaf (if available) with modern defaults and
 * persists the user's theme (light/dark) in config/ui.properties.
 *
 * Works even when FlatLaf jar is not present by falling back to system L&F.
 */
public final class ThemeManager {

    private static final String CONFIG_DIR = "config";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "ui.properties";
    private static final String KEY_THEME = "ui.theme"; // light | dark

    // Cache current theme selection
    private static String currentTheme = null; // "light" or "dark"
    private static boolean flatlafActive = false;

    private ThemeManager() {
    }

    public static boolean isDark() {
        ensureLoaded();
        return "dark".equalsIgnoreCase(currentTheme);
    }

    public static boolean isFlatLafActive() {
        return flatlafActive;
    }

    public static void toggleTheme(Window rootWindow) {
        setTheme(isDark() ? "light" : "dark", rootWindow);
    }

    public static void setTheme(String theme, Window rootWindow) {
        currentTheme = (theme == null || theme.isBlank()) ? "light" : theme.toLowerCase();
        saveTheme();
        applyLookAndFeel();
        updateAllWindows(rootWindow);
    }

    /**
     * Load persisted theme, apply L&F and defaults. Call this early in app
     * startup (before building UI).
     */
    public static void initLookAndFeel() {
        ensureLoaded();
        applyLookAndFeel();
        applyUiDefaults();
    }

    private static void ensureLoaded() {
        if (currentTheme != null) {
            return;
        }
        currentTheme = "light";
        Properties p = new Properties();
        File f = new File(CONFIG_FILE);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                p.load(fis);
                String t = p.getProperty(KEY_THEME);
                if (t != null && !t.isBlank()) {
                    currentTheme = t.trim().toLowerCase();
                }
            } catch (IOException ignored) {
            }
        }
    }

    private static void saveTheme() {
        try {
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Properties p = new Properties();
            p.setProperty(KEY_THEME, currentTheme);
            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                p.store(fos, "Rentops-AI UI settings");
            }
        } catch (IOException e) {
            System.err.println("ThemeManager: Failed to save UI theme: " + e.getMessage());
        }
    }

    private static void applyLookAndFeel() {
        // Try FlatLaf first via reflection so we don't require it at compile time.
        String lafClass = isDark() ? "com.formdev.flatlaf.FlatDarkLaf" : "com.formdev.flatlaf.FlatLightLaf";
        try {
            Class<?> c = Class.forName(lafClass);
            LookAndFeel laf = (LookAndFeel) c.getDeclaredConstructor().newInstance();
            UIManager.setLookAndFeel(laf);
            flatlafActive = true;
            // Optional: use native window decorations when available
            try {
                Class<?> flatLafClass = Class.forName("com.formdev.flatlaf.FlatLaf");
                flatLafClass.getMethod("setUseNativeWindowDecorations", boolean.class).invoke(null, true);
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            // Fallback to system
            flatlafActive = false;
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }
    }

    private static void applyUiDefaults() {
        // Typography
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("defaultFont", defaultFont);

        // Global shape & focus (FlatLaf keys; harmless on other L&Fs)
        UIManager.put("Component.arc", 14);
        UIManager.put("Button.arc", 16);
        UIManager.put("TextComponent.arc", 14);
        UIManager.put("Component.focusWidth", 1);

        // Palette
        UIManager.put("Component.accentColor", Color.decode("#2F80ED")); // Primary
        UIManager.put("Actions.Green", Color.decode("#27AE60")); // Success
        UIManager.put("Actions.Orange", Color.decode("#F2994A")); // Warning
        UIManager.put("Actions.Red", Color.decode("#EB5757")); // Danger
        UIManager.put("Panel.background", Color.decode("#F7F9FC")); // Surface

        // Explicit color assignments for key UI elements
        Color tableHeaderBg = new Color(52, 73, 94); // Darker blue header
        Color tableHeaderFg = Color.WHITE;
        Color tableGridColor = new Color(220, 225, 235); // Subtle grid lines

        // Tables & lists
        UIManager.put("Table.rowHeight", 36);
        UIManager.put("Table.showHorizontalLines", Boolean.TRUE); // Keep horizontal lines for readability
        UIManager.put("Table.showVerticalLines", Boolean.TRUE); // Keep vertical lines for readability
        UIManager.put("Table.gridColor", tableGridColor); // Soft color for grid
        UIManager.put("Table.alternateRowColor", new Color(245, 248, 252)); // Subtle alternate color
        UIManager.put("TableHeader.background", tableHeaderBg);
        UIManager.put("TableHeader.foreground", tableHeaderFg);
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("List.cellRendererBackground", Color.WHITE);
        UIManager.put("List.selectionBackground", new Color(0xE3F2FD));
        UIManager.put("Table.selectionBackground", new Color(209, 231, 255)); // Soft blue selection
        UIManager.put("Table.selectionForeground", new Color(20, 20, 20)); // Near black text

        // Buttons & inputs sizing hints (FlatLaf honors these via client props too)
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Button.minimumHeight", 34);
        UIManager.put("TextComponent.minimumHeight", 34);

        // Dialogs
        UIManager.put("TitlePane.unifiedBackground", Boolean.TRUE);
    }

    private static void updateAllWindows(Window rootWindow) {
        // Update entire UI tree so theme switches instantly
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
            w.invalidate();
            w.validate();
            w.repaint();
        }
        if (rootWindow != null) {
            rootWindow.toFront();
        }
    }
}
