package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.Application;
import com.hivehook.sdk.types.ListResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for outbound {@link Application}s.
 */
public final class ApplicationService extends BaseService {
    private static final String FIELDS = "id name uid createdAt";
    private static final String LIST = "query($search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { applications(search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { application(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($input: CreateApplicationInput!) { createApplication(input: $input) { " + FIELDS + " } }";
    private static final String UPDATE = "mutation($id: UUID!, $input: UpdateApplicationInput!) { updateApplication(id: $id, input: $input) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteApplication(id: $id) }";

    /** @param transport transport to use. */
    public ApplicationService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List applications.
     *
     * @param search optional substring search.
     * @param limit  page size (offset pagination).
     * @param offset page offset (offset pagination).
     * @param after  cursor for cursor pagination.
     * @param first  page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Application> list(String search, Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("applications"), Application.class);
    }

    /**
     * Fetch an application by id.
     *
     * @param id application UUID.
     * @return the application, or {@code null}.
     */
    public Application get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("application"), Application.class);
    }

    /**
     * Create an application.
     *
     * @param input map matching {@code CreateApplicationInput}.
     * @return the created application.
     */
    public Application create(Map<String, Object> input) {
        JsonNode data = transport.execute(CREATE, vars("input", input));
        return toType(data.get("createApplication"), Application.class);
    }

    /**
     * Update an application.
     *
     * @param id    application UUID.
     * @param input map of fields to update.
     * @return the updated application.
     */
    public Application update(String id, Map<String, Object> input) {
        JsonNode data = transport.execute(UPDATE, vars("id", id, "input", input));
        return toType(data.get("updateApplication"), Application.class);
    }

    /**
     * Delete an application.
     *
     * @param id application UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteApplication").asBoolean(false);
    }

    /**
     * Asynchronously list applications.
     *
     * @param search optional substring search.
     * @param limit  page size (offset pagination).
     * @param offset page offset (offset pagination).
     * @param after  cursor for cursor pagination.
     * @param first  page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Application>> listAsync(String search, Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("applications"), Application.class));
    }

    /**
     * Asynchronously fetch an application by id.
     *
     * @param id application UUID.
     * @return future completing with the application, or {@code null}.
     */
    public CompletableFuture<Application> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("application"), Application.class));
    }

    /**
     * Asynchronously create an application.
     *
     * @param input map matching {@code CreateApplicationInput}.
     * @return future completing with the created application.
     */
    public CompletableFuture<Application> createAsync(Map<String, Object> input) {
        return transport.executeAsync(CREATE, vars("input", input))
                .thenApply(data -> toType(data.get("createApplication"), Application.class));
    }

    /**
     * Asynchronously update an application.
     *
     * @param id    application UUID.
     * @param input map of fields to update.
     * @return future completing with the updated application.
     */
    public CompletableFuture<Application> updateAsync(String id, Map<String, Object> input) {
        return transport.executeAsync(UPDATE, vars("id", id, "input", input))
                .thenApply(data -> toType(data.get("updateApplication"), Application.class));
    }

    /**
     * Asynchronously delete an application.
     *
     * @param id application UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteApplication").asBoolean(false));
    }

    public java.util.List<Application> listAll(String search) {
        return paginate(after -> list(search, null, null, after, null));
    }
}
