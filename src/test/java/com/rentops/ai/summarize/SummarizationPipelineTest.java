package com.rentops.ai.summarize;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SummarizationPipelineTest {

    @Test
    void heuristicPipelineProducesSummaryWithoutAi() {
        String text = "RentOps AI provides advanced analytics. It helps admins manage fleets. Users can book cars quickly! The system logs AI queries. Summarization condenses long reports.";
        // heuristic services (ai disabled)
        ChunkSummarizerService chunkSummarizer = new ChunkSummarizerService(new NoopLlmClient(), false);
        MergeSummarizerService mergeSummarizer = new MergeSummarizerService(new NoopLlmClient(), false);
        SummarizationPipeline pipeline = new SummarizationPipeline(120, chunkSummarizer, mergeSummarizer);
        SummarizationPipeline.Result result = pipeline.summarize(text);
        assertTrue(result.summary().length() > 0, "Summary should not be empty");
        assertTrue(result.chunkCount() >= 1, "Should have at least one chunk");
        assertEquals(result.chunkSummaries().size(), result.chunkCount());
    }

    // Minimal stub to satisfy interface without requiring real LLM
    static class NoopLlmClient implements com.rentops.ai.client.LlmClient {

        @Override
        public com.rentops.ai.client.LlmClient.LlmResponse chat(com.rentops.ai.client.LlmClient.LlmRequest request) {
            return new com.rentops.ai.client.LlmClient.LlmResponse("", "noop", 0);
        }
    }
}
