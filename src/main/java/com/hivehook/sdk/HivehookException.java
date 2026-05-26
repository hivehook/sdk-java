package com.hivehook.sdk;

/**
 * Base class for all unchecked exceptions thrown by the Hivehook SDK.
 */
public class HivehookException extends RuntimeException {
    /**
     * @param message error message.
     */
    public HivehookException(String message) {
        super(message);
    }

    /**
     * @param message error message.
     * @param cause   underlying cause.
     */
    public HivehookException(String message, Throwable cause) {
        super(message, cause);
    }
}
