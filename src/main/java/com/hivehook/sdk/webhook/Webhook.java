package com.hivehook.sdk.webhook;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class Webhook {
    public static final String HEADER_SIGNATURE = "X-Hivehook-Signature";
    public static final String HEADER_TIMESTAMP = "X-Hivehook-Timestamp";
    public static final String HEADER_MESSAGE_ID = "X-Hivehook-Message-ID";

    /**
     * Sentinel value for the {@code toleranceSeconds} parameter that disables timestamp
     * freshness checking entirely. {@code 0} retains its strict meaning (the timestamp must
     * exactly match {@code now}), and any positive value enforces an N-second window.
     */
    public static final int NO_TOLERANCE = -1;

    private static final int DEFAULT_TOLERANCE = 300;

    private Webhook() {}

    public static String generateSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return "whsec_" + bytesToHex(bytes);
    }

    public static SignResult sign(String payload, String secret, long timestamp) {
        byte[] secretBytes = decodeSecret(secret);
        String signingInput = timestamp + "." + payload;
        byte[] mac = hmacSha256(secretBytes, signingInput.getBytes(StandardCharsets.UTF_8));
        String signature = "v1=" + bytesToHex(mac);
        return new SignResult(signature, timestamp);
    }

    public static SignResult sign(String payload, String secret) {
        return sign(payload, secret, System.currentTimeMillis() / 1000);
    }

    /**
     * Verify a webhook signature.
     *
     * <p>Tolerance semantics:
     * <ul>
     *     <li>{@link #NO_TOLERANCE} ({@code -1}): skip freshness checking entirely.</li>
     *     <li>{@code 0}: strict; the supplied {@code timestamp} must equal {@code now}.</li>
     *     <li>Positive {@code N}: accept timestamps within an N-second window of {@code now}.</li>
     * </ul>
     *
     * <p>Supports multi-scheme signature headers (e.g. {@code "v1=...,v2=..."} ) by splitting on
     * {@code ,} and matching the {@code v1=...} segment. Returns {@code false} when the
     * signature argument is {@code null}.
     */
    public static boolean verify(String payload, String secret, String signature, long timestamp, int toleranceSeconds) {
        if (signature == null) {
            return false;
        }
        if (toleranceSeconds != NO_TOLERANCE) {
            long now = System.currentTimeMillis() / 1000;
            if (Math.abs(now - timestamp) > toleranceSeconds) {
                return false;
            }
        }
        String v1 = extractV1Segment(signature);
        if (v1 == null) {
            return false;
        }
        SignResult expected = sign(payload, secret, timestamp);
        return MessageDigest.isEqual(
                expected.getSignature().getBytes(StandardCharsets.UTF_8),
                v1.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static boolean verify(String payload, String secret, String signature, long timestamp) {
        return verify(payload, secret, signature, timestamp, DEFAULT_TOLERANCE);
    }

    public static boolean verifyWithRotation(String payload, String primarySecret, String secondarySecret,
                                              String signature, long timestamp, int toleranceSeconds) {
        if (verify(payload, primarySecret, signature, timestamp, toleranceSeconds)) {
            return true;
        }
        if (secondarySecret != null && !secondarySecret.isEmpty()) {
            return verify(payload, secondarySecret, signature, timestamp, toleranceSeconds);
        }
        return false;
    }

    public static boolean verifyWithRotation(String payload, String primarySecret, String secondarySecret,
                                              String signature, long timestamp) {
        return verifyWithRotation(payload, primarySecret, secondarySecret, signature, timestamp, DEFAULT_TOLERANCE);
    }

    /**
     * Extract the {@code v1=...} segment from a (possibly multi-scheme) signature header.
     * Returns the entire {@code "v1=<hex>"} token, or {@code null} if no such segment is present.
     */
    private static String extractV1Segment(String signature) {
        for (String raw : signature.split(",")) {
            String segment = raw.trim();
            if (segment.startsWith("v1=")) {
                return segment;
            }
        }
        return null;
    }

    private static byte[] decodeSecret(String secret) {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] hmacSha256(byte[] key, byte[] data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256 failed", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    public static class SignResult {
        private final String signature;
        private final long timestamp;

        public SignResult(String signature, long timestamp) {
            this.signature = signature;
            this.timestamp = timestamp;
        }

        public String getSignature() { return signature; }
        public long getTimestamp() { return timestamp; }
    }
}
