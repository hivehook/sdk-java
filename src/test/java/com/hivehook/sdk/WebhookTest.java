package com.hivehook.sdk;

import com.hivehook.sdk.webhook.Webhook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebhookTest {

    @Test
    void signAndVerify() {
        String secret = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("{\"event\":\"test\"}", secret);
        assertTrue(Webhook.verify("{\"event\":\"test\"}", secret, result.getSignature(), result.getTimestamp()));
    }

    @Test
    void verifyWrongSecret() {
        String secret = Webhook.generateSecret();
        String other = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("test", secret);
        assertFalse(Webhook.verify("test", other, result.getSignature(), result.getTimestamp()));
    }

    @Test
    void verifyWrongPayload() {
        String secret = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("original", secret);
        assertFalse(Webhook.verify("tampered", secret, result.getSignature(), result.getTimestamp()));
    }

    @Test
    void verifyExpiredTimestamp() {
        String secret = Webhook.generateSecret();
        long oldTs = System.currentTimeMillis() / 1000 - 600;
        Webhook.SignResult result = Webhook.sign("test", secret, oldTs);
        assertFalse(Webhook.verify("test", secret, result.getSignature(), oldTs, 300));
    }

    @Test
    void verifyNoToleranceSentinelSkipsFreshnessCheck() {
        String secret = Webhook.generateSecret();
        long oldTs = System.currentTimeMillis() / 1000 - 99999;
        Webhook.SignResult result = Webhook.sign("test", secret, oldTs);
        assertTrue(Webhook.verify("test", secret, result.getSignature(), oldTs, Webhook.NO_TOLERANCE));
    }

    @Test
    void verifyZeroToleranceIsStrict() {
        String secret = Webhook.generateSecret();
        long oldTs = System.currentTimeMillis() / 1000 - 30;
        Webhook.SignResult result = Webhook.sign("test", secret, oldTs);
        assertFalse(Webhook.verify("test", secret, result.getSignature(), oldTs, 0));
    }

    @Test
    void verifyZeroToleranceAcceptsCurrentTimestamp() {
        String secret = Webhook.generateSecret();
        long now = System.currentTimeMillis() / 1000;
        Webhook.SignResult result = Webhook.sign("test", secret, now);
        assertTrue(Webhook.verify("test", secret, result.getSignature(), now, 0));
    }

    @Test
    void verifyNullSignatureReturnsFalse() {
        String secret = Webhook.generateSecret();
        long ts = System.currentTimeMillis() / 1000;
        assertFalse(Webhook.verify("test", secret, null, ts));
    }

    @Test
    void verifyMultiSchemeSignatureExtractsV1() {
        String secret = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("payload", secret);
        String multi = result.getSignature() + ",v2=deadbeef";
        assertTrue(Webhook.verify("payload", secret, multi, result.getTimestamp()));
    }

    @Test
    void verifyMultiSchemeSignatureWithLeadingScheme() {
        String secret = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("payload", secret);
        String multi = "v0=oldscheme, " + result.getSignature();
        assertTrue(Webhook.verify("payload", secret, multi, result.getTimestamp()));
    }

    @Test
    void verifyMultiSchemeMissingV1ReturnsFalse() {
        String secret = Webhook.generateSecret();
        long ts = System.currentTimeMillis() / 1000;
        assertFalse(Webhook.verify("payload", secret, "v0=foo,v2=bar", ts));
    }

    @Test
    void verifyWithRotationPrimary() {
        String primary = Webhook.generateSecret();
        String secondary = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("test", primary);
        assertTrue(Webhook.verifyWithRotation("test", primary, secondary, result.getSignature(), result.getTimestamp()));
    }

    @Test
    void verifyWithRotationSecondary() {
        String primary = Webhook.generateSecret();
        String secondary = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("test", secondary);
        assertTrue(Webhook.verifyWithRotation("test", primary, secondary, result.getSignature(), result.getTimestamp()));
    }

    @Test
    void verifyWithRotationNeither() {
        String primary = Webhook.generateSecret();
        String secondary = Webhook.generateSecret();
        String unrelated = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("test", unrelated);
        assertFalse(Webhook.verifyWithRotation("test", primary, secondary, result.getSignature(), result.getTimestamp()));
    }

    @Test
    void verifyWithRotationEmptySecondary() {
        String primary = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("test", primary);
        assertTrue(Webhook.verifyWithRotation("test", primary, "", result.getSignature(), result.getTimestamp()));
    }

    @Test
    void generateSecretFormat() {
        String secret = Webhook.generateSecret();
        assertTrue(secret.startsWith("whsec_"));
        assertEquals(6 + 64, secret.length());
    }

    @Test
    void signFormat() {
        String secret = Webhook.generateSecret();
        Webhook.SignResult result = Webhook.sign("test", secret);
        assertTrue(result.getSignature().startsWith("v1="));
        assertEquals(3 + 64, result.getSignature().length());
    }
}
