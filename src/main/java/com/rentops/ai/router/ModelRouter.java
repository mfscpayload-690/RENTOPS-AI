package com.rentops.ai.router;

import com.rentops.ai.config.ModelRegistry;

/**
 * Chooses a model ID based on task and contextual statistics.
 */
public class ModelRouter {

    private final ModelRegistry registry;

    public ModelRouter(ModelRegistry registry) {
        this.registry = registry;
    }

    public String pickModel(AiTask task, ContextStats stats, int jsonFailures, boolean multiDataSources) {
        return switch (task) {
            case INTENT ->
                (stats.inputChars() <= 300 ? registry.qwen32B() : registry.llama4Scout());
            case FAQ_ANSWER ->
                (stats.snippetTokens() > 1500 ? registry.gptOss20B() : registry.llama4Scout());
            case FAQ_RERANK ->
                registry.qwen32B();
            case CHUNK_SUMMARY ->
                registry.gptOss20B();
            case MERGE_SUMMARY ->
                (stats.chunkCount() > 4 || stats.varianceHigh() ? registry.gptOss120B() : registry.gptOss20B());
            case EXEC_NARRATIVE ->
                (multiDataSources ? registry.gptOss120B() : registry.llama3p370B());
            case SAFETY_CLASSIFY ->
                registry.llamaGuard();
            case SPEECH_TO_TEXT ->
                registry.whisperTurbo();
            case TTS ->
                registry.playAiTts();
        };
    }
}
