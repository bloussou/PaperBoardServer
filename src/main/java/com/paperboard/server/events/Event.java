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
            final IncorrectEventException e = new IncorrectEventException("Event [" + this.type + "] creation error: Payload does not suit the event requirements.");
            LOGGER.warning("ERROR : " + e.getMessage());
        }
        this.payload = payload;
    }

    public boolean checkPayload(final JsonObject payload) {
        boolean payloadIsCorrect = true;

        switch (this.type) {
            case DRAWER_CONNECTED:
                payloadIsCorrect = this.checkPayloadContains_SessionId(payload);
                break;
            case DRAWER_DISCONNECTED:
                payloadIsCorrect = this.checkPayloadContains_Pseudo(payload) || this.checkPayloadContains_SessionId(payload);
                break;
            case ASK_IDENTITY:
            case DRAWER_IDENTIFIED:
                payloadIsCorrect = this.checkPayloadContains_Pseudo(payload) && this.checkPayloadContains_SessionId(payload);
                break;
            case ASK_JOIN_BOARD:
            case ASK_LEAVE_BOARD:
                payloadIsCorrect = this.checkPayloadContains_Pseudo(payload) && this.checkPayloadContains_Board(payload);
                break;
            case DRAWER_JOINED_BOARD:
            case DRAWER_LEFT_BOARD:
                payloadIsCorrect = this.checkPayloadContains_Pseudo(payload) && this.checkPayloadContains_Board(payload) && this.checkPayloadContains_Userlist(payload);
                break;
            case CHAT_MESSAGE:
                payloadIsCorrect = this.checkPayloadContains_Pseudo(payload) && this.checkPayloadContains_Board(payload) && this.checkPayloadContains_Msg(payload);
                break;
            default:
                payloadIsCorrect = false;
        }

        return payloadIsCorrect;
    }

    private boolean checkPayloadContains_SessionId(final JsonObject payload) {
        if (!payload.containsKey("sessionId") || (payload.containsKey("sessionId") && payload.getString("sessionId").equals(""))) {
            return false;
        }
        return true;
    }

    private boolean checkPayloadContains_Pseudo(final JsonObject payload) {
        if (!payload.containsKey("pseudo") || (payload.containsKey("pseudo") && payload.getString("pseudo").equals(""))) {
            return false;
        }
        return true;
    }

    private boolean checkPayloadContains_Board(final JsonObject payload) {
        if (!payload.containsKey("board") || (payload.containsKey("board") && payload.getString("board").equals(""))) {
            return false;
        }
        return true;
    }

    private boolean checkPayloadContains_Msg(final JsonObject payload) {
        if (!payload.containsKey("msg") || (payload.containsKey("msg") && payload.getString("msg").equals(""))) {
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
