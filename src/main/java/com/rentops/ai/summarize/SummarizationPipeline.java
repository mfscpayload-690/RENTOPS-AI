package com.rentops.ai.summarize;

import java.util.ArrayList;
import java.util.List;

/**
 * End-to-end textual summarization pipeline using splitter->chunker->chunk
 * summarizer->merge summarizer.
 */
public class SummarizationPipeline {

    private final int targetChunkChars;
    private final ChunkSummarizerService chunkSummarizer;
    private final MergeSummarizerService mergeSummarizer;

    public SummarizationPipeline(int targetChunkChars,
            ChunkSummarizerService chunkSummarizer,
            MergeSummarizerService mergeSummarizer) {
        this.targetChunkChars = targetChunkChars;
        this.chunkSummarizer = chunkSummarizer;
        this.mergeSummarizer = mergeSummarizer;
    }

    public Result summarize(String text) {
        long start = System.currentTimeMillis();
        List<String> sentences = SentenceSplitter.split(text);
        List<String> chunks = Chunker.chunk(sentences, targetChunkChars);
        List<String> chunkSummaries = new ArrayList<>();
        for (String c : chunks) {
            chunkSummaries.add(chunkSummarizer.summarizeChunk(c));
        }
        String finalSummary = mergeSummarizer.merge(chunkSummaries);
        long elapsedMs = System.currentTimeMillis() - start;
        return new Result(finalSummary, chunks.size(), averageLength(chunks), elapsedMs, chunkSummaries);
    }

    private double averageLength(List<String> chunks) {
        if (chunks.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (String c : chunks) {
            total += c.length();
        }
        return total / (double) chunks.size();
    }

    public record Result(String summary,
            int chunkCount,
            double avgChunkLength,
            long elapsedMs,
            List<String> chunkSummaries) {

    }
}
