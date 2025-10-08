package com.rentops.ai.logging;

import java.time.Instant;

/**
 * Immutable representation of an AI query log entry.
 */
public record AiQueryLog(
        String task,
        String model,
        String promptHash,
        int promptChars,
        int responseChars,
        long latencyMs,
        boolean success,
        String errorType,
        Instant createdAt
        ) {

    public static AiQueryLog now(String task, String model, String promptHash, int promptChars, int responseChars, long latencyMs, boolean success, String errorType) {
        return new AiQueryLog(task, model, promptHash, promptChars, responseChars, latencyMs, success, errorType, Instant.now());
    }
}
