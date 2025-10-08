package com.rentops.ai.safety;

import com.rentops.ai.exceptions.AiException;

/**
 * Permissive safety service used when AI disabled or guard model unavailable.
 */
public class NoopSafetyService implements SafetyService {

    @Override
    public SafetyResult classify(String userText) throws AiException {
        return new SafetyResult(true, "no-check");
    }
}
