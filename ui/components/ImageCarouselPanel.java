package ui.components;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;

public class ImageCarouselPanel extends JPanel {

    private final JLabel mainImageLabel;
    private final JButton prevBtn;
    private final JButton nextBtn;
    private final JPanel thumbs;
    private int index = 0;
    private List<ImageIcon> images;

    public ImageCarouselPanel(List<ImageIcon> images) {
        setLayout(new BorderLayout(8, 8));
        this.images = images;
        mainImageLabel = new JLabel("No images", SwingConstants.CENTER);
        mainImageLabel.setOpaque(true);
        mainImageLabel.setBackground(new Color(245, 247, 250));
        add(mainImageLabel, BorderLayout.CENTER);

        JPanel controls = new JPanel(new BorderLayout());
        prevBtn = new JButton("◀");
        nextBtn = new JButton("▶");
        JPanel center = new JPanel();
        center.add(prevBtn);
        center.add(nextBtn);
        controls.add(center, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);

        thumbs = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JScrollPane thumbScroll = new JScrollPane(thumbs);
        thumbScroll.setPreferredSize(new Dimension(120, 100));
        add(thumbScroll, BorderLayout.NORTH);

        prevBtn.addActionListener(e -> showIndex(index - 1));
        nextBtn.addActionListener(e -> showIndex(index + 1));

        rebuild();
    }

    private void rebuild() {
        thumbs.removeAll();
        if (images == null || images.isEmpty()) {
            // Create a placeholder image
            mainImageLabel.setText("");
            mainImageLabel.setIcon(createPlaceholderImage(mainImageLabel.getWidth(), mainImageLabel.getHeight()));
            prevBtn.setEnabled(false);
            nextBtn.setEnabled(false);
            return;
        }
        prevBtn.setEnabled(true);
        nextBtn.setEnabled(true);
        for (int i = 0; i < images.size(); i++) {
            int idx = i;
            ImageIcon icon = images.get(i);
            Image img = icon.getImage().getScaledInstance(80, 60, Image.SCALE_SMOOTH);
            JLabel thumb = new JLabel(new ImageIcon(img));
            thumb.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            thumb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            thumb.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    showIndex(idx);
                }
            });
            thumbs.add(thumb);
        }
        showIndex(Math.min(index, images.size() - 1));
        revalidate();
        repaint();
    }

    private void showIndex(int newIndex) {
        if (images == null || images.isEmpty()) {
            return;
        }
        if (newIndex < 0) {
            newIndex = images.size() - 1;
        }
        if (newIndex >= images.size()) {
            newIndex = 0;
        }
        this.index = newIndex;
        mainImageLabel.setText("");
        mainImageLabel.setIcon(images.get(index));
    }

    public void setImages(List<ImageIcon> images) {
        this.images = images;
        rebuild();
    }

    /**
     * Creates a placeholder image with a car icon
     *
     * @param width Desired width (will use defaults if <= 0)
     * @param height Desired height (will use defaults if <= 0)
     * @return ImageIcon with placeholder graphic
     */
    private ImageIcon createPlaceholderImage(int width, int height) {
        // Use reasonable defaults if dimensions are not valid
        int w = width > 0 ? width : 320;
        int h = height > 0 ? height : 240;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        // Fill background with a light gray gradient
        g2.setPaint(new GradientPaint(0, 0, new Color(245, 245, 245),
                0, h, new Color(230, 230, 230)));
        g2.fillRect(0, 0, w, h);

        // Draw a rounded rectangle border
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(10, 10, w - 20, h - 20, 20, 20);

        // Draw a simple car icon
        int carWidth = w / 2;
        int carHeight = h / 3;
        int carX = (w - carWidth) / 2;
        int carY = (h - carHeight) / 2;

        // Car body
        g2.setColor(new Color(180, 180, 180));
        g2.fillRoundRect(carX, carY, carWidth, carHeight / 2, 10, 10);
        g2.fillRect(carX + carWidth / 5, carY - carHeight / 4, carWidth * 3 / 5, carHeight / 4);

        // Wheels
        int wheelSize = carHeight / 4;
        g2.setColor(new Color(80, 80, 80));
        g2.fillOval(carX + carWidth / 5 - wheelSize / 2, carY + carHeight / 2 - wheelSize / 2,
                wheelSize, wheelSize);
        g2.fillOval(carX + carWidth * 4 / 5 - wheelSize / 2, carY + carHeight / 2 - wheelSize / 2,
                wheelSize, wheelSize);

        // Draw "No Image Available" text
        g2.setColor(new Color(100, 100, 100));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        String message = "No Image Available";
        int textWidth = fm.stringWidth(message);
        g2.drawString(message, (w - textWidth) / 2, carY + carHeight + 30);

        g2.dispose();
        return new ImageIcon(img);
    }
}
