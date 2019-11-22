package com.paperboard.server.events;

import reactor.util.annotation.Nullable;

import javax.json.JsonObject;
import java.util.Date;
import java.util.logging.Logger;

public class Event {

    private static final Logger LOGGER = Logger.getLogger(Event.class.getName());
    public Date firedAt;
    public EventType type;
    public JsonObject payload;
    public StackTraceElement source;

    public Event(final EventType type, @Nullable final JsonObject payload) {
        this.type = type;
        this.source = new Throwable().getStackTrace()[1];
        if (!this.checkPayload(payload)) {
            final IncorrectEventException e = new IncorrectEventException("Event [" + this.type + "] creation error: "
                    + "Payload does not suit the event requirements.");
            LOGGER.warning("ERROR : " + e.getMessage());
        }
        this.payload = payload;
    }

    public boolean checkPayload(final JsonObject payload) {
        boolean payloadIsCorrect = true;

        switch (this.type) {
            case DRAWER_CONNECTED:
                payloadIsCorrect = this.checkPayloadContains_String(payload, "sessionId");
                break;
            case DRAWER_DISCONNECTED:
                payloadIsCorrect = this.checkPayloadContains_String(payload,
                        "pseudo") || this.checkPayloadContains_String(payload, "sessionId");
                break;
            case ASK_IDENTITY:
            case DRAWER_IDENTIFIED:
                payloadIsCorrect = this.checkPayloadContains_String(payload,
                        "pseudo") && this.checkPayloadContains_String(payload, "sessionId");
                break;
            case ASK_JOIN_BOARD:
            case ASK_LEAVE_BOARD:
                payloadIsCorrect = this.checkPayloadContains_String(payload,
                        "pseudo") && this.checkPayloadContains_String(payload, "board");
                break;
            case DRAWER_JOINED_BOARD:
            case DRAWER_LEFT_BOARD:
                payloadIsCorrect = this.checkPayloadContains_String(payload,
                        "pseudo") && this.checkPayloadContains_String(payload,
                        "board") && this.checkPayloadContains_Userlist(payload);
                break;
            case CHAT_MESSAGE:
                payloadIsCorrect = this.checkPayloadContains_String(payload,
                        "pseudo") && this.checkPayloadContains_String(payload,
                        "board") && this.checkPayloadContains_String(payload, "msg");
                break;
            case OBJECT_CREATED:
                payloadIsCorrect = this.checkPayloadContains_String(payload,
                        "pseudo") && this.checkPayloadContains_String(payload,
                        "shape") && this.checkPayloadContains_String(payload, "id") && this.checkPayloadContains_String(
                        payload,
                        "X") && this.checkPayloadContains_String(payload, "Y") && this.checkPayloadContains_String(
                        payload,
                        "lineWidth") && this.checkPayloadContains_String(payload, "lineColor");
                payloadIsCorrect = payloadIsCorrect && (payload.getString("shape")
                        .equals("circle") && this.checkPayloadContains_String(payload, "radius"));
                break;
            case ASK_CREATE_OBJECT:
                payloadIsCorrect = this.checkPayloadContains_String(payload,
                        "pseudo") && this.checkPayloadContains_String(payload,
                        "board") && this.checkPayloadContains_String(payload,
                        "shape") && this.checkPayloadContains_String(payload,
                        "positionX") && this.checkPayloadContains_String(payload, "positionY");
                break;
            case ASK_LOCK_OBJECT:
            case ASK_UNLOCK_OBJECT:
            case OBJECT_LOCKED:
            case OBJECT_UNLOCKED:
            case OBJECT_EDITED:
                payloadIsCorrect = this.checkPayloadContains_String(payload,
                        "pseudo") && this.checkPayloadContains_String(payload,
                        "board") && this.checkPayloadContains_String(payload, "drawingId");
                break;
            case ASK_EDIT_OBJECT:
                payloadIsCorrect = this.checkPayloadContains_String(payload,
                        "pseudo") && this.checkPayloadContains_String(payload, "drawingId");
                // values available are X, Y, lineWidth, lineColor, radius
                break;
            default:
                payloadIsCorrect = false;
        }

        return payloadIsCorrect;
    }

    private boolean checkPayloadContains_String(final JsonObject payload, final String key) {
        if (!payload.containsKey(key) || (payload.containsKey(key) && payload.getString(key).equals(""))) {
            return false;
        }
        return true;
    }

    private boolean checkPayloadContains_Userlist(final JsonObject payload) {
        if (!payload.containsKey("userlist")) {
            return false;
        }
        return true;
    }
}
