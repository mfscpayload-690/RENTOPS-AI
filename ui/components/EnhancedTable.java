package ui.components;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Enhanced JTable with modern styling and improved functionality: - Sortable
 * columns - Row highlighting on hover - Column reordering - Customizable cell
 * rendering
 */
public class EnhancedTable extends JTable {

    private Color headerBackground = new Color(41, 128, 185);
    private Color headerForeground = Color.WHITE;
    private Color alternateRowColor = new Color(240, 245, 250);
    private Color hoverRowColor = new Color(230, 240, 255);
    private Color selectionColor = new Color(100, 149, 237);
    private Font tableFont = new Font("Segoe UI", Font.PLAIN, 12);
    private int rowHeight = 28;

    private int hoverRow = -1;

    public EnhancedTable() {
        setupTable();
    }

    public EnhancedTable(TableModel model) {
        super(model);
        setupTable();
    }

    private void setupTable() {
        // Enable sorting
        setAutoCreateRowSorter(true);

        // Enable column reordering
        getTableHeader().setReorderingAllowed(true);

        // Set appearance
        setFont(tableFont);
        setRowHeight(rowHeight);
        setSelectionBackground(selectionColor);
        setSelectionForeground(Color.WHITE);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));

        // Customize header
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer());
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Add hover effect
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                int newHoverRow = rowAtPoint(p);
                if (newHoverRow != hoverRow) {
                    int oldHoverRow = hoverRow;
                    hoverRow = newHoverRow;
                    if (oldHoverRow != -1) {
                        Rectangle r = getCellRect(oldHoverRow, 0, true);
                        r.width = getWidth();
                        repaint(r);
                    }
                    if (hoverRow != -1) {
                        Rectangle r = getCellRect(hoverRow, 0, true);
                        r.width = getWidth();
                        repaint(r);
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverRow != -1) {
                    int oldHoverRow = hoverRow;
                    hoverRow = -1;
                    Rectangle r = getCellRect(oldHoverRow, 0, true);
                    r.width = getWidth();
                    repaint(r);
                }
            }
        });

        // Use custom cell renderer for alternating row colors and hover effect
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row == hoverRow) {
                        c.setBackground(hoverRowColor);
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : alternateRowColor);
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

                return c;
            }
        });
    }

    /**
     * Custom header renderer for better styling
     */
    private class HeaderRenderer extends DefaultTableCellRenderer {

        public HeaderRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            setBackground(headerBackground);
            setForeground(headerForeground);
            setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 73, 94)));
            return this;
        }
    }

    // Setter methods for customization
    public void setHeaderBackground(Color headerBackground) {
        this.headerBackground = headerBackground;
        repaint();
    }

    public void setHeaderForeground(Color headerForeground) {
        this.headerForeground = headerForeground;
        repaint();
    }

    public void setAlternateRowColor(Color alternateRowColor) {
        this.alternateRowColor = alternateRowColor;
        repaint();
    }

    public void setHoverRowColor(Color hoverRowColor) {
        this.hoverRowColor = hoverRowColor;
        repaint();
    }

    public void setTableFont(Font tableFont) {
        this.tableFont = tableFont;
        setFont(tableFont);
        repaint();
    }
}
