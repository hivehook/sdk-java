package com.hivehook.sdk;

import java.util.Map;

/**
 * Thrown when a request conflicts with the current state of the server (e.g. duplicate slug).
 */
public final class ConflictException extends ApiException {
    /**
     * @param message error message.
     */
    public ConflictException(String message) {
        super(message, 409);
    }

    /**
     * @param message    error message.
     * @param statusCode HTTP status code.
     */
    public ConflictException(String message, int statusCode) {
        super(message, statusCode);
    }

    /**
     * @param message    error message.
     * @param extensions GraphQL {@code extensions} payload, or {@code null}.
     */
    public ConflictException(String message, Map<String, Object> extensions) {
        super(message, 409, extensions);
    }
}
