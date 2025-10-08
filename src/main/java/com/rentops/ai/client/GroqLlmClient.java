package com.rentops.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentops.ai.config.AiConfig;
import com.rentops.ai.exceptions.AiException;
import okhttp3.*;

import java.io.IOException;
import java.time.Instant;

/**
 * Minimal Groq client (chat completions). Expanded later with retries, JSON
 * repair, logging.
 */
public class GroqLlmClient implements LlmClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient http = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final AiConfig config;
    private final String endpoint = "https://api.groq.com/openai/v1/chat/completions"; // adjust if different

    public GroqLlmClient(AiConfig config) {
        this.config = config;
    }

    @Override
    public LlmResponse chat(LlmRequest request) throws AiException {
        if (!config.isEnabled()) {
            throw new AiException("AI disabled (no key)");
        }
        long start = System.nanoTime();
        try {
            String bodyJson = mapper.createObjectNode()
                    .put("model", request.model())
                    .put("temperature", request.temperature())
                    .set("messages", mapper.createArrayNode()
                            .add(mapper.createObjectNode().put("role", "system").put("content", request.systemPrompt()))
                            .add(mapper.createObjectNode().put("role", "user").put("content", request.userPrompt()))
                    ).toString();
            Request httpReq = new Request.Builder()
                    .url(endpoint)
                    .header("Authorization", "Bearer " + config.apiKey())
                    .header("Accept", "application/json")
                    .post(RequestBody.create(bodyJson, JSON))
                    .build();
            try (Response resp = http.newCall(httpReq).execute()) {
                if (!resp.isSuccessful()) {
                    throw new AiException("Groq HTTP " + resp.code());
                }
                String respBody = resp.body() != null ? resp.body().string() : "";
                JsonNode root = mapper.readTree(respBody);
                JsonNode choices = root.get("choices");
                String text = choices != null && choices.size() > 0 ? choices.get(0).get("message").get("content").asText("") : "";
                long latency = (System.nanoTime() - start) / 1_000_000L;
                return new LlmResponse(text, request.model(), latency);
            }
        } catch (IOException e) {
            throw new AiException("Groq IO", e);
        }
    }
}
