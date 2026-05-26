package com.hivehook.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivehook.sdk.GraphQLTransport;
import com.hivehook.sdk.types.Bookmark;
import com.hivehook.sdk.types.ListResult;

import java.util.concurrent.CompletableFuture;

/**
 * CRUD service for event {@link Bookmark}s.
 */
public final class BookmarkService extends BaseService {
    private static final String FIELDS = "id eventId name notes createdAt";
    private static final String LIST = "query($eventId: UUID, $search: String, $limit: Int, $offset: Int, $after: String, $first: Int) { bookmarks(eventId: $eventId, search: $search, limit: $limit, offset: $offset, after: $after, first: $first) { nodes { " + FIELDS + " } pageInfo { total limit offset endCursor hasNextPage } } }";
    private static final String GET = "query($id: UUID!) { bookmark(id: $id) { " + FIELDS + " } }";
    private static final String CREATE = "mutation($eventId: UUID!, $name: String, $notes: String) { createBookmark(eventId: $eventId, name: $name, notes: $notes) { " + FIELDS + " } }";
    private static final String DELETE = "mutation($id: UUID!) { deleteBookmark(id: $id) }";

    /** @param transport transport to use. */
    public BookmarkService(GraphQLTransport transport) {
        super(transport);
    }

    /**
     * List bookmarks.
     *
     * @param eventId optional event UUID filter.
     * @param search  optional substring search.
     * @param limit   page size (offset pagination).
     * @param offset  page offset (offset pagination).
     * @param after   cursor for cursor pagination.
     * @param first   page size for cursor pagination.
     * @return paginated result.
     */
    public ListResult<Bookmark> list(String eventId, String search,
                                     Integer limit, Integer offset, String after, Integer first) {
        JsonNode data = transport.execute(LIST, vars(
                "eventId", eventId, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first));
        return parseList(data.get("bookmarks"), Bookmark.class);
    }

    /**
     * Fetch a bookmark by id.
     *
     * @param id bookmark UUID.
     * @return the bookmark, or {@code null}.
     */
    public Bookmark get(String id) {
        JsonNode data = transport.execute(GET, vars("id", id));
        return toType(data.get("bookmark"), Bookmark.class);
    }

    /**
     * Create a bookmark for an event.
     *
     * @param eventId event UUID.
     * @param name    bookmark name.
     * @param notes   free-form notes (nullable).
     * @return the created bookmark.
     */
    public Bookmark create(String eventId, String name, String notes) {
        JsonNode data = transport.execute(CREATE, vars("eventId", eventId, "name", name, "notes", notes));
        return toType(data.get("createBookmark"), Bookmark.class);
    }

    /**
     * Delete a bookmark.
     *
     * @param id bookmark UUID.
     * @return {@code true} on success.
     */
    public boolean delete(String id) {
        JsonNode data = transport.execute(DELETE, vars("id", id));
        return data.path("deleteBookmark").asBoolean(false);
    }

    /**
     * Asynchronously list bookmarks.
     *
     * @param eventId optional event UUID filter.
     * @param search  optional substring search.
     * @param limit   page size (offset pagination).
     * @param offset  page offset (offset pagination).
     * @param after   cursor for cursor pagination.
     * @param first   page size for cursor pagination.
     * @return future completing with the paginated result.
     */
    public CompletableFuture<ListResult<Bookmark>> listAsync(String eventId, String search,
                                                             Integer limit, Integer offset, String after, Integer first) {
        return transport.executeAsync(LIST, vars(
                "eventId", eventId, "search", search,
                "limit", limit, "offset", offset, "after", after, "first", first))
                .thenApply(data -> parseList(data.get("bookmarks"), Bookmark.class));
    }

    /**
     * Asynchronously fetch a bookmark by id.
     *
     * @param id bookmark UUID.
     * @return future completing with the bookmark, or {@code null}.
     */
    public CompletableFuture<Bookmark> getAsync(String id) {
        return transport.executeAsync(GET, vars("id", id))
                .thenApply(data -> toType(data.get("bookmark"), Bookmark.class));
    }

    /**
     * Asynchronously create a bookmark for an event.
     *
     * @param eventId event UUID.
     * @param name    bookmark name.
     * @param notes   free-form notes (nullable).
     * @return future completing with the created bookmark.
     */
    public CompletableFuture<Bookmark> createAsync(String eventId, String name, String notes) {
        return transport.executeAsync(CREATE, vars("eventId", eventId, "name", name, "notes", notes))
                .thenApply(data -> toType(data.get("createBookmark"), Bookmark.class));
    }

    /**
     * Asynchronously delete a bookmark.
     *
     * @param id bookmark UUID.
     * @return future completing with {@code true} on success.
     */
    public CompletableFuture<Boolean> deleteAsync(String id) {
        return transport.executeAsync(DELETE, vars("id", id))
                .thenApply(data -> data.path("deleteBookmark").asBoolean(false));
    }
}
