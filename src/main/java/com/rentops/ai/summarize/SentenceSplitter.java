package com.rentops.ai.summarize;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple sentence splitter (naive punctuation-based).
 */
public final class SentenceSplitter {

    private static final Pattern SENTENCE_END = Pattern.compile("(?<=[.!?])\\s+");

    private SentenceSplitter() {
    }

    public static List<String> split(String text) {
        List<String> list = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return list;
        }
        for (String part : SENTENCE_END.split(text.trim())) {
            String s = part.trim();
            if (!s.isEmpty()) {
                list.add(s);
            }
        }
        return list;
    }
}
