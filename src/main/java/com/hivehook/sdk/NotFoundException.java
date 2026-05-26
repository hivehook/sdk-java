package com.hivehook.sdk;

import java.util.Map;

/**
 * Thrown when the requested resource was not found.
 */
public final class NotFoundException extends ApiException {
    /**
     * @param message error message.
     */
    public NotFoundException(String message) {
        super(message, 404);
    }

    /**
     * @param message    error message.
     * @param statusCode HTTP status code.
     */
    public NotFoundException(String message, int statusCode) {
        super(message, statusCode);
    }

    /**
     * @param message    error message.
     * @param extensions GraphQL {@code extensions} payload, or {@code null}.
     */
    public NotFoundException(String message, Map<String, Object> extensions) {
        super(message, 404, extensions);
    }
}
