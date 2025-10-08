package com.rentops.ai.summarize;

import java.util.ArrayList;
import java.util.List;

/**
 * Groups sentences into chunks up to a max character target.
 */
public final class Chunker {

    private Chunker() {
    }

    public static List<String> chunk(List<String> sentences, int targetChars) {
        List<String> out = new ArrayList<>();
        if (sentences == null || sentences.isEmpty()) {
            return out;
        }
        StringBuilder current = new StringBuilder();
        for (String s : sentences) {
            if (current.length() + s.length() + 1 > targetChars && current.length() > 0) {
                out.add(current.toString().trim());
                current.setLength(0);
            }
            current.append(s).append(' ');
        }
        if (current.length() > 0) {
            out.add(current.toString().trim());
        }
        return out;
    }
}
