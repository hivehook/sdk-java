package com.hivehook.sdk;

import java.util.Collections;
import java.util.Map;

/**
 * Thrown when the Hivehook API returns an error. Carries the HTTP status code (when available)
 * and the GraphQL {@code extensions} map for the originating error, and serves as the common
 * base for the more specific {@link AuthException}, {@link NotFoundException},
 * {@link ConflictException}, {@link ValidationException}, {@link RateLimitException}, and
 * {@link ServerException}.
 */
public class ApiException extends HivehookException {
    private final Integer statusCode;
    private final Map<String, Object> extensions;

    /**
     * @param message    error message.
     * @param statusCode HTTP status code, or {@code null} for application-level errors.
     */
    public ApiException(String message, Integer statusCode) {
        this(message, statusCode, (Map<String, Object>) null, null);
    }

    /**
     * @param message    error message.
     * @param statusCode HTTP status code, or {@code null} for application-level errors.
     * @param cause      underlying cause.
     */
    public ApiException(String message, Integer statusCode, Throwable cause) {
        this(message, statusCode, null, cause);
    }

    /**
     * @param message    error message.
     * @param statusCode HTTP status code, or {@code null} for application-level errors.
     * @param extensions GraphQL {@code extensions} payload for the originating error, or
     *                   {@code null} when none was returned.
     */
    public ApiException(String message, Integer statusCode, Map<String, Object> extensions) {
        this(message, statusCode, extensions, null);
    }

    /**
     * @param message    error message.
     * @param statusCode HTTP status code, or {@code null} for application-level errors.
     * @param extensions GraphQL {@code extensions} payload for the originating error, or
     *                   {@code null} when none was returned.
     * @param cause      underlying cause.
     */
    public ApiException(String message, Integer statusCode, Map<String, Object> extensions, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.extensions = extensions == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(extensions);
    }

    /**
     * @return the HTTP status code, or {@code null} when the error originated from the GraphQL layer.
     */
    public Integer statusCode() {
        return statusCode;
    }

    /**
     * @return the HTTP status code, or {@code null} when the error originated from the GraphQL layer.
     * @deprecated use {@link #statusCode()} instead.
     */
    @Deprecated
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * @return the GraphQL {@code extensions} payload attached to the originating error; never
     *         {@code null}. The returned map is unmodifiable.
     */
    public Map<String, Object> extensions() {
        return extensions;
    }
}
