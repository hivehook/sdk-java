package com.hivehook.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class GraphQLTransportTest {
    private HttpServer server;
    private String baseUrl;

    @BeforeEach
    void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach
    void stop() {
        server.stop(0);
    }

    private void on(String path, HttpHandler handler) {
        server.createContext(path, handler);
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange, int status, String body,
                                Map<String, String> headers) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        if (headers != null) {
            headers.forEach((k, v) -> exchange.getResponseHeaders().add(k, v));
        }
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    @Test
    void rateLimitThrowsRateLimitException() {
        on("/graphql", ex -> respond(ex, 429,
                "{\"errors\":[{\"message\":\"slow down\",\"extensions\":{\"code\":\"RATE_LIMITED\"}}]}",
                Map.of("Retry-After", "1")));
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 0);
        RateLimitException ex = assertThrows(RateLimitException.class,
                () -> transport.execute("query { ping }", null));
        assertEquals(429, ex.statusCode());
        assertTrue(ex.retryAfterMillis().isPresent());
        assertEquals(1000L, ex.retryAfterMillis().get());
        assertEquals("RATE_LIMITED", ex.extensions().get("code"));
    }

    @Test
    void serverErrorThrowsServerException() {
        on("/graphql", ex -> respond(ex, 503, "{\"errors\":[{\"message\":\"down\"}]}", null));
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 0);
        ServerException ex = assertThrows(ServerException.class,
                () -> transport.execute("query { ping }", null));
        assertEquals(503, ex.statusCode());
        assertEquals("down", ex.getMessage());
    }

    @Test
    void retriesOn429ThenSucceeds() {
        AtomicInteger calls = new AtomicInteger();
        on("/graphql", ex -> {
            int n = calls.incrementAndGet();
            if (n == 1) {
                respond(ex, 429, "{\"errors\":[{\"message\":\"slow\"}]}", Map.of("Retry-After", "0"));
            } else {
                respond(ex, 200, "{\"data\":{\"ok\":true}}", null);
            }
        });
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 2);
        JsonNode data = transport.execute("query { ok }", null);
        assertTrue(data.path("ok").asBoolean());
        assertEquals(2, calls.get());
    }

    @Test
    void retriesOn5xxThenSucceeds() {
        AtomicInteger calls = new AtomicInteger();
        on("/graphql", ex -> {
            int n = calls.incrementAndGet();
            if (n < 3) {
                respond(ex, 502, "{\"errors\":[{\"message\":\"bad gateway\"}]}", null);
            } else {
                respond(ex, 200, "{\"data\":{\"ok\":true}}", null);
            }
        });
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 2);
        JsonNode data = transport.execute("query { ok }", null);
        assertTrue(data.path("ok").asBoolean());
        assertEquals(3, calls.get());
    }

    @Test
    void retriesExhaustedThrows() {
        AtomicInteger calls = new AtomicInteger();
        on("/graphql", ex -> {
            calls.incrementAndGet();
            respond(ex, 500, "{\"errors\":[{\"message\":\"boom\"}]}", null);
        });
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 1);
        assertThrows(ServerException.class, () -> transport.execute("query { ok }", null));
        assertEquals(2, calls.get()); // 1 initial + 1 retry
    }

    @Test
    void doesNotRetryAuthErrors() {
        AtomicInteger calls = new AtomicInteger();
        on("/graphql", ex -> {
            calls.incrementAndGet();
            respond(ex, 401, "{\"errors\":[{\"message\":\"nope\"}]}", null);
        });
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 3);
        assertThrows(AuthException.class, () -> transport.execute("query { ok }", null));
        assertEquals(1, calls.get());
    }

    @Test
    void doesNotRetryNotFound() {
        AtomicInteger calls = new AtomicInteger();
        on("/graphql", ex -> {
            calls.incrementAndGet();
            respond(ex, 200,
                    "{\"errors\":[{\"message\":\"not found\",\"extensions\":{\"code\":\"NOT_FOUND\"}}]}",
                    null);
        });
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 3);
        assertThrows(NotFoundException.class, () -> transport.execute("query { ok }", null));
        assertEquals(1, calls.get());
    }

    @Test
    void extensionsPreservedOnGraphQLError() {
        on("/graphql", ex -> respond(ex, 200,
                "{\"errors\":[{\"message\":\"conflict\",\"extensions\":{\"code\":\"CONFLICT\",\"field\":\"slug\"}}]}",
                null));
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 0);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> transport.execute("mutation {}", null));
        assertEquals("CONFLICT", ex.extensions().get("code"));
        assertEquals("slug", ex.extensions().get("field"));
    }

    @Test
    void asyncRetriesOn429ThenSucceeds() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        on("/graphql", ex -> {
            int n = calls.incrementAndGet();
            if (n == 1) {
                respond(ex, 429, "{\"errors\":[{\"message\":\"slow\"}]}", Map.of("Retry-After", "0"));
            } else {
                respond(ex, 200, "{\"data\":{\"ok\":true}}", null);
            }
        });
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 2);
        CompletableFuture<JsonNode> f = transport.executeAsync("query { ok }", null);
        JsonNode data = f.get(5, TimeUnit.SECONDS);
        assertTrue(data.path("ok").asBoolean());
        assertEquals(2, calls.get());
    }

    @Test
    void asyncExhaustedRetriesCompletesExceptionally() {
        AtomicInteger calls = new AtomicInteger();
        on("/graphql", ex -> {
            calls.incrementAndGet();
            respond(ex, 500, "{\"errors\":[{\"message\":\"boom\"}]}", null);
        });
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 1);
        CompletableFuture<JsonNode> f = transport.executeAsync("query { ok }", null);
        ExecutionException ee = assertThrows(ExecutionException.class, () -> f.get(5, TimeUnit.SECONDS));
        assertTrue(ee.getCause() instanceof ServerException, "cause was " + ee.getCause());
        assertEquals(2, calls.get());
    }

    @Test
    void asyncDoesNotRetryValidationError() {
        AtomicInteger calls = new AtomicInteger();
        on("/graphql", ex -> {
            calls.incrementAndGet();
            respond(ex, 200,
                    "{\"errors\":[{\"message\":\"bad\",\"extensions\":{\"code\":\"VALIDATION\"}}]}",
                    null);
        });
        GraphQLTransport transport = new GraphQLTransport(baseUrl, "k", null,
                Duration.ofSeconds(5), Duration.ofSeconds(5), 3);
        CompletableFuture<JsonNode> f = transport.executeAsync("query { ok }", null);
        ExecutionException ee = assertThrows(ExecutionException.class, () -> f.get(5, TimeUnit.SECONDS));
        assertTrue(ee.getCause() instanceof ValidationException);
        assertEquals(1, calls.get());
    }
}
