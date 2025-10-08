package com.rentops.ai.summarize;

import com.rentops.ai.client.LlmClient;
import com.rentops.ai.router.AiTask;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Summarizes individual chunks. Heuristic path only for now (AI disabled or no
 * key): - Take first sentence (<= 160 chars) plus extracted key phrases
 * (capitalized words & numbers) deduped. If AI is enabled, calls LLM with
 * CHUNK_SUMMARY task.
 */
public class ChunkSummarizerService {

    private static final Pattern KEY_TOKEN = Pattern.compile("(?<!\\p{Lu})([A-Z][a-zA-Z0-9]+)|\\b(\\d{2,})\\b");

    private final LlmClient llmClient;
    private final boolean aiEnabled;

    public ChunkSummarizerService(LlmClient llmClient, boolean aiEnabled) {
        this.llmClient = llmClient;
        this.aiEnabled = aiEnabled;
    }

    public String summarizeChunk(String chunk) {
        if (chunk == null || chunk.isBlank()) {
            return "";
        }
        if (!aiEnabled) {
            return heuristic(chunk);
        }
        try {
            var req = new LlmClient.LlmRequest("default-model", "You summarize chunks.", prompt(chunk), 0.2);
            return llmClient.chatWithTask(AiTask.CHUNK_SUMMARY, req).rawText();
        } catch (Exception e) {
            // fallback to heuristic
            return heuristic(chunk);
        }
    }

    private String heuristic(String chunk) {
        String firstSentence = chunk.split("(?<=[.!?])\\s+")[0];
        if (firstSentence.length() > 160) {
            firstSentence = firstSentence.substring(0, 157) + "...";
        }
        List<String> phrases = new ArrayList<>();
        var m = KEY_TOKEN.matcher(chunk);
        while (m.find()) {
            String token = m.group();
            if (!phrases.contains(token) && phrases.size() < 6) {
                phrases.add(token);
            }
        }
        if (!phrases.isEmpty()) {
            return firstSentence + " | " + String.join(", ", phrases);
        }
        return firstSentence;
    }

    private String prompt(String chunk) {
        return "Summarize the following text in <= 2 concise sentences highlighting key entities and numbers.\n\n" + chunk;
    }
}
