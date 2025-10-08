package com.rentops.ai.router;

/**
 * Statistical context used by routing heuristics.
 */
public record ContextStats(int inputChars, int snippetTokens, int chunkCount, boolean varianceHigh) {

    public static ContextStats basic(int inputChars) {
        return new ContextStats(inputChars, 0, 0, false);
    }
}
