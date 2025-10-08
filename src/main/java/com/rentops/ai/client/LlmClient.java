package com.rentops.ai.client;

import com.rentops.ai.exceptions.AiException;

/**
 * Abstraction for LLM interactions. Future: streaming, embeddings.
 */
public interface LlmClient {

    LlmResponse chat(LlmRequest request) throws AiException;

    record LlmRequest(String model, String systemPrompt, String userPrompt, double temperature) {

    }

    record LlmResponse(String rawText, String model, long latencyMs) {

    }
}
