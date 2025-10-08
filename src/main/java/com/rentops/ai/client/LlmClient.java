package com.rentops.ai.client;

import com.rentops.ai.exceptions.AiException;
import com.rentops.ai.router.AiTask;

/**
 * Abstraction for LLM interactions. Future: streaming, embeddings.
 */
public interface LlmClient {

    LlmResponse chat(LlmRequest request) throws AiException;

    /**
     * Optional task-aware call. Default just delegates to
     * {@link #chat(LlmRequest)}.
     */
    default LlmResponse chatWithTask(AiTask task, LlmRequest request) throws AiException {
        return chat(request);
    }

    record LlmRequest(String model, String systemPrompt, String userPrompt, double temperature) {

    }

    record LlmResponse(String rawText, String model, long latencyMs) {

    }
}
