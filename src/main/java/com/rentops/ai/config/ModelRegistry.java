package com.rentops.ai.config;

/**
 * Central place mapping logical model roles to concrete Groq model IDs.
 */
public final class ModelRegistry {

    private final String llama4Scout = "llama-4-scout";
    private final String gptOss20B = "gpt-oss-20b";
    private final String gptOss120B = "gpt-oss-120b";
    private final String llama3p370B = "llama-3.3-70b";
    private final String qwen32B = "qwen-3-32b";
    private final String llamaGuard = "llama-guard";
    private final String whisperTurbo = "whisper-large-v3-turbo";
    private final String playAiTts = "playai-tts";

    public String llama4Scout() {
        return llama4Scout;
    }

    public String gptOss20B() {
        return gptOss20B;
    }

    public String gptOss120B() {
        return gptOss120B;
    }

    public String llama3p370B() {
        return llama3p370B;
    }

    public String qwen32B() {
        return qwen32B;
    }

    public String llamaGuard() {
        return llamaGuard;
    }

    public String whisperTurbo() {
        return whisperTurbo;
    }

    public String playAiTts() {
        return playAiTts;
    }
}
