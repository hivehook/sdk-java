package com.hivehook.sdk;

import com.sun.net.httpserver.HttpServer;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.TransformTestResult;
import com.hivehook.sdk.types.Transformation;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransformationServiceTest {
    private static HttpServer server;
    private static HivehookClient client;

    @BeforeAll
    static void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/graphql", exchange -> {
            byte[] reqBody = exchange.getRequestBody().readAllBytes();
            String body = new String(reqBody, StandardCharsets.UTF_8);

            String response;

            if (body.contains("transformations") && !body.contains("mutation") && !body.contains("$id")) {
                response = "{\"data\":{\"transformations\":{\"nodes\":[{\"id\":\"tf-1\",\"name\":\"Add metadata\",\"description\":\"Adds request metadata\",\"code\":\"function transform(evt) { return evt; }\",\"enabled\":true,\"failOpen\":false,\"timeoutMs\":1000,\"createdAt\":\"2025-01-01T00:00:00Z\",\"updatedAt\":\"2025-01-01T00:00:00Z\"}],\"pageInfo\":{\"total\":1,\"limit\":20,\"offset\":0,\"endCursor\":null,\"hasNextPage\":false}}}}";
            } else if (body.contains("transformation") && body.contains("$id") && !body.contains("mutation")) {
                if (body.contains("not-found")) {
                    response = "{\"errors\":[{\"message\":\"transformation not found\",\"extensions\":{\"code\":\"NOT_FOUND\"}}]}";
                } else {
                    response = "{\"data\":{\"transformation\":{\"id\":\"tf-1\",\"name\":\"Add metadata\",\"description\":\"Adds request metadata\",\"code\":\"function transform(evt) { return evt; }\",\"enabled\":true,\"failOpen\":false,\"timeoutMs\":1000,\"createdAt\":\"2025-01-01T00:00:00Z\",\"updatedAt\":\"2025-01-01T00:00:00Z\"}}}";
                }
            } else if (body.contains("createTransformation")) {
                response = "{\"data\":{\"createTransformation\":{\"id\":\"tf-new\",\"name\":\"Strip headers\",\"description\":\"Removes sensitive headers\",\"code\":\"function transform(evt) { delete evt.headers; return evt; }\",\"enabled\":true,\"failOpen\":true,\"timeoutMs\":2000,\"createdAt\":\"2025-01-01T00:00:00Z\",\"updatedAt\":\"2025-01-01T00:00:00Z\"}}}";
            } else if (body.contains("updateTransformation")) {
                response = "{\"data\":{\"updateTransformation\":{\"id\":\"tf-1\",\"name\":\"Updated transform\",\"description\":\"Updated description\",\"code\":\"function transform(evt) { return evt; }\",\"enabled\":false,\"failOpen\":true,\"timeoutMs\":3000,\"createdAt\":\"2025-01-01T00:00:00Z\",\"updatedAt\":\"2025-01-02T00:00:00Z\"}}}";
            } else if (body.contains("deleteTransformation")) {
                response = "{\"data\":{\"deleteTransformation\":true}}";
            } else if (body.contains("testTransformation")) {
                response = "{\"data\":{\"testTransformation\":{\"success\":true,\"output\":{\"body\":{\"event\":\"test\"},\"eventType\":\"test.event\"},\"error\":\"\",\"durationMs\":5}}}";
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
        String baseUrl = "http://127.0.0.1:" + port;
        client = HivehookClient.builder().apiKey("test_key").baseUrl(baseUrl).build();
    }

    @AfterAll
    static void tearDown() {
        server.stop(0);
    }

    @Test
    void listTransformations() {
        ListResult<Transformation> result = client.transformations().list(null, null, null, null);
        assertEquals(1, result.nodes().size());
        assertEquals("Add metadata", result.nodes().get(0).name());
        assertEquals(1, result.pageInfo().total());
    }

    @Test
    void listTransformationsWithFilter() {
        ListResult<Transformation> result = client.transformations().list(true, null, null, null);
        assertEquals(1, result.nodes().size());
    }

    @Test
    void getTransformation() {
        Transformation t = client.transformations().get("tf-1");
        assertNotNull(t);
        assertEquals("tf-1", t.id());
        assertEquals("Add metadata", t.name());
        assertEquals("Adds request metadata", t.description());
        assertEquals("function transform(evt) { return evt; }", t.code());
        assertTrue(t.enabled());
        assertFalse(t.failOpen());
        assertEquals(1000, t.timeoutMs());
    }

    @Test
    void getTransformationNotFound() {
        assertThrows(NotFoundException.class, () -> client.transformations().get("not-found"));
    }

    @Test
    void createTransformation() {
        Transformation t = client.transformations().create("Strip headers", "Removes sensitive headers",
                "function transform(evt) { delete evt.headers; return evt; }", true, 2000);
        assertEquals("tf-new", t.id());
        assertEquals("Strip headers", t.name());
        assertTrue(t.failOpen());
        assertEquals(2000, t.timeoutMs());
    }

    @Test
    void updateTransformation() {
        Transformation t = client.transformations().update("tf-1", "Updated transform", "Updated description",
                null, false, true, 3000);
        assertEquals("tf-1", t.id());
        assertEquals("Updated transform", t.name());
        assertFalse(t.enabled());
        assertTrue(t.failOpen());
        assertEquals(3000, t.timeoutMs());
    }

    @Test
    void deleteTransformation() {
        boolean result = client.transformations().delete("tf-1");
        assertTrue(result);
    }

    @Test
    void testTransformation() {
        Map<String, Object> payload = Map.of("event", "test");
        TransformTestResult result = client.transformations().test(
                "function transform(evt) { return evt; }", payload, "test.event", null);
        assertTrue(result.success());
        assertNotNull(result.output());
        assertEquals("", result.error());
        assertEquals(5, result.durationMs());
    }

    @Test
    void transformationPageInfo() {
        ListResult<Transformation> result = client.transformations().list(null, null, null, null);
        assertFalse(result.pageInfo().hasNextPage());
        assertEquals(20, result.pageInfo().limit());
    }

    @Test
    void clientAccessor() {
        assertNotNull(client.transformations());
    }
}
