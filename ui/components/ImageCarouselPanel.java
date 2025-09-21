package ui.components;

import java.awt.*;
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
            mainImageLabel.setText("No images");
            mainImageLabel.setIcon(null);
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
}
