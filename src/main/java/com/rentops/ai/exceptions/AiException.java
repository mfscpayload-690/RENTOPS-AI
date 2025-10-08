package com.rentops.ai.exceptions;

/**
 * Base checked exception for AI integration failures.
 */
public class AiException extends Exception {

    public AiException(String message) {
        super(message);
    }

    public AiException(String message, Throwable cause) {
        super(message, cause);
    }
}
