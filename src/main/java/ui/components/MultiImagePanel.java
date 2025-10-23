package ui.components;

import utils.ModernTheme;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for uploading and managing multiple images
 */
public class MultiImagePanel extends JPanel {
    private List<String> imagePaths;
    private JPanel imagePreviewPanel;
    private String category; // "exterior" or "interior"
    private static final int PREVIEW_SIZE = 100;
    private static final int MAX_IMAGES = 10;

    public MultiImagePanel(String category) {
        this.category = category;
        this.imagePaths = new ArrayList<>();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(ModernTheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR),
                category.substring(0, 1).toUpperCase() + category.substring(1) + " Images",
                0, 0, ModernTheme.FONT_BOLD, ModernTheme.TEXT_PRIMARY
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Image preview area (scrollable)
        imagePreviewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        imagePreviewPanel.setBackground(ModernTheme.BG_DARK);
        JScrollPane scrollPane = new JScrollPane(imagePreviewPanel);
        scrollPane.setPreferredSize(new Dimension(500, 130));
        scrollPane.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(ModernTheme.BG_CARD);
        
        JButton addButton = new JButton("Add Images");
        addButton.setFont(ModernTheme.FONT_REGULAR);
        addButton.addActionListener(e -> addImages());
        
        JButton clearButton = new JButton("Clear All");
        clearButton.setFont(ModernTheme.FONT_REGULAR);
        clearButton.addActionListener(e -> clearImages());
        
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addImages() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif");
            }
            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
                if (imagePaths.size() >= MAX_IMAGES) {
                    JOptionPane.showMessageDialog(this, 
                        "Maximum " + MAX_IMAGES + " images allowed per category",
                        "Limit Reached", JOptionPane.WARNING_MESSAGE);
                    break;
                }
                imagePaths.add(file.getAbsolutePath());
            }
            refreshPreviews();
        }
    }

    private void clearImages() {
        imagePaths.clear();
        refreshPreviews();
    }

    private void refreshPreviews() {
        imagePreviewPanel.removeAll();
        
        for (int i = 0; i < imagePaths.size(); i++) {
            final int index = i;
            String path = imagePaths.get(i);
            
            JPanel previewBox = new JPanel(new BorderLayout());
            previewBox.setPreferredSize(new Dimension(PREVIEW_SIZE, PREVIEW_SIZE + 30));
            previewBox.setBackground(ModernTheme.BG_DARKER);
            previewBox.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR));
            
            // Load and scale image
            try {
                ImageIcon icon = new ImageIcon(path);
                Image scaled = icon.getImage().getScaledInstance(
                    PREVIEW_SIZE, PREVIEW_SIZE, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaled));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                previewBox.add(imageLabel, BorderLayout.CENTER);
            } catch (Exception e) {
                JLabel errorLabel = new JLabel("Error");
                errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                errorLabel.setForeground(ModernTheme.ERROR_RED);
                previewBox.add(errorLabel, BorderLayout.CENTER);
            }
            
            // Remove button
            JButton removeBtn = new JButton("×");
            removeBtn.setFont(new Font("Arial", Font.BOLD, 16));
            removeBtn.setForeground(ModernTheme.ERROR_RED);
            removeBtn.setBackground(ModernTheme.BG_DARKER);
            removeBtn.setBorderPainted(false);
            removeBtn.setFocusPainted(false);
            removeBtn.setPreferredSize(new Dimension(PREVIEW_SIZE, 25));
            removeBtn.addActionListener(e -> {
                imagePaths.remove(index);
                refreshPreviews();
            });
            previewBox.add(removeBtn, BorderLayout.SOUTH);
            
            imagePreviewPanel.add(previewBox);
        }
        
        imagePreviewPanel.revalidate();
        imagePreviewPanel.repaint();
    }

    public List<String> getImagePaths() {
        return new ArrayList<>(imagePaths);
    }

    public void setImagePaths(List<String> paths) {
        this.imagePaths = new ArrayList<>(paths != null ? paths : new ArrayList<>());
        refreshPreviews();
    }
}
