package com.rentops.ai.util;

import java.util.regex.Pattern;

/**
 * Minimal PII redaction utility (expand later).
 */
public final class Redactor {

    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}");
    private static final Pattern PHONE = Pattern.compile("\\b\\d{7,15}\\b");

    private Redactor() {
    }

    public static String redact(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        String out = EMAIL.matcher(input).replaceAll("EMAIL_REDACTED");
        out = PHONE.matcher(out).replaceAll("PHONE_REDACTED");
        return out;
    }
}
