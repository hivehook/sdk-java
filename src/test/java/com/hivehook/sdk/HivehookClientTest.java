package com.hivehook.sdk;

import com.sun.net.httpserver.HttpServer;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.Source;
import com.hivehook.sdk.types.SystemStatus;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class HivehookClientTest {
    private static HttpServer server;
    private static String baseUrl;
    private static HivehookClient client;

    @BeforeAll
    static void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/graphql", exchange -> {
            byte[] reqBody = exchange.getRequestBody().readAllBytes();
            String body = new String(reqBody, StandardCharsets.UTF_8);

            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            String response;

            if ("Bearer bad_key".equals(auth)) {
                response = "{\"errors\":[{\"message\":\"unauthorized\",\"extensions\":{\"code\":\"UNAUTHORIZED\"}}]}";
            } else if (body.contains("status") && body.contains("dlqSize")) {
                response = "{\"data\":{\"status\":{\"status\":\"healthy\",\"dlqSize\":0,\"outboundDlqSize\":0,\"queueDepth\":5,\"activeWorkers\":4,\"totalWorkers\":4,\"uptime\":3600,\"version\":\"v0.1.0-beta\",\"sourcesTotal\":3,\"destinationsTotal\":2,\"subscriptionsTotal\":5,\"eventsTotal\":1000,\"eventsFailed\":10,\"deliveriesTotal\":900,\"deliveriesPending\":50,\"deliveriesDelivered\":840,\"messagesTotal\":100,\"outboundDeliveriesTotal\":80,\"outboundDeliveriesPending\":5,\"outboundDeliveriesFailed\":2}}}";
            } else if (body.contains("sources") && !body.contains("mutation") && !body.contains("$id")) {
                response = "{\"data\":{\"sources\":{\"nodes\":[{\"id\":\"src-1\",\"name\":\"GitHub\",\"slug\":\"github\",\"providerType\":\"github\",\"status\":\"ACTIVE\",\"rateLimitRps\":100,\"createdAt\":\"2025-01-01T00:00:00Z\"}],\"pageInfo\":{\"total\":1,\"limit\":20,\"offset\":0,\"endCursor\":null,\"hasNextPage\":false}}}}";
            } else if (body.contains("source") && body.contains("$id") && !body.contains("mutation")) {
                if (body.contains("not-found")) {
                    response = "{\"errors\":[{\"message\":\"source not found\",\"extensions\":{\"code\":\"NOT_FOUND\"}}]}";
                } else {
                    response = "{\"data\":{\"source\":{\"id\":\"src-1\",\"name\":\"GitHub\",\"slug\":\"github\",\"providerType\":\"github\",\"status\":\"ACTIVE\",\"rateLimitRps\":100,\"createdAt\":\"2025-01-01T00:00:00Z\"}}}";
                }
            } else if (body.contains("createSource")) {
                response = "{\"data\":{\"createSource\":{\"id\":\"src-new\",\"name\":\"Stripe\",\"slug\":\"stripe\",\"providerType\":\"stripe\",\"status\":\"ACTIVE\",\"rateLimitRps\":0,\"createdAt\":\"2025-01-01T00:00:00Z\"}}}";
            } else if (body.contains("deleteSource")) {
                response = "{\"data\":{\"deleteSource\":true}}";
            } else {
                response = "{\"data\":{}}";
            }

            byte[] respBytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, respBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(respBytes);
            }
        });
        server.start();
        int port = server.getAddress().getPort();
        baseUrl = "http://127.0.0.1:" + port;
        client = HivehookClient.builder().apiKey("test_key").baseUrl(baseUrl).build();
    }

    @AfterAll
    static void tearDown() {
        server.stop(0);
    }

    @Test
    void listSources() {
        ListResult<Source> result = client.sources().list(null, null, null, null, null, null, null);
        assertEquals(1, result.nodes().size());
        assertEquals("github", result.nodes().get(0).slug());
        assertEquals(1, result.pageInfo().total());
    }

    @Test
    void getSource() {
        Source source = client.sources().get("src-1");
        assertNotNull(source);
        assertEquals("src-1", source.id());
        assertEquals("GitHub", source.name());
    }

    @Test
    void getSourceNotFound() {
        assertThrows(NotFoundException.class, () -> client.sources().get("not-found"));
    }

    @Test
    void createSource() {
        Source source = client.sources().create("Stripe", "stripe", "stripe", null);
        assertEquals("src-new", source.id());
        assertEquals("Stripe", source.name());
    }

    @Test
    void deleteSource() {
        boolean result = client.sources().delete("src-1");
        assertTrue(result);
    }

    @Test
    void getStatus() {
        SystemStatus status = client.status().get();
        assertNotNull(status);
        assertEquals("healthy", status.status());
        assertEquals("v0.1.0-beta", status.version());
        assertEquals(1000, status.eventsTotal());
        assertEquals(4, status.activeWorkers());
    }

    @Test
    void authError() {
        HivehookClient badClient = HivehookClient.builder().apiKey("bad_key").baseUrl(baseUrl).build();
        assertThrows(AuthException.class, () -> badClient.sources().list(null, null, null, null, null, null, null));
    }

    @Test
    void builderDefaults() {
        HivehookClient c = HivehookClient.builder().apiKey("key").build();
        assertNotNull(c.sources());
        assertNotNull(c.status());
    }

    @Test
    void listSourcesWithFilters() {
        ListResult<Source> result = client.sources().list("ACTIVE", null, null, 10, null, null, null);
        assertEquals(1, result.nodes().size());
    }

    @Test
    void pageInfo() {
        ListResult<Source> result = client.sources().list(null, null, null, null, null, null, null);
        assertFalse(result.pageInfo().hasNextPage());
        assertEquals(20, result.pageInfo().limit());
    }

    @Test
    void getStatusAsync() throws Exception {
        CompletableFuture<SystemStatus> future = client.status().getAsync();
        SystemStatus status = future.get(5, TimeUnit.SECONDS);
        assertNotNull(status);
        assertEquals("healthy", status.status());
        assertEquals("v0.1.0-beta", status.version());
        assertEquals(1000, status.eventsTotal());
    }

    @Test
    void listSourcesAsync() throws Exception {
        CompletableFuture<ListResult<Source>> future = client.sources().listAsync(null, null, null, null, null, null, null);
        ListResult<Source> result = future.get(5, TimeUnit.SECONDS);
        assertEquals(1, result.nodes().size());
        assertEquals("github", result.nodes().get(0).slug());
    }

    @Test
    void deleteSourceAsync() throws Exception {
        CompletableFuture<Boolean> future = client.sources().deleteAsync("src-1");
        Boolean result = future.get(5, TimeUnit.SECONDS);
        assertTrue(result);
    }
}
