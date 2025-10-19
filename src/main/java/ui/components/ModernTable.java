package ui.components;

import utils.ModernTheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ModernTable - Enhanced JTable with modern styling and features.
 * 
 * Features:
 * - Dark theme with high contrast
 * - Alternating row colors
 * - Smooth hover effects
 * - Modern header styling
 * - Sorting indicators
 * - Custom cell padding
 * - Professional borders
 * 
 * Usage:
 * <pre>
 * String[] columns = {"ID", "Name", "Email", "Status"};
 * ModernTable table = new ModernTable(columns);
 * table.addRow(new Object[]{1, "John Doe", "john@example.com", "Active"});
 * </pre>
 * 
 * Inspired by: Stripe dashboard tables, GitHub pull requests table
 */
public class ModernTable extends JScrollPane {
    
    private JTable table;
    private DefaultTableModel model;
    private Color headerColor = new Color(35, 35, 35);
    private Color evenRowColor = ModernTheme.BG_CARD;
    private Color oddRowColor = new Color(40, 40, 40);
    private Color hoverColor = new Color(50, 50, 50);
    private Color selectionColor = ModernTheme.ACCENT_BLUE_DARK;
    
    /**
     * Create a modern table with specified column names.
     * 
     * @param columnNames Array of column header names
     */
    public ModernTable(String[] columnNames) {
        this(columnNames, true);
    }
    
    /**
     * Create a modern table with optional edit capability.
     * 
     * @param columnNames Array of column header names
     * @param editable Whether cells are editable
     */
    public ModernTable(String[] columnNames, boolean editable) {
        // Create model
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };
        
        // Create table
        table = new JTable(model);
        setupTableAppearance();
        setupTableBehavior();
        
        // Wrap in scroll pane
        setViewportView(table);
        setBorder(null);
        getViewport().setBackground(ModernTheme.BG_DARK);
        
        // Modern scrollbar styling
        getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(80, 80, 80);
                this.trackColor = ModernTheme.BG_DARKER;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
    }
    
    /**
     * Setup visual appearance of the table.
     */
    private void setupTableAppearance() {
        // Table colors
        table.setBackground(evenRowColor);
        table.setForeground(ModernTheme.TEXT_PRIMARY);
        table.setSelectionBackground(selectionColor);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(ModernTheme.BORDER_COLOR);
        
        // Font and sizing
        table.setFont(ModernTheme.FONT_REGULAR);
        table.setRowHeight(40);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(headerColor);
        header.setForeground(ModernTheme.TEXT_PRIMARY);
        header.setFont(ModernTheme.FONT_BOLD);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ModernTheme.ACCENT_BLUE));
        
        // Custom header renderer
        header.setDefaultRenderer(new ModernHeaderRenderer());
        
        // Custom cell renderer for alternating rows
        table.setDefaultRenderer(Object.class, new ModernCellRenderer());
    }
    
    /**
     * Setup table behavior and interactions.
     */
    private void setupTableBehavior() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        
        // Hover effect
        table.addMouseMotionListener(new MouseMotionAdapter() {
            int lastRow = -1;
            
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != lastRow) {
                    lastRow = row;
                    table.repaint();
                }
            }
        });
        
        // Reset hover on exit
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                table.repaint();
            }
        });
    }
    
    /**
     * Add a row to the table.
     */
    public void addRow(Object[] rowData) {
        model.addRow(rowData);
    }
    
    /**
     * Remove selected row.
     */
    public void removeSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            model.removeRow(selectedRow);
        }
    }
    
    /**
     * Clear all rows.
     */
    public void clearRows() {
        model.setRowCount(0);
    }
    
    /**
     * Get the underlying JTable.
     */
    public JTable getTable() {
        return table;
    }
    
    /**
     * Get the table model.
     */
    public DefaultTableModel getModel() {
        return model;
    }
    
    /**
     * Get selected row index.
     */
    public int getSelectedRow() {
        return table.getSelectedRow();
    }
    
    /**
     * Get value at specific cell.
     */
    public Object getValueAt(int row, int column) {
        return model.getValueAt(row, column);
    }
    
    /**
     * Custom header renderer with modern styling.
     */
    private class ModernHeaderRenderer extends DefaultTableCellRenderer {
        public ModernHeaderRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(headerColor);
            setForeground(ModernTheme.TEXT_PRIMARY);
            setFont(ModernTheme.FONT_BOLD);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return this;
        }
    }
    
    /**
     * Custom cell renderer with alternating rows and hover effect.
     */
    private class ModernCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Alternating row colors
            if (!isSelected) {
                Point mousePoint = table.getMousePosition();
                int hoverRow = mousePoint != null ? table.rowAtPoint(mousePoint) : -1;
                
                if (row == hoverRow) {
                    setBackground(hoverColor);
                } else {
                    setBackground(row % 2 == 0 ? evenRowColor : oddRowColor);
                }
            } else {
                setBackground(selectionColor);
                setForeground(Color.WHITE);
            }
            
            // Padding
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            
            // Alignment
            setHorizontalAlignment(column == 0 ? JLabel.CENTER : JLabel.LEFT);
            
            return this;
        }
    }
}
