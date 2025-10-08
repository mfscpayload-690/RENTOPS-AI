package com.rentops.ai.summarize;

import com.rentops.ai.client.LlmClient;
import com.rentops.ai.router.AiTask;
import java.util.List;

/**
 * Combines chunk summaries into a final summary. Heuristic path: join unique
 * sentences up to 3.
 */
public class MergeSummarizerService {

    private final LlmClient llmClient;
    private final boolean aiEnabled;

    public MergeSummarizerService(LlmClient llmClient, boolean aiEnabled) {
        this.llmClient = llmClient;
        this.aiEnabled = aiEnabled;
    }

    public String merge(List<String> chunkSummaries) {
        if (chunkSummaries == null || chunkSummaries.isEmpty()) {
            return "";
        }
        if (!aiEnabled) {
            return heuristic(chunkSummaries);
        }
        try {
            String merged = String.join("\n", chunkSummaries);
            var req = new LlmClient.LlmRequest("default-model", "You merge chunk summaries.", prompt(merged), 0.2);
            return llmClient.chatWithTask(AiTask.MERGE_SUMMARY, req).rawText();
        } catch (Exception e) {
            return heuristic(chunkSummaries);
        }
    }

    private String heuristic(List<String> chunkSummaries) {
        StringBuilder sb = new StringBuilder();
        int added = 0;
        for (String cs : chunkSummaries) {
            if (cs == null || cs.isBlank()) {
                continue;
            }
            String sentence = cs.split("\\|")[0].trim();
            if (sb.indexOf(sentence) < 0) {
                if (added > 0) {
                    sb.append(' ');
                }
                sb.append(sentence);
                added++;
            }
            if (added >= 3) {
                break;
            }
        }
        return sb.toString();
    }

    private String prompt(String merged) {
        return "Given these chunk summaries, produce a cohesive summary (<= 4 sentences) retaining key facts and numbers.\n\n" + merged;
    }
}
