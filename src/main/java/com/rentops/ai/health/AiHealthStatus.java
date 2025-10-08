package com.rentops.ai.health;

/**
 * Immutable snapshot of AI subsystem status.
 */
public record AiHealthStatus(boolean enabled, long lastSuccessEpochMs, String lastError) {

}
