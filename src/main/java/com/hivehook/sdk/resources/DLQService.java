package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.DLQEntry;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.PurgeResult;
import com.hivehook.sdk.types.ReplayResult;

import java.util.concurrent.CompletableFuture;

/**
 * Service for inspecting and managing the inbound dead-letter queue.
 */
public final class DLQService extends BaseService {
    private static final String FIELDS = "id deliveryId eventId lastError replayedAt createdAt";
    private static final String LIST = "query($eventId: UUID, $replayed: Boolean, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { dlqEntries(eventId: $eventId, replayed: $replayed, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String REPLAY = "mutation($id: UUID!) { replayDLQEntry(id: $id) }";
    private static final String REPLAY_ALL = "mutation { replayAllDLQ { deliveries } }";
    private static final String PURGE = "mutation($olderThan: String) { purgeDLQ(olderThan: $olderThan) { purged } }";

    /** @param transport transport to use. */
    public DLQService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List DLQ entries.
     *
     * @param eventId  optional event UUID filter.
     * @param replayed optional replayed filter.
     * @param search   optional substring search.
     * @param limit    page size (offset pagination).
     * @param offset   page offset (offset pagination).
     * @param after    cursor for cursor pagination.
     * @param first    page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<DLQEntry> list(String eventId, Boolean replayed, String search,
                                     Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "eventId", eventId, "replayed", replayed, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("dlqEntries"), DLQEntry.class);
    }

    /**
     * Replay one DLQ entry.
     *
     * @param id DLQ entry UUID.
     * @return {@code true} on success.
     */
    public boolean replay(String id) {
        JsonNode data = transport.execute(REPLAY, vars("id", id));
        return data.path("replayDLQEntry").asBoolean(false);
    }

    /**
     * Replay every entry currently in the DLQ.
     *
     * @return number of deliveries re-queued.
     */
    public ReplayResult replayAll() {
        JsonNode data = transport.execute(REPLAY_ALL, null);
        ReplayResult r = toType(data.get("replayAllDLQ"), ReplayResult.class);
        return r != null ? r : new ReplayResult(0);
    }

    /**
     * Purge DLQ entries older than the given duration.
     *
     * @param olderThan Go-style duration string, or {@code null} to purge everything.
     * @return number of entries removed.
     */
    public PurgeResult purge(String olderThan) {
        JsonNode data = transport.execute(PURGE, vars("olderThan", olderThan));
        PurgeResult r = toType(data.get("purgeDLQ"), PurgeResult.class);
        return r != null ? r : new PurgeResult(0);
    }

    /**
     * Asynchronously list DLQ entries.
     *
     * @param eventId  optional event UUID filter.
     * @param replayed optional replayed filter.
     * @param search   optional substring search.
     * @param limit    page size (offset pagination).
     * @param offset   page offset (offset pagination).
     * @param after    cursor for cursor pagination.
     * @param first    page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<DLQEntry>> listAsync(String eventId, Boolean replayed, String search,
                                                             Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "eventId", eventId, "replayed", replayed, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("dlqEntries"), DLQEntry.class));
    }

    /**
     * Asynchronously replay one DLQ entry.
     *
     * @param id DLQ entry UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> replayAsync(String id) {
        return transport.executeAsync(REPLAY, vars("id", id))
                .thenApply(data -> data.path("replayDLQEntry").asBoolean(false));
    }

    /**
     * Asynchronously replay every entry currently in the DLQ.
     *
     * @return future completing with the number of deliveries re-queued.
     */
    public CompletableFuture<ReplayResult> replayAllAsync() {
        return transport.executeAsync(REPLAY_ALL, null)
                .thenApply(data -> {
                    ReplayResult r = toType(data.get("replayAllDLQ"), ReplayResult.class);
                    return r != null ? r : new ReplayResult(0);
                });
    }

    /**
     * Asynchronously purge DLQ entries older than the given duration.
     *
     * @param olderThan Go-style duration string, or {@code null} to purge everything.
     * @return future completing with the number of entries removed.
     */
    public CompletableFuture<PurgeResult> purgeAsync(String olderThan) {
        return transport.executeAsync(PURGE, vars("olderThan", olderThan))
                .thenApply(data -> {
                    PurgeResult r = toType(data.get("purgeDLQ"), PurgeResult.class);
                    return r != null ? r : new PurgeResult(0);
                });
    }
}
