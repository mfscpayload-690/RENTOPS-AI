package ui.components;

import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Advanced search panel with multiple criteria for filtering cars
 */
public class AdvancedSearchPanel extends JPanel {

    private JTextField searchField;
    private JSlider priceRangeSlider;
    private JLabel priceRangeLabel;
    private JComboBox<String> makeFilter;
    private JComboBox<String> yearFilter;
    private JCheckBox availableOnlyFilter;
    private List<Consumer<SearchCriteria>> searchListeners = new ArrayList<>();
    private JButton searchButton;

    // Current search criteria
    private SearchCriteria currentCriteria = new SearchCriteria();

    public AdvancedSearchPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create components
        JPanel searchBoxPanel = createSearchBoxPanel();
        JPanel filtersPanel = createFiltersPanel();

        add(searchBoxPanel, BorderLayout.NORTH);
        add(filtersPanel, BorderLayout.CENTER);

        // Initial search criteria
        updateSearchCriteria();
    }

    private JPanel createSearchBoxPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        searchButton = new ModernButton("Search");

        panel.add(new JLabel("Search:"), BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(searchButton, BorderLayout.EAST);

        // Add listeners
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSearchText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSearchText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSearchText();
            }

            private void updateSearchText() {
                currentCriteria.searchText = searchField.getText();
                notifySearchListeners();
            }
        });

        searchButton.addActionListener(e -> notifySearchListeners());

        return panel;
    }

    private JPanel createFiltersPanel() {
        JPanel panel = new CardPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                        "Filters",
                        TitledBorder.LEADING,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12),
                        new Color(70, 70, 70)
                )
        ));

        // Make filter
        JPanel makePanel = new JPanel(new BorderLayout(10, 0));
        makePanel.setOpaque(false);
        makePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        makeFilter = new JComboBox<>(new String[]{
            "All Makes", "BMW", "Ford", "Honda", "Tesla", "Toyota"
        });

        makePanel.add(new JLabel("Make:"), BorderLayout.WEST);
        makePanel.add(makeFilter, BorderLayout.CENTER);

        // Year filter
        JPanel yearPanel = new JPanel(new BorderLayout(10, 0));
        yearPanel.setOpaque(false);
        yearPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        yearFilter = new JComboBox<>(new String[]{
            "All Years", "2023", "2022", "2021", "2020", "2019"
        });

        yearPanel.add(new JLabel("Year:"), BorderLayout.WEST);
        yearPanel.add(yearFilter, BorderLayout.CENTER);

        // Price range filter
        JPanel pricePanel = new JPanel(new BorderLayout(10, 0));
        pricePanel.setOpaque(false);
        pricePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        priceRangeSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 200);
        priceRangeSlider.setMajorTickSpacing(50);
        priceRangeSlider.setMinorTickSpacing(10);
        priceRangeSlider.setPaintTicks(true);
        priceRangeSlider.setPaintLabels(true);
        priceRangeSlider.setOpaque(false);

        priceRangeLabel = new JLabel("Max Price: ₹200/day");

        pricePanel.add(new JLabel("Price:"), BorderLayout.NORTH);
        pricePanel.add(priceRangeSlider, BorderLayout.CENTER);
        pricePanel.add(priceRangeLabel, BorderLayout.SOUTH);

        // Availability filter
        JPanel availabilityPanel = new JPanel(new BorderLayout(10, 0));
        availabilityPanel.setOpaque(false);
        availabilityPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        availableOnlyFilter = new JCheckBox("Show available cars only");
        availableOnlyFilter.setOpaque(false);
        availableOnlyFilter.setSelected(true);

        availabilityPanel.add(availableOnlyFilter, BorderLayout.CENTER);

        // Add all to filters panel
        panel.add(makePanel);
        panel.add(yearPanel);
        panel.add(pricePanel);
        panel.add(availabilityPanel);

        // Add listeners
        makeFilter.addActionListener(e -> {
            String selected = (String) makeFilter.getSelectedItem();
            currentCriteria.make = "All Makes".equals(selected) ? null : selected;
            notifySearchListeners();
        });

        yearFilter.addActionListener(e -> {
            String selected = (String) yearFilter.getSelectedItem();
            currentCriteria.year = "All Years".equals(selected) ? 0 : Integer.parseInt(selected);
            notifySearchListeners();
        });

        priceRangeSlider.addChangeListener(e -> {
            int value = priceRangeSlider.getValue();
            priceRangeLabel.setText("Max Price: ₹" + value + "/day");
            currentCriteria.maxPrice = new BigDecimal(value);
            if (!priceRangeSlider.getValueIsAdjusting()) {
                notifySearchListeners();
            }
        });

        availableOnlyFilter.addActionListener(e -> {
            currentCriteria.availableOnly = availableOnlyFilter.isSelected();
            notifySearchListeners();
        });

        return panel;
    }

    private void updateSearchCriteria() {
        currentCriteria.searchText = searchField.getText();

        String selectedMake = (String) makeFilter.getSelectedItem();
        currentCriteria.make = "All Makes".equals(selectedMake) ? null : selectedMake;

        String selectedYear = (String) yearFilter.getSelectedItem();
        currentCriteria.year = "All Years".equals(selectedYear) ? 0 : Integer.parseInt(selectedYear);

        currentCriteria.maxPrice = new BigDecimal(priceRangeSlider.getValue());
        currentCriteria.availableOnly = availableOnlyFilter.isSelected();
    }

    /**
     * Register a listener for search criteria changes
     */
    public void addSearchListener(Consumer<SearchCriteria> listener) {
        searchListeners.add(listener);
    }

    /**
     * Notify all listeners of search criteria changes
     */
    private void notifySearchListeners() {
        for (Consumer<SearchCriteria> listener : searchListeners) {
            listener.accept(currentCriteria);
        }
    }

    /**
     * Get the search button for external action listeners
     */
    public JButton getSearchButton() {
        return searchButton;
    }

    /**
     * Class representing search criteria
     */
    public static class SearchCriteria {

        private String searchText = "";
        private String make = null;
        private int year = 0;
        private BigDecimal maxPrice = new BigDecimal(200);
        private boolean availableOnly = true;

        public String getSearchText() {
            return searchText;
        }

        public String getMake() {
            return make;
        }

        public int getYear() {
            return year;
        }

        public BigDecimal getMaxPrice() {
            return maxPrice;
        }

        public boolean isAvailableOnly() {
            return availableOnly;
        }
    }
}
