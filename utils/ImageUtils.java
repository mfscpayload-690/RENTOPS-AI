package utils;

import models.Car;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageUtils {

    private static final String[] SUPPORTED_EXT = {".jpg", ".jpeg", ".png", ".gif"};

    public static List<ImageIcon> loadCarCategoryImages(Car car, String category, int maxW, int maxH) {
        List<String> candidates = possiblePathsForCar(car, category);
        for (String path : candidates) {
            List<ImageIcon> icons = loadImageIconsFromFolder(path, maxW, maxH);
            if (!icons.isEmpty()) {
                return icons;
            }
        }
        return new ArrayList<>();
    }

    public static List<String> possiblePathsForCar(Car car, String category) {
        List<String> paths = new ArrayList<>();
        String base = "assets" + File.separator + "cars" + File.separator;
        // 1) by numeric id
        paths.add(base + car.getId() + File.separator + category);
        // 2) by license plate
        if (car.getLicensePlate() != null && !car.getLicensePlate().isEmpty()) {
            paths.add(base + sanitize(car.getLicensePlate()) + File.separator + category);
        }
        // 3) by make_model_year
        String name = (car.getMake() + "_" + car.getModel() + "_" + car.getYear());
        paths.add(base + sanitize(name) + File.separator + category);
        return paths;
    }

    public static List<ImageIcon> loadImageIconsFromFolder(String folderPath, int maxW, int maxH) {
        List<ImageIcon> result = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            return result;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return result;
        }

        Arrays.sort(files);
        for (File f : files) {
            if (f.isFile() && hasSupportedExt(f.getName())) {
                ImageIcon icon = new ImageIcon(f.getAbsolutePath());
                Image scaled = scaleToFit(icon.getImage(), maxW, maxH);
                result.add(new ImageIcon(scaled));
            }
        }
        return result;
    }

    private static boolean hasSupportedExt(String name) {
        String lower = name.toLowerCase();
        for (String ext : SUPPORTED_EXT) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static Image scaleToFit(Image img, int maxW, int maxH) {
        if (img == null) {
            return null;
        }
        int iw = img.getWidth(null);
        int ih = img.getHeight(null);
        if (iw <= 0 || ih <= 0) {
            return img;
        }
        double rw = (double) maxW / iw;
        double rh = (double) maxH / ih;
        double r = Math.min(rw, rh);
        int w = Math.max(1, (int) Math.round(iw * r));
        int h = Math.max(1, (int) Math.round(ih * r));
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(scaled, 0, 0, null);
        g2.dispose();
        return out;
    }

    private static String sanitize(String s) {
        // Replace any non-alphanumeric, non-underscore, non-dash with underscore
        return s.replaceAll("[^A-Za-z0-9_\\-]", "_");
    }
}
