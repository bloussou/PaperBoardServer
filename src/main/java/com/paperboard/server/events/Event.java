package com.paperboard.server.events;

import com.paperboard.drawings.ModificationType;
import reactor.util.annotation.Nullable;

import javax.json.JsonObject;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Event object related to an EventType and checking that the payload contains at least the needed attributes.
 */
public class Event {
    private static final Logger LOGGER = Logger.getLogger(Event.class.getName());
    final public EventType type;
    private final StackTraceElement source;
    final public JsonObject payload;
    Date firedAt;

    /**
     * Constructor for Event assign and log if there is an error in the payload.
     *
     * @param type    a specified EventType
     * @param payload the JsonObject payload describing the event
     */
    public Event(final EventType type, @Nullable final JsonObject payload) throws IncorrectEventException {
        this.type    = type;
        this.source  = new Throwable().getStackTrace()[1];
        this.payload = payload;
        if (!this.checkPayload(payload)) {
            final IncorrectEventException e = new IncorrectEventException("Event [" +
                                                                          this.type +
                                                                          "] creation error: " +
                                                                          "Payload does not suit the event " +
                                                                          "requirements.");
            LOGGER.warning("ERROR : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Method to check the correctness of the payload for each EventType
     *
     * @param payload JsonObject
     * @return boolean
     */
    public boolean checkPayload(final JsonObject payload) {
        final boolean payloadIsCorrect;

        switch (this.type) {
            case DRAWER_CONNECTED:
                payloadIsCorrect = checkPayloadContains_String(payload, "sessionId");
                break;
            case DRAWER_DISCONNECTED:
                payloadIsCorrect = checkPayloadContains_String(payload, "pseudo") ||
                                   checkPayloadContains_String(payload, "sessionId");
                break;
            case DRAWER_IDENTIFICATION:
                payloadIsCorrect = checkPayloadContains_String(payload, "pseudo", "sessionId", "isAvailable");
                break;
            case ASK_IDENTITY:
                payloadIsCorrect = checkPayloadContains_String(payload, "pseudo", "sessionId");
                break;
            case ASK_JOIN_BOARD:
            case ASK_LEAVE_BOARD:
                payloadIsCorrect = checkPayloadContains_String(payload, "pseudo", "board");
                break;
            case DRAWER_JOINED_BOARD:
            case DRAWER_LEFT_BOARD:
                payloadIsCorrect = checkPayloadContains_String(payload, "pseudo", "board") &&
                                   payload.containsKey("userlist");
                break;
            case CHAT_MESSAGE:
                payloadIsCorrect = checkPayloadContains_String(payload, "pseudo", "board", "msg");
                break;
            case OBJECT_CREATED:
                // contains pseudo and drawing.encodeToJsonObjectBuilder() for each drawing
                payloadIsCorrect = checkPayloadContains_String(payload, "pseudo");
                break;
            case ASK_CREATE_OBJECT:
                payloadIsCorrect = checkPayloadContains_String(payload,
                                                               "pseudo",
                                                               "board",
                                                               "shape",
                                                               "positionX",
                                                               "positionY") && payload.containsKey("description");

                break;
            case ASK_LOCK_OBJECT:
            case ASK_UNLOCK_OBJECT:
            case OBJECT_LOCKED:
            case OBJECT_UNLOCKED:
            case ASK_DELETE_OBJECT:
            case OBJECT_DELETED:
                payloadIsCorrect = checkPayloadContains_String(payload, "pseudo", "board", "drawingId");
                break;
            case OBJECT_EDITED:
            case ASK_EDIT_OBJECT:
                payloadIsCorrect = checkPayloadContains_String(payload, "pseudo", "drawingId", "board") &&
                                   checkPayloadContains_ModificationType(payload);
                break;
            default:
                payloadIsCorrect = false;
        }
        return payloadIsCorrect;
    }

    /**
     * Method to check that a payload is containing a specific key and that this one is not an empty string.
     *
     * @param payload the payload you want to check
     * @param keys    keys requested in the payload
     * @return boolean
     */
    public static boolean checkPayloadContains_String(final JsonObject payload, final String... keys) {
        for (final String key : keys) {
            if (!payload.containsKey(key) || (payload.containsKey(key) && payload.getString(key).equals(""))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Iterate on payload keys to check that every key except "pseudo", "drawingId" and "board" are in
     * ModificationType enum.
     *
     * @param payload JsonObject
     * @return boolean
     */
    public static boolean checkPayloadContains_ModificationType(final JsonObject payload) {
        for (final String key : payload.keySet()) {
            if (!ModificationType.contains(key) &&
                !key.equals("pseudo") &&
                !key.equals("drawingId") &&
                !key.equals("board")) {
                return false;
            }
        }
        return true;
    }
}
