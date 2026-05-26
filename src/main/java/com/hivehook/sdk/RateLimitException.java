package com.hivehook.sdk;

import java.util.Map;
import java.util.Optional;

/**
 * Thrown when the Hivehook gateway responds with HTTP 429 (Too Many Requests). Surfaces the
 * server-provided {@code Retry-After} hint (in milliseconds) when present.
 */
public final class RateLimitException extends ApiException {
    private final Long retryAfterMillis;

    /**
     * @param message          error message.
     * @param retryAfterMillis the parsed {@code Retry-After} hint in milliseconds, or
     *                         {@code null} when the server did not provide one.
     */
    public RateLimitException(String message, Long retryAfterMillis) {
        super(message, 429);
        this.retryAfterMillis = retryAfterMillis;
    }

    /**
     * @param message          error message.
     * @param retryAfterMillis the parsed {@code Retry-After} hint in milliseconds, or
     *                         {@code null} when the server did not provide one.
     * @param extensions       GraphQL {@code extensions} payload, or {@code null}.
     */
    public RateLimitException(String message, Long retryAfterMillis, Map<String, Object> extensions) {
        super(message, 429, extensions);
        this.retryAfterMillis = retryAfterMillis;
    }

    /**
     * @return the {@code Retry-After} hint in milliseconds, if the server provided one.
     */
    public Optional<Long> retryAfterMillis() {
        return Optional.ofNullable(retryAfterMillis);
    }
}
