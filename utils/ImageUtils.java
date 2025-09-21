package utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import models.Car;

public class ImageUtils {

    private static final String[] SUPPORTED_EXT = {".jpg", ".jpeg", ".png", ".gif"};
    private static final String[] SUPPORTED_URL_PROTOCOLS = {"http://", "https://"};

    // Simple LRU-like image cache with a maximum size
    private static final int MAX_CACHE_SIZE = 50;
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();

    /**
     * Checks if a string is a URL (starts with http:// or https://)
     */
    public static boolean isUrl(String path) {
        if (path == null) {
            return false;
        }
        String lowerPath = path.toLowerCase();
        for (String protocol : SUPPORTED_URL_PROTOCOLS) {
            if (lowerPath.startsWith(protocol)) {
                return true;
            }
        }
        return false;
    }

    public static List<ImageIcon> loadCarCategoryImages(Car car, String category, int maxW, int maxH) {
        // Determine which URL to use based on the category
        String imageUrl = null;

        if (category != null && category.equalsIgnoreCase("interior")) {
            imageUrl = car.getInteriorImageUrl();
        } else {
            // For exterior or any other category, use exterior URL
            imageUrl = car.getExteriorImageUrl();
        }

        // First try to load from URL if car has imageUrl
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String urlPath = imageUrl;
            if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("interior") && !category.equalsIgnoreCase("exterior")) {
                // If category specified (other than interior/exterior), look for category-specific URLs
                if (!urlPath.endsWith("/")) {
                    urlPath += "/";
                }
                urlPath += category;
            }

            // If URL ends with /, assume it's a folder URL and try to load images
            // Functionality could be expanded to fetch folder listings, but that's more complex
            // For now, we'll try common image names
            if (urlPath.endsWith("/")) {
                List<ImageIcon> icons = loadCommonImagesFromUrl(urlPath, maxW, maxH);
                if (!icons.isEmpty()) {
                    return icons;
                }
            } // Check if URL directly points to an image
            else if (isImageUrl(urlPath)) {
                ImageIcon icon = loadFromUrl(urlPath, maxW, maxH);
                if (icon != null) {
                    List<ImageIcon> icons = new ArrayList<>();
                    icons.add(icon);
                    return icons;
                }
            }
        }

        // Fall back to local folders
        List<String> candidates = possiblePathsForCar(car, category);
        for (String path : candidates) {
            List<ImageIcon> icons = loadImageIconsFromFolder(path, maxW, maxH);
            if (!icons.isEmpty()) {
                return icons;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Check if URL points to an image based on extension
     */
    private static boolean isImageUrl(String url) {
        return hasSupportedExt(url);
    }

    /**
     * Try to load common image names from a URL folder
     */
    private static List<ImageIcon> loadCommonImagesFromUrl(String baseUrl, int maxW, int maxH) {
        List<ImageIcon> icons = new ArrayList<>();
        String[] commonNames = {"1.jpg", "2.jpg", "3.jpg", "front.jpg", "rear.jpg", "side.jpg",
            "interior.jpg", "dashboard.jpg", "1.png", "2.png", "3.png", "main.jpg"};

        for (String name : commonNames) {
            String url = baseUrl;
            if (!baseUrl.endsWith("/")) {
                url += "/";
            }
            url += name;

            ImageIcon icon = loadFromUrl(url, maxW, maxH);
            if (icon != null) {
                icons.add(icon);
            }
        }

        return icons;
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
                String filePath = f.getAbsolutePath();
                String cacheKey = "file:" + filePath + "-" + maxW + "x" + maxH;

                ImageIcon icon = getCachedImage(cacheKey, () -> {
                    ImageIcon originalIcon = new ImageIcon(filePath);
                    Image scaled = scaleToFit(originalIcon.getImage(), maxW, maxH);
                    return new ImageIcon(scaled);
                });

                if (icon != null) {
                    result.add(icon);
                }
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

    /**
     * Loads an image from a URL
     *
     * @param urlString The URL to load from
     * @param maxW Maximum width
     * @param maxH Maximum height
     * @return The loaded ImageIcon or null if failed
     */
    public static ImageIcon loadFromUrl(String urlString, int maxW, int maxH) {
        // Generate a unique cache key based on URL and dimensions
        String cacheKey = "url:" + urlString + "-" + maxW + "x" + maxH;

        return getCachedImage(cacheKey, () -> {
            try {
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                // Set user-agent to avoid some server restrictions
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000); // 5 second timeout
                conn.setReadTimeout(10000);   // 10 second timeout

                BufferedImage img = ImageIO.read(conn.getInputStream());
                if (img == null) {
                    return null;
                }

                Image scaled = scaleToFit(img, maxW, maxH);
                return new ImageIcon(scaled);
            } catch (IOException e) {
                System.err.println("Error loading image from URL: " + urlString);
                // Log error but don't print full stack trace
                System.err.println("Error: " + e.getMessage());
                return null;
            }
        });
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

    /**
     * Get a cached image or add it to the cache if not present
     *
     * @param key The cache key
     * @param creator Function to create the image if not in cache
     * @return The cached or newly created image
     */
    private static ImageIcon getCachedImage(String key, java.util.function.Supplier<ImageIcon> creator) {
        // Check if image is in cache
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }

        // Create new image
        ImageIcon image = creator.get();

        // If created successfully, add to cache
        if (image != null) {
            // If cache is full, remove an entry (could implement more sophisticated LRU)
            if (imageCache.size() >= MAX_CACHE_SIZE) {
                String keyToRemove = imageCache.keySet().iterator().next();
                imageCache.remove(keyToRemove);
            }

            // Add to cache
            imageCache.put(key, image);
        }

        return image;
    }

    /**
     * Clear the image cache
     */
    public static void clearCache() {
        imageCache.clear();
    }

    private static String sanitize(String s) {
        // Replace any non-alphanumeric, non-underscore, non-dash with underscore
        return s.replaceAll("[^A-Za-z0-9_\\-]", "_");
    }
}
