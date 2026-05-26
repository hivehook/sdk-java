package com.hivehook.sdk;

import java.util.Map;

/**
 * Thrown for authentication and authorisation failures (HTTP 401/403 or GraphQL {@code UNAUTHORIZED}).
 */
public final class AuthException extends ApiException {
    /**
     * @param message    error message.
     * @param statusCode HTTP status code (typically 401 or 403).
     */
    public AuthException(String message, int statusCode) {
        super(message, statusCode);
    }

    /**
     * @param message error message; the status code defaults to 401.
     */
    public AuthException(String message) {
        super(message, 401);
    }

    /**
     * @param message    error message.
     * @param statusCode HTTP status code.
     * @param extensions GraphQL {@code extensions} payload, or {@code null}.
     */
    public AuthException(String message, int statusCode, Map<String, Object> extensions) {
        super(message, statusCode, extensions);
    }

    /**
     * @param message    error message.
     * @param extensions GraphQL {@code extensions} payload, or {@code null}.
     */
    public AuthException(String message, Map<String, Object> extensions) {
        super(message, 401, extensions);
    }
}
