package com.rentops.ai.router;

/**
 * Enumerates distinct AI task categories for routing.
 */
public enum AiTask {
    INTENT,
    FAQ_ANSWER,
    FAQ_RERANK,
    CHUNK_SUMMARY,
    MERGE_SUMMARY,
    EXEC_NARRATIVE,
    SAFETY_CLASSIFY,
    SPEECH_TO_TEXT,
    TTS
}
