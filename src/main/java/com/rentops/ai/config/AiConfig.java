package com.rentops.ai.config;

import com.rentops.ai.util.Env;

/**
 * Central AI runtime configuration. Reads environment variables lazily and
 * exposes simple feature flags. All AI feature code should query this instead
 * of touching environment variables directly.
 */
public final class AiConfig {

    private static final String KEY_API = "GROQ_API_KEY";
    private static final String KEY_ENABLE = "AI_ENABLE"; // optional override

    private final String apiKey;
    private final boolean enabledExplicit;

    private AiConfig(String apiKey, boolean enabledExplicit) {
        this.apiKey = apiKey;
        this.enabledExplicit = enabledExplicit;
    }

    public static AiConfig load() {
        String key = Env.get(KEY_API);
        String enable = Env.get(KEY_ENABLE);
        boolean explicit = enable == null || enable.isBlank() ? true : enable.equalsIgnoreCase("true");
        return new AiConfig(key, explicit);
    }

    /**
     * Returns true if AI features should attempt remote calls.
     */
    public boolean isEnabled() {
        return enabledExplicit && apiKey != null && !apiKey.isBlank();
    }

    /**
     * API key or null if not configured.
     */
    public String apiKey() {
        return apiKey;
    }
}
