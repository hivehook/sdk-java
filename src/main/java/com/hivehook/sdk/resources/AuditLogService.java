package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.AuditLog;
import com.hivehook.sdk.types.ListResult;

import java.util.concurrent.CompletableFuture;

/**
 * Read-only service for the audit log.
 */
public final class AuditLogService extends BaseService {
    private static final String FIELDS = "id actorType actorId actorName action resourceType resourceId orgId ipAddress userAgent details createdAt";
    private static final String LIST = "query($actorType: String, $resourceType: String, $resourceId: UUID, $action: String, $since: Time, $until: Time, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { auditLogs(actorType: $actorType, resourceType: $resourceType, resourceId: $resourceId, action: $action, since: $since, until: $until, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { auditLog(id: $id) { " + FIELDS + " } }";

    /** @param transport transport to use. */
    public AuditLogService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List audit-log entries.
     *
     * @param actorType    optional actor-type filter.
     * @param resourceType optional resource-type filter.
     * @param resourceId   optional resource UUID filter.
     * @param action       optional action filter.
     * @param since        optional lower-bound timestamp (RFC3339).
     * @param until        optional upper-bound timestamp (RFC3339).
     * @param search       optional substring search.
     * @param limit        page size (offset pagination).
     * @param offset       page offset (offset pagination).
     * @param after        cursor for cursor pagination.
     * @param first        page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<AuditLog> list(String actorType, String resourceType, String resourceId,
                                     String action, String since, String until, String search,
                                     Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "actorType", actorType, "resourceType", resourceType, "resourceId", resourceId,
                "action", action, "since", since, "until", until, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("auditLogs"), AuditLog.class);
    }

    /**
     * Fetch one audit-log entry by id.
     *
     * @param id audit log UUID.
     * @return the entry, or {@code null}.
     */
    public AuditLog get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("auditLog"), AuditLog.class);
    }

    /**
     * Asynchronously list audit-log entries.
     *
     * @param actorType    optional actor-type filter.
     * @param resourceType optional resource-type filter.
     * @param resourceId   optional resource UUID filter.
     * @param action       optional action filter.
     * @param since        optional lower-bound timestamp (RFC3339).
     * @param until        optional upper-bound timestamp (RFC3339).
     * @param search       optional substring search.
     * @param limit        page size (offset pagination).
     * @param offset       page offset (offset pagination).
     * @param after        cursor for cursor pagination.
     * @param first        page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<AuditLog>> listAsync(String actorType, String resourceType, String resourceId,
                                                             String action, String since, String until, String search,
                                                             Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "actorType", actorType, "resourceType", resourceType, "resourceId", resourceId,
                "action", action, "since", since, "until", until, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("auditLogs"), AuditLog.class));
    }

    /**
     * Asynchronously fetch one audit-log entry by id.
     *
     * @param id audit log UUID.
     * @return future completing with the entry, or {@code null}.
     */
    public CompletableFuture<AuditLog> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("auditLog"), AuditLog.class));
    }

    public java.util.List<AuditLog> listAll(String actorType, String resourceType, String resourceId, String action, String since, String until, String search) {
        return paginate(after -> list(actorType, resourceType, resourceId, action, since, until, search, null, null, after, null));
    }
}
