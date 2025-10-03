package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import models.Car;

public class ImageUtils {

    private static final String[] SUPPORTED_EXT = {".jpg", ".jpeg", ".png", ".gif"};
    private static final String IMAGES_BASE_PATH = "ui" + File.separator + "components" + File.separator + "images";
    private static final String CARS_PATH = IMAGES_BASE_PATH + File.separator + "cars";
    private static final String PLACEHOLDER_PATH = IMAGES_BASE_PATH + File.separator + "placeholder.jpg";
    private static final int DEFAULT_IMAGE_WIDTH = 800;
    private static final int DEFAULT_IMAGE_HEIGHT = 600;

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

    // ==================== NEW MULTI-IMAGE METHODS ====================
    /**
     * Validates if a file is a supported image format
     */
    public static boolean isValidImageFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        String name = file.getName().toLowerCase();
        return Arrays.stream(SUPPORTED_EXT).anyMatch(name::endsWith);
    }

    /**
     * Creates the directory structure for a car's images
     */
    public static boolean createCarImageDirectories(int carId) {
        try {
            String carPath = CARS_PATH + File.separator + carId;
            String exteriorPath = carPath + File.separator + "exterior";
            String interiorPath = carPath + File.separator + "interior";

            new File(exteriorPath).mkdirs();
            new File(interiorPath).mkdirs();
            return true;
        } catch (Exception e) {
            System.err.println("Failed to create directories for car " + carId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Copies and resizes an image file to the car's directory
     */
    public static String saveCarImage(File sourceFile, int carId, String category, int imageIndex) {
        if (!isValidImageFile(sourceFile)) {
            return null;
        }

        try {
            createCarImageDirectories(carId);

            String extension = getFileExtension(sourceFile.getName());
            String fileName = category + "_" + imageIndex + extension;
            String targetPath = CARS_PATH + File.separator + carId + File.separator + category + File.separator + fileName;

            // Load and resize image
            BufferedImage originalImage = ImageIO.read(sourceFile);
            if (originalImage == null) {
                return null;
            }

            BufferedImage resizedImage = resizeImageToFit(originalImage, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);

            // Save resized image
            File targetFile = new File(targetPath);
            ImageIO.write(resizedImage, extension.substring(1), targetFile);

            return targetPath;
        } catch (IOException e) {
            System.err.println("Failed to save image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads all images for a car category (interior/exterior)
     */
    public static List<ImageIcon> loadCarImages(int carId, String category, int maxW, int maxH) {
        List<ImageIcon> images = new ArrayList<>();
        String categoryPath = CARS_PATH + File.separator + carId + File.separator + category;

        File categoryDir = new File(categoryPath);
        if (!categoryDir.exists() || !categoryDir.isDirectory()) {
            // Return placeholder image
            images.add(getPlaceholderImage(maxW, maxH));
            return images;
        }

        File[] files = categoryDir.listFiles((dir, name) -> isValidImageFile(new File(dir, name)));
        if (files == null || files.length == 0) {
            images.add(getPlaceholderImage(maxW, maxH));
            return images;
        }

        Arrays.sort(files); // Sort by filename for consistent ordering

        for (File file : files) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    Image scaled = scaleToFit(img, maxW, maxH);
                    images.add(new ImageIcon(scaled));
                }
            } catch (IOException e) {
                System.err.println("Failed to load image: " + file.getAbsolutePath());
            }
        }

        if (images.isEmpty()) {
            images.add(getPlaceholderImage(maxW, maxH));
        }

        return images;
    }

    /**
     * Gets the placeholder image
     */
    public static ImageIcon getPlaceholderImage(int maxW, int maxH) {
        try {
            File placeholderFile = new File(PLACEHOLDER_PATH);
            if (placeholderFile.exists()) {
                BufferedImage img = ImageIO.read(placeholderFile);
                if (img != null) {
                    Image scaled = scaleToFit(img, maxW, maxH);
                    return new ImageIcon(scaled);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load placeholder image: " + e.getMessage());
        }

        // Fallback: create a simple colored rectangle
        BufferedImage placeholder = new BufferedImage(maxW, maxH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = placeholder.createGraphics();
        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(0, 0, maxW, maxH);
        g2.setColor(Color.GRAY);
        g2.drawRect(0, 0, maxW - 1, maxH - 1);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("No Image", maxW / 2 - 50, maxH / 2);
        g2.dispose();

        return new ImageIcon(placeholder);
    }

    /**
     * Deletes all images for a specific car
     */
    public static boolean deleteCarImages(int carId) {
        try {
            String carPath = CARS_PATH + File.separator + carId;
            File carDir = new File(carPath);

            if (carDir.exists()) {
                return deleteDirectory(carDir);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to delete images for car " + carId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a specific image file
     */
    public static boolean deleteCarImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                return imageFile.delete();
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to delete image: " + imagePath + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Resizes image to fit within specified dimensions while maintaining aspect
     * ratio
     */
    private static BufferedImage resizeImageToFit(BufferedImage original, int maxWidth, int maxHeight) {
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        double scaleFactor = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);

        int scaledWidth = (int) (originalWidth * scaleFactor);
        int scaledHeight = (int) (originalHeight * scaleFactor);

        BufferedImage resized = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(original, 0, 0, scaledWidth, scaledHeight, null);
        g2.dispose();

        return resized;
    }

    /**
     * Gets file extension from filename
     */
    private static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }

    /**
     * Recursively deletes a directory and all its contents
     */
    private static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!deleteDirectory(file)) {
                        return false;
                    }
                }
            }
        }
        return directory.delete();
    }

    /**
     * Gets list of image file paths from a car directory
     */
    public static List<String> getCarImagePaths(int carId, String category) {
        List<String> imagePaths = new ArrayList<>();
        String categoryPath = CARS_PATH + File.separator + carId + File.separator + category;

        File categoryDir = new File(categoryPath);
        if (!categoryDir.exists() || !categoryDir.isDirectory()) {
            return imagePaths;
        }

        File[] files = categoryDir.listFiles((dir, name) -> isValidImageFile(new File(dir, name)));
        if (files != null) {
            Arrays.sort(files);
            for (File file : files) {
                imagePaths.add(file.getAbsolutePath());
            }
        }

        return imagePaths;
    }

    private static String sanitize(String s) {
        // Replace any non-alphanumeric, non-underscore, non-dash with underscore
        return s.replaceAll("[^A-Za-z0-9_\\-]", "_");
    }
}
