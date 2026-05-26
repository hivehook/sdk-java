package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.TransformTestResult;
import com.hivehook.sdk.types.Transformation;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for JavaScript {@link Transformation}s.
 */
public final class TransformationService extends BaseService {
    private static final String FIELDS = "id name description code enabled failOpen timeoutMs createdAt updatedAt";
    private static final String TEST_FIELDS = "success output error durationMs";
    private static final String LIST = "query($enabled: Boolean, $search: String, $after: String, $first: Int) { transformations(enabled: $enabled, search: $search, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { transformation(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateTransformationInput!) { createTransformation(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateTransformationInput!) { updateTransformation(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteTransformation(id: $id) }";
    private static final String TEST = "mutation($input: TestTransformationInput!) { testTransformation(input: $input) { " + TEST_FIELDS + " } }";

    /** @param transport transport to use. */
    public TransformationService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List transformations.
     *
     * @param enabled optional enabled filter.
     * @param search  optional substring search.
     * @param after   cursor for cursor pagination.
     * @param first   page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Transformation> list(Boolean enabled, String search, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "enabled", enabled, "search", search, "after", after, "first", first));
        return parseList(data.get("transformations"), Transformation.class);
    }

    /**
     * Fetch a transformation by id.
     *
     * @param id transformation UUID.
     * @return the transformation, or {@code null}.
     */
    public Transformation get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("transformation"), Transformation.class);
    }

    /**
     * Create a transformation.
     *
     * @param name        human-readable name.
     * @param description short description.
     * @param code        JavaScript source containing a top-level {@code transform} function.
     * @param failOpen    {@code true} to skip the transform on error rather than failing the delivery.
     * @param timeoutMs   per-event execution timeout in milliseconds.
     * @return the created transformation.
     */
    public Transformation create(String name, String description, String code, Boolean failOpen, Integer timeoutMs) {
        Map<String, Object> input = vars(
                "name", name, "description", description, "code", code,
                "failOpen", failOpen, "timeoutMs", timeoutMs);
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createTransformation"), Transformation.class);
    }

    /**
     * Update a transformation. {@code null} arguments are omitted from the request.
     *
     * @param id          transformation UUID.
     * @param name        new name, or {@code null} to leave unchanged.
     * @param description new description, or {@code null}.
     * @param code        new code, or {@code null}.
     * @param enabled     new enabled state, or {@code null}.
     * @param failOpen    new fail-open setting, or {@code null}.
     * @param timeoutMs   new timeout, or {@code null}.
     * @return the updated transformation.
     */
    public Transformation update(String id, String name, String description, String code,
                                 Boolean enabled, Boolean failOpen, Integer timeoutMs) {
        Map<String, Object> input = vars(
                "name", name, "description", description, "code", code,
                "enabled", enabled, "failOpen", failOpen, "timeoutMs", timeoutMs);
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateTransformation"), Transformation.class);
    }

    /**
     * Delete a transformation.
     *
     * @param id transformation UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteTransformation").asBoolean(false);
    }

    /**
     * Run a transformation against a sample payload server-side without persisting it.
     *
     * @param code      JavaScript source.
     * @param payload   sample event payload.
     * @param eventType event-type string (nullable).
     * @param headers   sample headers (nullable).
     * @return the dry-run outcome.
     */
    public TransformTestResult test(String code, Map<String, Object> payload, String eventType, Map<String, Object> headers) {
        Map<String, Object> input = vars("code", code, "payload", payload, "eventType", eventType, "headers", headers);
        JsonNode data = transport.execute(TEST, vars("input", input));
        return toType(data.get("testTransformation"), TransformTestResult.class);
    }

    /**
     * Asynchronously list transformations.
     *
     * @param enabled optional enabled filter.
     * @param search  optional substring search.
     * @param after   cursor for cursor pagination.
     * @param first   page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Transformation>> listAsync(Boolean enabled, String search, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "enabled", enabled, "search", search, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("transformations"), Transformation.class));
    }

    /**
     * Asynchronously fetch a transformation by id.
     *
     * @param id transformation UUID.
     * @return future completing with the transformation, or {@code null}.
     */
    public CompletableFuture<Transformation> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("transformation"), Transformation.class));
    }

    /**
     * Asynchronously create a transformation.
     *
     * @param name        human-readable name.
     * @param description short description.
     * @param code        JavaScript source containing a top-level {@code transform} function.
     * @param failOpen    {@code true} to skip the transform on error rather than failing the delivery.
     * @param timeoutMs   per-event execution timeout in milliseconds.
     * @return future completing with the created transformation.
     */
    public CompletableFuture<Transformation> createAsync(String name, String description, String code, Boolean failOpen, Integer timeoutMs) {
        Map<String, Object> input = vars(
                "name", name, "description", description, "code", code,
                "failOpen", failOpen, "timeoutMs", timeoutMs);
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createTransformation"), Transformation.class));
    }

    /**
     * Asynchronously update a transformation. {@code null} arguments are omitted from the request.
     *
     * @param id          transformation UUID.
     * @param name        new name, or {@code null} to leave unchanged.
     * @param description new description, or {@code null}.
     * @param code        new code, or {@code null}.
     * @param enabled     new enabled state, or {@code null}.
     * @param failOpen    new fail-open setting, or {@code null}.
     * @param timeoutMs   new timeout, or {@code null}.
     * @return future completing with the updated transformation.
     */
    public CompletableFuture<Transformation> updateAsync(String id, String name, String description, String code,
                                                         Boolean enabled, Boolean failOpen, Integer timeoutMs) {
        Map<String, Object> input = vars(
                "name", name, "description", description, "code", code,
                "enabled", enabled, "failOpen", failOpen, "timeoutMs", timeoutMs);
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateTransformation"), Transformation.class));
    }

    /**
     * Asynchronously delete a transformation.
     *
     * @param id transformation UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteTransformation").asBoolean(false));
    }

    /**
     * Asynchronously run a transformation against a sample payload server-side without persisting it.
     *
     * @param code      JavaScript source.
     * @param payload   sample event payload.
     * @param eventType event-type string (nullable).
     * @param headers   sample headers (nullable).
     * @return future completing with the dry-run outcome.
     */
    public CompletableFuture<TransformTestResult> testAsync(String code, Map<String, Object> payload, String eventType, Map<String, Object> headers) {
        Map<String, Object> input = vars("code", code, "payload", payload, "eventType", eventType, "headers", headers);
        return transport.executeAsync(TEST, vars("input", input))
                .thenApply(data -> toType(data.get("testTransformation"), TransformTestResult.class));
    }
}
