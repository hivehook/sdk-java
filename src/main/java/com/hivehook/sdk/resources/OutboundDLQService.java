package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.OutboundDLQEntry;
import com.hivehook.sdk.types.PurgeResult;
import com.hivehook.sdk.types.ReplayResult;

import java.util.concurrent.CompletableFuture;

/**
 * Service for inspecting and managing the outbound dead-letter queue.
 */
public final class OutboundDLQService extends BaseService {
    private static final String FIELDS = "id deliveryId messageId lastError replayedAt createdAt";
    private static final String LIST = "query($messageId: UUID, $replayed: Boolean, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { outboundDlqEntries(messageId: $messageId, replayed: $replayed, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String REPLAY = "mutation($id: UUID!) { replayOutboundDlqEntry(id: $id) }";
    private static final String REPLAY_ALL = "mutation { replayAllOutboundDlq { deliveries } }";
    private static final String PURGE = "mutation($olderThan: String) { purgeOutboundDlq(olderThan: $olderThan) { purged } }";

    /** @param transport transport to use. */
    public OutboundDLQService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List outbound DLQ entries.
     *
     * @param messageId optional message UUID filter.
     * @param replayed  optional replayed filter.
     * @param search    optional substring search.
     * @param limit     page size (offset pagination).
     * @param offset    page offset (offset pagination).
     * @param after     cursor for cursor pagination.
     * @param first     page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<OutboundDLQEntry> list(String messageId, Boolean replayed, String search,
                                             Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "messageId", messageId, "replayed", replayed, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("outboundDlqEntries"), OutboundDLQEntry.class);
    }

    /**
     * Replay one outbound DLQ entry.
     *
     * @param id DLQ entry UUID.
     * @return {@code true} on success.
     */
    public boolean replay(String id) {
        JsonNode data = transport.execute(REPLAY, vars("id", id));
        return data.path("replayOutboundDlqEntry").asBoolean(false);
    }

    /**
     * Replay every entry in the outbound DLQ.
     *
     * @return number of deliveries re-queued.
     */
    public ReplayResult replayAll() {
        JsonNode data = transport.execute(REPLAY_ALL, null);
        ReplayResult r = toType(data.get("replayAllOutboundDlq"), ReplayResult.class);
        return r != null ? r : new ReplayResult(0);
    }

    /**
     * Purge outbound DLQ entries older than the given duration.
     *
     * @param olderThan Go-style duration string, or {@code null} to purge everything.
     * @return number of entries removed.
     */
    public PurgeResult purge(String olderThan) {
        JsonNode data = transport.execute(PURGE, vars("olderThan", olderThan));
        PurgeResult r = toType(data.get("purgeOutboundDlq"), PurgeResult.class);
        return r != null ? r : new PurgeResult(0);
    }

    /**
     * Asynchronously list outbound DLQ entries.
     *
     * @param messageId optional message UUID filter.
     * @param replayed  optional replayed filter.
     * @param search    optional substring search.
     * @param limit     page size (offset pagination).
     * @param offset    page offset (offset pagination).
     * @param after     cursor for cursor pagination.
     * @param first     page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<OutboundDLQEntry>> listAsync(String messageId, Boolean replayed, String search,
                                                                     Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "messageId", messageId, "replayed", replayed, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("outboundDlqEntries"), OutboundDLQEntry.class));
    }

    /**
     * Asynchronously replay one outbound DLQ entry.
     *
     * @param id DLQ entry UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> replayAsync(String id) {
        return transport.executeAsync(REPLAY, vars("id", id))
                .thenApply(data -> data.path("replayOutboundDlqEntry").asBoolean(false));
    }

    /**
     * Asynchronously replay every entry in the outbound DLQ.
     *
     * @return future completing with the number of deliveries re-queued.
     */
    public CompletableFuture<ReplayResult> replayAllAsync() {
        return transport.executeAsync(REPLAY_ALL, null)
                .thenApply(data -> {
                    ReplayResult r = toType(data.get("replayAllOutboundDlq"), ReplayResult.class);
                    return r != null ? r : new ReplayResult(0);
                });
    }

    /**
     * Asynchronously purge outbound DLQ entries older than the given duration.
     *
     * @param olderThan Go-style duration string, or {@code null} to purge everything.
     * @return future completing with the number of entries removed.
     */
    public CompletableFuture<PurgeResult> purgeAsync(String olderThan) {
        return transport.executeAsync(PURGE, vars("olderThan", olderThan))
                .thenApply(data -> {
                    PurgeResult r = toType(data.get("purgeOutboundDlq"), PurgeResult.class);
                    return r != null ? r : new PurgeResult(0);
                });
    }

    public java.util.List<OutboundDLQEntry> listAll(String messageId, Boolean replayed, String search) {
        return paginate(after -> list(messageId, replayed, search, null, null, after, null));
    }
}
