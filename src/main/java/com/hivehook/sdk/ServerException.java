package com.hivehook.sdk;

import java.util.Map;

/**
 * Thrown when the Hivehook gateway responds with an HTTP 5xx status code, indicating a
 * server-side failure that may be retriable.
 */
public final class ServerException extends ApiException {
    /**
     * @param message    error message.
     * @param statusCode HTTP status code (typically 500-599).
     */
    public ServerException(String message, int statusCode) {
        super(message, statusCode);
    }

    /**
     * @param message    error message.
     * @param statusCode HTTP status code (typically 500-599).
     * @param extensions GraphQL {@code extensions} payload, or {@code null}.
     */
    public ServerException(String message, int statusCode, Map<String, Object> extensions) {
        super(message, statusCode, extensions);
    }
}
