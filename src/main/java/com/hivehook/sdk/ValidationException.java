package com.hivehook.sdk;

import java.util.Map;

/**
 * Thrown when the API rejects an input payload as invalid.
 */
public final class ValidationException extends ApiException {
    /**
     * @param message error message.
     */
    public ValidationException(String message) {
        super(message, 400);
    }

    /**
     * @param message    error message.
     * @param statusCode HTTP status code.
     */
    public ValidationException(String message, int statusCode) {
        super(message, statusCode);
    }

    /**
     * @param message    error message.
     * @param extensions GraphQL {@code extensions} payload, or {@code null}.
     */
    public ValidationException(String message, Map<String, Object> extensions) {
        super(message, 400, extensions);
    }
}
