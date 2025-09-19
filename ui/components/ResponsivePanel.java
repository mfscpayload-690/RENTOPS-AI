package ui.components;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * A responsive panel that can adjust its layout based on the window size
 */
public class ResponsivePanel extends JPanel {

    private static final int BREAKPOINT_SMALL = 600;
    private static final int BREAKPOINT_MEDIUM = 900;

    public enum ScreenSize {
        SMALL, MEDIUM, LARGE
    }

    private ScreenSize currentSize = ScreenSize.LARGE;
    private Consumer<ScreenSize> layoutChangeHandler;

    public ResponsivePanel() {
        this(new BorderLayout());
    }

    public ResponsivePanel(LayoutManager layout) {
        super(layout);

        // Add component listener to detect size changes
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayout();
            }
        });
    }

    /**
     * Set a handler to be called when the layout should change
     */
    public void setLayoutChangeHandler(Consumer<ScreenSize> handler) {
        this.layoutChangeHandler = handler;
    }

    /**
     * Update layout based on current width
     */
    private void updateLayout() {
        int width = getWidth();
        ScreenSize newSize;

        if (width < BREAKPOINT_SMALL) {
            newSize = ScreenSize.SMALL;
        } else if (width < BREAKPOINT_MEDIUM) {
            newSize = ScreenSize.MEDIUM;
        } else {
            newSize = ScreenSize.LARGE;
        }

        // Only update if size changed
        if (newSize != currentSize) {
            currentSize = newSize;
            if (layoutChangeHandler != null) {
                layoutChangeHandler.accept(currentSize);
            }
        }
    }

    /**
     * Get the current screen size
     */
    public ScreenSize getCurrentScreenSize() {
        return currentSize;
    }

    /**
     * Creates a responsive grid panel
     */
    public static class GridPanel extends JPanel {

        private int columns;
        private int defaultGap;
        private java.util.List<Component> components = new java.util.ArrayList<>();

        public GridPanel(int columns, int gap) {
            this.columns = columns;
            this.defaultGap = gap;
            setLayout(new GridLayout(0, columns, gap, gap));
            setBorder(new EmptyBorder(gap, gap, gap, gap));
        }

        @Override
        public Component add(Component comp) {
            components.add(comp);
            return super.add(comp);
        }

        /**
         * Change the number of columns in the grid
         */
        public void setColumns(int columns) {
            if (this.columns != columns) {
                this.columns = columns;

                // Need to rebuild the layout
                removeAll();
                setLayout(new GridLayout(0, columns, defaultGap, defaultGap));

                // Re-add all components
                for (Component comp : components) {
                    super.add(comp);
                }

                revalidate();
                repaint();
            }
        }
    }
}
