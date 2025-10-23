package ui.components;

import utils.ModernTheme;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel for displaying a carousel of images with navigation
 */
public class ImageCarouselPanel extends JPanel {
    private List<String> imagePaths;
    private int currentIndex = 0;
    private JLabel imageLabel;
    private JLabel counterLabel;
    private JButton prevButton;
    private JButton nextButton;
    private int displayWidth = 600;
    private int displayHeight = 400;

    public ImageCarouselPanel(List<String> imagePaths) {
        this.imagePaths = imagePaths;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(ModernTheme.BG_CARD);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Image display area
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setBackground(ModernTheme.BG_DARK);
        imageLabel.setOpaque(true);
        imageLabel.setBorder(BorderFactory.createLineBorder(ModernTheme.BORDER_COLOR, 2));
        imageLabel.setPreferredSize(new Dimension(displayWidth, displayHeight));
        add(imageLabel, BorderLayout.CENTER);

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        navPanel.setBackground(ModernTheme.BG_CARD);

        prevButton = new JButton("← Previous");
        prevButton.setFont(ModernTheme.FONT_REGULAR);
        prevButton.addActionListener(e -> showPrevious());

        counterLabel = new JLabel();
        counterLabel.setFont(ModernTheme.FONT_BOLD);
        counterLabel.setForeground(ModernTheme.TEXT_PRIMARY);

        nextButton = new JButton("Next →");
        nextButton.setFont(ModernTheme.FONT_REGULAR);
        nextButton.addActionListener(e -> showNext());

        navPanel.add(prevButton);
        navPanel.add(counterLabel);
        navPanel.add(nextButton);
        add(navPanel, BorderLayout.SOUTH);

        // Display first image
        displayImage();
    }

    public ImageCarouselPanel(List<String> imagePaths, int width, int height) {
        this(imagePaths);
        this.displayWidth = width;
        this.displayHeight = height;
        imageLabel.setPreferredSize(new Dimension(width, height));
    }

    private void displayImage() {
        if (imagePaths == null || imagePaths.isEmpty()) {
            imageLabel.setIcon(null);
            imageLabel.setText("No images available");
            imageLabel.setForeground(ModernTheme.TEXT_SECONDARY);
            counterLabel.setText("0 / 0");
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
            return;
        }

        // Load and display current image
        try {
            String path = imagePaths.get(currentIndex);
            ImageIcon icon = new ImageIcon(path);
            
            // Scale image to fit
            Image scaledImage = icon.getImage().getScaledInstance(
                displayWidth - 10, displayHeight - 10, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setText(null);
        } catch (Exception e) {
            imageLabel.setIcon(null);
            imageLabel.setText("Error loading image");
            imageLabel.setForeground(ModernTheme.ERROR_RED);
        }

        // Update counter and buttons
        counterLabel.setText((currentIndex + 1) + " / " + imagePaths.size());
        prevButton.setEnabled(currentIndex > 0);
        nextButton.setEnabled(currentIndex < imagePaths.size() - 1);
    }

    private void showPrevious() {
        if (currentIndex > 0) {
            currentIndex--;
            displayImage();
        }
    }

    private void showNext() {
        if (imagePaths != null && currentIndex < imagePaths.size() - 1) {
            currentIndex++;
            displayImage();
        }
    }

    public void setImages(List<String> imagePaths) {
        this.imagePaths = imagePaths;
        this.currentIndex = 0;
        displayImage();
    }
}
