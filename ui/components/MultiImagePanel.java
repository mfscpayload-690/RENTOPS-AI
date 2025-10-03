package ui.components;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import utils.ImageUtils;

/**
 * Reusable component for managing multiple images (upload, preview, delete)
 */
public class MultiImagePanel extends JPanel {

    private final String category; // "exterior" or "interior"
    private final List<String> imagePaths;
    private final JPanel imagePreviewPanel;
    private final JScrollPane scrollPane;
    private final int maxImages;
    private final int thumbnailSize = 150;

    // Callbacks
    private Runnable onImagesChanged;

    public MultiImagePanel(String category, int maxImages) {
        this.category = category;
        this.maxImages = maxImages;
        this.imagePaths = new ArrayList<>();

        setLayout(new BorderLayout());
        setBorder(new TitledBorder(category.substring(0, 1).toUpperCase() + category.substring(1) + " Images"));

        // Create upload button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton uploadButton = new JButton("Upload " + category + " Images");
        uploadButton.setIcon(new ImageIcon(createUploadIcon()));
        uploadButton.addActionListener(this::uploadImages);
        topPanel.add(uploadButton);

        JLabel infoLabel = new JLabel("(Max " + maxImages + " images, JPG/PNG/JPEG supported)");
        infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC, 11f));
        infoLabel.setForeground(Color.GRAY);
        topPanel.add(infoLabel);

        add(topPanel, BorderLayout.NORTH);

        // Create image preview area
        imagePreviewPanel = new JPanel();
        imagePreviewPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        imagePreviewPanel.setBackground(Color.WHITE);

        scrollPane = new JScrollPane(imagePreviewPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, thumbnailSize + 50));
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        add(scrollPane, BorderLayout.CENTER);

        refreshImagePreviews();
    }

    /**
     * Sets the list of image paths and refreshes the preview
     */
    public void setImagePaths(List<String> paths) {
        imagePaths.clear();
        if (paths != null) {
            imagePaths.addAll(paths);
        }
        refreshImagePreviews();
    }

    /**
     * Gets the current list of image paths
     */
    public List<String> getImagePaths() {
        return new ArrayList<>(imagePaths);
    }

    /**
     * Sets callback for when images change
     */
    public void setOnImagesChanged(Runnable callback) {
        this.onImagesChanged = callback;
    }

    /**
     * Handles image upload action
     */
    private void uploadImages(ActionEvent e) {
        if (imagePaths.size() >= maxImages) {
            JOptionPane.showMessageDialog(this,
                    "Maximum " + maxImages + " images allowed for " + category + ".",
                    "Upload Limit",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select " + category + " Images");
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Image files (JPG, PNG, JPEG)", "jpg", "jpeg", "png"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();

            int remainingSlots = maxImages - imagePaths.size();
            if (selectedFiles.length > remainingSlots) {
                JOptionPane.showMessageDialog(this,
                        "You can only add " + remainingSlots + " more images.\n"
                        + "Only the first " + remainingSlots + " files will be processed.",
                        "Upload Limit",
                        JOptionPane.WARNING_MESSAGE);
            }

            int processedCount = 0;
            for (File file : selectedFiles) {
                if (processedCount >= remainingSlots) {
                    break;
                }

                if (ImageUtils.isValidImageFile(file)) {
                    // For now, just store the absolute path
                    // In a real application, you'd copy the file to the app's image directory
                    imagePaths.add(file.getAbsolutePath());
                    processedCount++;
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Skipping invalid image file: " + file.getName(),
                            "Invalid File",
                            JOptionPane.WARNING_MESSAGE);
                }
            }

            refreshImagePreviews();
            notifyImagesChanged();
        }
    }

    /**
     * Refreshes the image preview panel
     */
    private void refreshImagePreviews() {
        imagePreviewPanel.removeAll();

        if (imagePaths.isEmpty()) {
            JLabel emptyLabel = new JLabel("No " + category + " images uploaded");
            emptyLabel.setHorizontalAlignment(JLabel.CENTER);
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setFont(emptyLabel.getFont().deriveFont(Font.ITALIC));
            imagePreviewPanel.add(emptyLabel);
        } else {
            for (int i = 0; i < imagePaths.size(); i++) {
                final int index = i;
                final String imagePath = imagePaths.get(i);

                JPanel imagePanel = createImagePreview(imagePath, index);
                imagePreviewPanel.add(imagePanel);
            }
        }

        imagePreviewPanel.revalidate();
        imagePreviewPanel.repaint();
    }

    /**
     * Creates a single image preview panel with delete button
     */
    private JPanel createImagePreview(String imagePath, int index) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(thumbnailSize, thumbnailSize + 30));
        panel.setBorder(BorderFactory.createRaisedBevelBorder());

        // Load and display thumbnail
        JLabel imageLabel;
        try {
            ImageIcon icon = loadThumbnail(imagePath);
            imageLabel = new JLabel(icon);
        } catch (Exception e) {
            imageLabel = new JLabel("Error loading image");
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
        }

        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(imageLabel, BorderLayout.CENTER);

        // Create delete button
        JButton deleteButton = new JButton("✕");
        deleteButton.setFont(deleteButton.getFont().deriveFont(Font.BOLD, 14f));
        deleteButton.setForeground(Color.RED);
        deleteButton.setPreferredSize(new Dimension(30, 25));
        deleteButton.setToolTipText("Delete this image");
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Delete this " + category + " image?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
                imagePaths.remove(index);
                refreshImagePreviews();
                notifyImagesChanged();
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(deleteButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Loads a thumbnail image
     */
    private ImageIcon loadThumbnail(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image image = originalIcon.getImage();

                // Scale image to thumbnail size
                Image scaledImage = image.getScaledInstance(
                        thumbnailSize - 20,
                        thumbnailSize - 20,
                        Image.SCALE_SMOOTH);

                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Error loading thumbnail: " + e.getMessage());
        }

        // Return placeholder if loading fails
        return ImageUtils.getPlaceholderImage(thumbnailSize - 20, thumbnailSize - 20);
    }

    /**
     * Creates an upload icon
     */
    private Image createUploadIcon() {
        int size = 16;
        java.awt.image.BufferedImage icon = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = icon.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw upload arrow
        g2.setColor(new Color(70, 130, 180));
        g2.setStroke(new BasicStroke(2));

        // Arrow shaft
        g2.drawLine(size / 2, size - 2, size / 2, 4);

        // Arrow head
        g2.drawLine(size / 2, 4, size / 2 - 3, 7);
        g2.drawLine(size / 2, 4, size / 2 + 3, 7);

        g2.dispose();
        return icon;
    }

    /**
     * Notifies listeners that images have changed
     */
    private void notifyImagesChanged() {
        if (onImagesChanged != null) {
            onImagesChanged.run();
        }
    }

    /**
     * Clears all images
     */
    public void clearImages() {
        imagePaths.clear();
        refreshImagePreviews();
        notifyImagesChanged();
    }

    /**
     * Gets the number of uploaded images
     */
    public int getImageCount() {
        return imagePaths.size();
    }
}
