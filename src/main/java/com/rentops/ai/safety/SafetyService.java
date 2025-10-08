package com.rentops.ai.safety;

import com.rentops.ai.exceptions.AiException;

public interface SafetyService {

    SafetyResult classify(String userText) throws AiException;

    record SafetyResult(boolean allowed, String reason) {

    }
}
