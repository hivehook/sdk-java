package com.hivehook.sdk;

import com.hivehook.sdk.types.Source;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.sun.net.httpserver.HttpServer;

import static org.junit.jupiter.api.Assertions.*;

class PaginationTest {
    private static HttpServer server;
    private static HivehookClient client;

    @BeforeAll
    static void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/graphql", exchange -> {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String response;
            if (body.contains("\"after\":\"c1\"")) {
                response = "{\"data\":{\"sources\":{\"nodes\":[{\"id\":\"c\"}],\"pageInfo\":{\"hasNextPage\":false}}}}";
            } else {
                response = "{\"data\":{\"sources\":{\"nodes\":[{\"id\":\"a\"},{\"id\":\"b\"}],\"pageInfo\":{\"endCursor\":\"c1\",\"hasNextPage\":true}}}}";
            }
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        client = HivehookClient.builder().apiKey("test_key").baseUrl(baseUrl).build();
    }

    @AfterAll
    static void tearDown() {
        server.stop(0);
    }

    @Test
    void listAllWalksEveryPage() {
        List<Source> sources = client.sources().listAll(null, null, null);
        assertEquals(3, sources.size());
        assertEquals("a", sources.get(0).id());
        assertEquals("c", sources.get(2).id());
    }
}
