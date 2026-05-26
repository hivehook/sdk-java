package com.hivehook.sdk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Low-level transport that issues GraphQL POST requests over HTTP and surfaces typed errors.
 *
 * <p>The transport is built around <a href="https://github.com/FasterXML/jackson">Jackson</a>: it
 * serialises request payloads, parses responses into a {@link JsonNode} tree, and exposes the
 * shared {@link ObjectMapper} so callers can hydrate strongly-typed records.
 *
 * <p>It applies bounded exponential-backoff retries to transient failures ({@link IOException},
 * {@link RateLimitException}, {@link ServerException}) and honours {@code Retry-After} hints
 * when the server provides them.
 */
public final class GraphQLTransport {
    /** SDK version advertised in the User-Agent header. */
    public static final String VERSION = "0.1.0";

    private static final String USER_AGENT = "hivehook-java/" + VERSION;
    private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration DEFAULT_REQUEST_TIMEOUT = Duration.ofSeconds(30);
    private static final int DEFAULT_MAX_RETRIES = 2;
    private static final long BASE_BACKOFF_MILLIS = 200L;

    /** Lazily-initialised scheduler used to delay asynchronous retries. */
    private static final AtomicReference<ScheduledExecutorService> RETRY_SCHEDULER = new AtomicReference<>();

    private final String baseUrl;
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final Duration requestTimeout;
    private final int maxRetries;

    /**
     * Construct a transport with default timeouts (10s connect, 30s request) and default retry
     * policy (2 retries). Prefer {@link HivehookClient.Builder} for production use.
     *
     * @param baseUrl    base URL of the Hivehook gateway (with or without trailing slash).
     * @param apiKey     bearer token to send in {@code Authorization}; may be {@code null} or empty
     *                   for unauthenticated calls.
     * @param httpClient HTTP client to use; {@code null} for a default {@link HttpClient} with a
     *                   10s connect timeout.
     */
    public GraphQLTransport(String baseUrl, String apiKey, HttpClient httpClient) {
        this(baseUrl, apiKey, httpClient, DEFAULT_CONNECT_TIMEOUT, DEFAULT_REQUEST_TIMEOUT, DEFAULT_MAX_RETRIES);
    }

    /**
     * Construct a transport with explicit timeout and retry settings.
     *
     * @param baseUrl        base URL of the Hivehook gateway (with or without trailing slash).
     * @param apiKey         bearer token to send in {@code Authorization}; may be {@code null} or
     *                       empty for unauthenticated calls.
     * @param httpClient     HTTP client to use; {@code null} to build a default one applying
     *                       {@code connectTimeout}.
     * @param connectTimeout connect timeout; only used when {@code httpClient} is {@code null}.
     *                       {@code null} falls back to the default (10s).
     * @param requestTimeout per-request timeout. {@code null} falls back to the default (30s).
     * @param maxRetries     maximum retry attempts on transient failures (must be {@code >= 0}).
     */
    public GraphQLTransport(String baseUrl, String apiKey, HttpClient httpClient,
                            Duration connectTimeout, Duration requestTimeout, int maxRetries) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.apiKey = apiKey;
        Duration effectiveConnect = connectTimeout != null ? connectTimeout : DEFAULT_CONNECT_TIMEOUT;
        this.httpClient = httpClient != null
                ? httpClient
                : HttpClient.newBuilder().connectTimeout(effectiveConnect).build();
        this.requestTimeout = requestTimeout != null ? requestTimeout : DEFAULT_REQUEST_TIMEOUT;
        this.maxRetries = Math.max(0, maxRetries);
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Exposes the {@link ObjectMapper} used by this transport so services can deserialise the
     * {@code data} subtree of a GraphQL response into typed records.
     *
     * @return the shared Jackson mapper.
     */
    public ObjectMapper getMapper() {
        return mapper;
    }

    /**
     * Execute a GraphQL operation and return the {@code data} subtree.
     *
     * @param query     GraphQL query or mutation string.
     * @param variables variable map; {@code null} sends no variables.
     * @return the {@code data} object from the response (never {@code null}; an empty node when the
     *         server omits {@code data}).
     * @throws AuthException       on 401/403 responses or {@code UNAUTHORIZED} errors.
     * @throws NotFoundException   on {@code NOT_FOUND} errors.
     * @throws ConflictException   on {@code CONFLICT} or "already exists" errors.
     * @throws ValidationException on {@code VALIDATION} errors.
     * @throws RateLimitException  on HTTP 429 responses (after retries are exhausted).
     * @throws ServerException     on HTTP 5xx responses (after retries are exhausted).
     * @throws ApiException        on any other API error.
     * @throws HivehookException   on transport-level failures.
     */
    public JsonNode execute(String query, Map<String, Object> variables) {
        HttpRequest request = buildRequest(query, variables);
        int attempt = 0;
        while (true) {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return processResponse(response);
            } catch (RateLimitException | ServerException e) {
                if (attempt >= maxRetries) {
                    throw e;
                }
                sleepFor(backoffMillis(attempt, e));
                attempt++;
            } catch (IOException e) {
                if (attempt >= maxRetries) {
                    throw new HivehookException("request failed: " + e.getMessage(), e);
                }
                sleepFor(backoffMillis(attempt, null));
                attempt++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new HivehookException("request interrupted", e);
            }
        }
    }

    /**
     * Asynchronously execute a GraphQL operation and return the {@code data} subtree.
     *
     * <p>The returned future completes normally with the {@code data} object from the response
     * (an empty node when the server omits {@code data}). On failure it completes exceptionally
     * with the same exception types as {@link #execute(String, Map)}. Transient failures
     * ({@link IOException}, {@link RateLimitException}, {@link ServerException}) are retried up
     * to the configured retry budget before the future completes exceptionally.
     *
     * @param query     GraphQL query or mutation string.
     * @param variables variable map; {@code null} sends no variables.
     * @return future completing with the {@code data} subtree, or completing exceptionally on error.
     */
    public CompletableFuture<JsonNode> executeAsync(String query, Map<String, Object> variables) {
        HttpRequest request;
        try {
            request = buildRequest(query, variables);
        } catch (RuntimeException e) {
            CompletableFuture<JsonNode> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
        return executeAsyncWithRetry(request, 0);
    }

    private CompletableFuture<JsonNode> executeAsyncWithRetry(HttpRequest request, int attempt) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(this::processResponse)
                .handle((data, throwable) -> {
                    if (throwable == null) {
                        return CompletableFuture.completedFuture(data);
                    }
                    Throwable cause = unwrap(throwable);
                    if (cause instanceof RateLimitException rle) {
                        if (attempt >= maxRetries) {
                            return GraphQLTransport.<JsonNode>failed(rle);
                        }
                        return GraphQLTransport.delay(backoffMillis(attempt, rle))
                                .thenCompose(v -> executeAsyncWithRetry(request, attempt + 1));
                    }
                    if (cause instanceof ServerException se) {
                        if (attempt >= maxRetries) {
                            return GraphQLTransport.<JsonNode>failed(se);
                        }
                        return GraphQLTransport.delay(backoffMillis(attempt, se))
                                .thenCompose(v -> executeAsyncWithRetry(request, attempt + 1));
                    }
                    if (cause instanceof IOException ioe) {
                        if (attempt >= maxRetries) {
                            return GraphQLTransport.<JsonNode>failed(new HivehookException("request failed: " + ioe.getMessage(), ioe));
                        }
                        return GraphQLTransport.delay(backoffMillis(attempt, null))
                                .thenCompose(v -> executeAsyncWithRetry(request, attempt + 1));
                    }
                    if (cause instanceof HivehookException he) {
                        return GraphQLTransport.<JsonNode>failed(he);
                    }
                    if (cause instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                        return GraphQLTransport.<JsonNode>failed(new HivehookException("request interrupted", cause));
                    }
                    return GraphQLTransport.<JsonNode>failed(new HivehookException("request failed: " + cause.getMessage(), cause));
                })
                .thenCompose(f -> f);
    }

    private static Throwable unwrap(Throwable t) {
        if ((t instanceof CompletionException || t instanceof java.util.concurrent.ExecutionException)
                && t.getCause() != null) {
            return t.getCause();
        }
        return t;
    }

    private static <T> CompletableFuture<T> failed(Throwable t) {
        CompletableFuture<T> f = new CompletableFuture<>();
        f.completeExceptionally(t);
        return f;
    }

    private static CompletableFuture<Void> delay(long millis) {
        if (millis <= 0) {
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<Void> f = new CompletableFuture<>();
        retryScheduler().schedule(() -> f.complete(null), millis, TimeUnit.MILLISECONDS);
        return f;
    }

    private static ScheduledExecutorService retryScheduler() {
        ScheduledExecutorService s = RETRY_SCHEDULER.get();
        if (s != null) {
            return s;
        }
        ScheduledExecutorService created = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "hivehook-retry-scheduler");
            t.setDaemon(true);
            return t;
        });
        if (RETRY_SCHEDULER.compareAndSet(null, created)) {
            return created;
        }
        created.shutdownNow();
        return RETRY_SCHEDULER.get();
    }

    private long backoffMillis(int attempt, ApiException e) {
        if (e instanceof RateLimitException rle) {
            Optional<Long> retry = rle.retryAfterMillis();
            if (retry.isPresent()) {
                return Math.max(0L, retry.get());
            }
        }
        return BASE_BACKOFF_MILLIS * (1L << attempt);
    }

    private static void sleepFor(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new HivehookException("retry sleep interrupted", e);
        }
    }

    private HttpRequest buildRequest(String query, Map<String, Object> variables) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("query", query);
        if (variables != null && !variables.isEmpty()) {
            payload.put("variables", variables);
        }

        String body;
        try {
            body = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new HivehookException("failed to encode request: " + e.getMessage(), e);
        }

        HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/graphql"))
                .header("Content-Type", "application/json")
                .header("User-Agent", USER_AGENT)
                .timeout(requestTimeout);

        if (apiKey != null && !apiKey.isEmpty()) {
            reqBuilder.header("Authorization", "Bearer " + apiKey);
        }

        reqBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
        return reqBuilder.build();
    }

    private JsonNode processResponse(HttpResponse<String> response) {
        JsonNode respBody;
        try {
            respBody = response.body() == null || response.body().isEmpty()
                    ? mapper.createObjectNode()
                    : mapper.readTree(response.body());
        } catch (JsonProcessingException e) {
            if (response.statusCode() >= 500) {
                throw new ServerException("HTTP " + response.statusCode(), response.statusCode());
            }
            if (response.statusCode() == 429) {
                throw new RateLimitException("rate limited", parseRetryAfterMillis(response));
            }
            throw new ApiException("invalid JSON response: " + e.getMessage(), response.statusCode());
        }
        return handleResponse(response, respBody);
    }

    private JsonNode handleResponse(HttpResponse<String> response, JsonNode body) {
        int statusCode = response.statusCode();
        if (statusCode == 401 || statusCode == 403) {
            String msg = extractError(body);
            Map<String, Object> ext = extractExtensions(body);
            throw new AuthException(msg.isEmpty() ? "authentication failed" : msg, statusCode, ext);
        }

        if (statusCode == 429) {
            String msg = extractError(body);
            Map<String, Object> ext = extractExtensions(body);
            throw new RateLimitException(
                    msg.isEmpty() ? "rate limited" : msg,
                    parseRetryAfterMillis(response),
                    ext);
        }

        if (statusCode >= 500 && statusCode < 600) {
            String msg = extractError(body);
            Map<String, Object> ext = extractExtensions(body);
            throw new ServerException(msg.isEmpty() ? "HTTP " + statusCode : msg, statusCode, ext);
        }

        if (statusCode >= 400) {
            String msg = extractError(body);
            Map<String, Object> ext = extractExtensions(body);
            throw new ApiException(msg.isEmpty() ? "HTTP " + statusCode : msg, statusCode, ext);
        }

        JsonNode errors = body.path("errors");
        if (errors.isArray() && !errors.isEmpty()) {
            JsonNode err = errors.get(0);
            String msg = err.path("message").asText("");
            String code = err.path("extensions").path("code").asText("");
            Map<String, Object> ext = nodeToMap(err.path("extensions"));
            String lower = msg.toLowerCase();

            if ("NOT_FOUND".equals(code) || lower.contains("not found")) {
                throw new NotFoundException(msg, ext);
            }
            if ("CONFLICT".equals(code) || lower.contains("conflict") || lower.contains("already exists")) {
                throw new ConflictException(msg, ext);
            }
            if ("VALIDATION".equals(code) || lower.contains("validation")) {
                throw new ValidationException(msg, ext);
            }
            if ("UNAUTHORIZED".equals(code)) {
                throw new AuthException(msg, ext);
            }
            throw new ApiException(msg, null, ext);
        }

        JsonNode data = body.get("data");
        if (data == null || data.isMissingNode() || data.isNull() || !(data instanceof ObjectNode)) {
            return mapper.createObjectNode();
        }
        return data;
    }

    private String extractError(JsonNode body) {
        JsonNode errors = body.path("errors");
        if (errors.isArray() && !errors.isEmpty()) {
            return errors.get(0).path("message").asText("");
        }
        return body.path("message").asText("");
    }

    private Map<String, Object> extractExtensions(JsonNode body) {
        JsonNode errors = body.path("errors");
        if (errors.isArray() && !errors.isEmpty()) {
            return nodeToMap(errors.get(0).path("extensions"));
        }
        return Collections.emptyMap();
    }

    private Map<String, Object> nodeToMap(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull() || !node.isObject()) {
            return Collections.emptyMap();
        }
        try {
            return mapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
        } catch (IllegalArgumentException e) {
            return Collections.emptyMap();
        }
    }

    private Long parseRetryAfterMillis(HttpResponse<?> response) {
        Optional<String> value = response.headers().firstValue("Retry-After");
        if (value.isEmpty()) {
            return null;
        }
        String raw = value.get().trim();
        if (raw.isEmpty()) {
            return null;
        }
        try {
            long seconds = Long.parseLong(raw);
            if (seconds < 0) {
                return null;
            }
            return TimeUnit.SECONDS.toMillis(seconds);
        } catch (NumberFormatException ignored) {
            // fall through to HTTP-date parsing
        }
        try {
            Instant when = Instant.from(java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.parse(raw));
            long delta = when.toEpochMilli() - System.currentTimeMillis();
            return Math.max(0L, delta);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}
