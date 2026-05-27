package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.ListResult;
import com.hivehook.sdk.types.Message;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for outbound {@link Message}s.
 */
public final class MessageService extends BaseService {
    private static final String FIELDS = "id applicationId eventType payload idempotencyKey status createdAt";
    private static final String LIST = "query($applicationId: UUID, $eventType: String, $status: MessageStatus, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { messages(applicationId: $applicationId, eventType: $eventType, status: $status, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { message(id: $id) { " + FIELDS + " } }";
    private static final String SEND = "mutation($input: SendMessageInput!) { sendMessage(input: $input) { " + FIELDS + " } }";

    /** @param transport transport to use. */
    public MessageService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List messages.
     *
     * @param applicationId optional application UUID filter.
     * @param eventType     optional event-type filter.
     * @param status        optional status filter.
     * @param search        optional substring search.
     * @param limit         page size (offset pagination).
     * @param offset        page offset (offset pagination).
     * @param after         cursor for cursor pagination.
     * @param first         page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Message> list(String applicationId, String eventType, String status, String search,
                                    Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "applicationId", applicationId, "eventType", eventType, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("messages"), Message.class);
    }

    /**
     * Fetch a message by id.
     *
     * @param id message UUID.
     * @return the message, or {@code null}.
     */
    public Message get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("message"), Message.class);
    }

    /**
     * Send a message with a UTF-8 string payload. The payload is Base64-encoded before transmission.
     *
     * @param applicationId  target application UUID.
     * @param eventType      event-type string.
     * @param payload        UTF-8 payload.
     * @param idempotencyKey idempotency key (nullable or empty to skip).
     * @return the persisted message.
     */
    public Message send(String applicationId, String eventType, String payload, String idempotencyKey) {
        return send(applicationId, eventType, payload.getBytes(StandardCharsets.UTF_8), idempotencyKey);
    }

    /**
     * Send a message with a raw-byte payload. The payload is Base64-encoded before transmission.
     *
     * @param applicationId  target application UUID.
     * @param eventType      event-type string.
     * @param payload        payload bytes.
     * @param idempotencyKey idempotency key (nullable or empty to skip).
     * @return the persisted message.
     */
    public Message send(String applicationId, String eventType, byte[] payload, String idempotencyKey) {
        String payloadB64 = Base64.getEncoder().encodeToString(payload);
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("applicationId", applicationId);
        input.put("eventType", eventType);
        input.put("payload", payloadB64);
        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
            input.put("idempotencyKey", idempotencyKey);
        }
        JsonNode data = transport.execute(SEND, vars("input", input));
        return toType(data.get("sendMessage"), Message.class);
    }

    /**
     * Asynchronously list messages.
     *
     * @param applicationId optional application UUID filter.
     * @param eventType     optional event-type filter.
     * @param status        optional status filter.
     * @param search        optional substring search.
     * @param limit         page size (offset pagination).
     * @param offset        page offset (offset pagination).
     * @param after         cursor for cursor pagination.
     * @param first         page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Message>> listAsync(String applicationId, String eventType, String status, String search,
                                                            Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "applicationId", applicationId, "eventType", eventType, "status", status, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("messages"), Message.class));
    }

    /**
     * Asynchronously fetch a message by id.
     *
     * @param id message UUID.
     * @return future completing with the message, or {@code null}.
     */
    public CompletableFuture<Message> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("message"), Message.class));
    }

    /**
     * Asynchronously send a message with a UTF-8 string payload. The payload is Base64-encoded before transmission.
     *
     * @param applicationId  target application UUID.
     * @param eventType      event-type string.
     * @param payload        UTF-8 payload.
     * @param idempotencyKey idempotency key (nullable or empty to skip).
     * @return future completing with the persisted message.
     */
    public CompletableFuture<Message> sendAsync(String applicationId, String eventType, String payload, String idempotencyKey) {
        return sendAsync(applicationId, eventType, payload.getBytes(StandardCharsets.UTF_8), idempotencyKey);
    }

    /**
     * Asynchronously send a message with a raw-byte payload. The payload is Base64-encoded before transmission.
     *
     * @param applicationId  target application UUID.
     * @param eventType      event-type string.
     * @param payload        payload bytes.
     * @param idempotencyKey idempotency key (nullable or empty to skip).
     * @return future completing with the persisted message.
     */
    public CompletableFuture<Message> sendAsync(String applicationId, String eventType, byte[] payload, String idempotencyKey) {
        String payloadB64 = Base64.getEncoder().encodeToString(payload);
        Map<String, Object> input = new LinkedHashMap<>();
        input.put("applicationId", applicationId);
        input.put("eventType", eventType);
        input.put("payload", payloadB64);
        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
            input.put("idempotencyKey", idempotencyKey);
        }
        return transport.executeAsync(SEND, vars("input", input))
                .thenApply(data -> toType(data.get("sendMessage"), Message.class));
    }

    public java.util.List<Message> listAll(String applicationId, String eventType, String status, String search) {
        return paginate(after -> list(applicationId, eventType, status, search, null, null, after, null));
    }
}
