package com.rentops.ai.util;

/**
 * Utility methods to salvage near-JSON from LLM responses.
 */
public class JsonCleaningUtil {

    public static String extractFirstJsonObject(String text) {
        if (text == null) {
            return null;
        }
        int firstBrace = text.indexOf('{');
        if (firstBrace < 0) {
            return null;
        }
        int depth = 0;
        for (int i = firstBrace; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '{') {
                depth++; 
            }else if (c == '}') {
                depth--;
                if (depth == 0) {
                    return text.substring(firstBrace, i + 1);
                }
            }
        }
        return text.substring(firstBrace); // fallback (maybe truncated)
    }

    public static String stripCodeFences(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("(?s)```[a-zA-Z0-9]*", "").replace("```", "").trim();
    }
}
