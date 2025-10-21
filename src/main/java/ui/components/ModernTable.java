package ui.components;

import utils.ModernTheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class ModernTable extends JTable {
    private DefaultTableModel tableModel;
    
    public ModernTable(TableModel model) {
        super(model);
        this.tableModel = (model instanceof DefaultTableModel) ? (DefaultTableModel) model : null;
        setupTable();
    }
    
    public ModernTable(String[] columnNames) {
        this(columnNames, false);
    }
    
    public ModernTable(String[] columnNames, boolean editable) {
        this.tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return editable;
            }
        };
        setModel(this.tableModel);
        setupTable();
    }
    
    private void setupTable() {
        setFont(ModernTheme.MEDIUM_FONT);
        setRowHeight(40);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setBackground(ModernTheme.CARD_BG);
        setForeground(ModernTheme.TEXT_PRIMARY);
        
        getTableHeader().setFont(ModernTheme.MEDIUM_BOLD_FONT);
        getTableHeader().setBackground(ModernTheme.SURFACE_DARK);
        getTableHeader().setForeground(ModernTheme.TEXT_PRIMARY);
        
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? ModernTheme.CARD_BG : ModernTheme.SURFACE_DARK);
                }
                return c;
            }
        });
    }
    
    @Override
    public DefaultTableModel getModel() {
        return tableModel != null ? tableModel : (DefaultTableModel) super.getModel();
    }
    
    public JTable getTable() {
        return this;
    }
    
    public void removeSelectedRow() {
        int selectedRow = getSelectedRow();
        if (selectedRow != -1 && tableModel != null) {
            tableModel.removeRow(selectedRow);
        }
    }
}
