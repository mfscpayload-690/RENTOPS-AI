package com.rentops.ai.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Simple .env loader (key=value per line). Values in real environment override
 * file.
 */
public final class Env {

    private static final Properties PROPS = new Properties();
    private static volatile boolean loaded = false;

    private Env() {
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (Env.class) {
            if (loaded) {
                return;
            }
            Path p = Path.of(".env");
            if (Files.exists(p)) {
                try (FileInputStream fis = new FileInputStream(p.toFile())) {
                    PROPS.load(fis);
                } catch (IOException ignored) {
                }
            }
            loaded = true;
        }
    }

    public static String get(String key) {
        ensureLoaded();
        String sys = System.getenv(key);
        return (sys != null && !sys.isBlank()) ? sys : PROPS.getProperty(key);
    }
}
