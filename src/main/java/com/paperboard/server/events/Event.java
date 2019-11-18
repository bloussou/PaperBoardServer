package com.paperboard.server.events;

import reactor.util.annotation.Nullable;

import javax.json.JsonObject;
import java.util.Date;

public class Event {

    public Date firedAt;
    public EventType type;
    public JsonObject payload;
    public StackTraceElement source;

    public Event(final EventType type, @Nullable final JsonObject payload) {
        this.type = type;
        this.source = new Throwable().getStackTrace()[1];
        if (!this.checkPayload(payload)) {
            throw new IncorrectEventException("Event [" + this.type + "] creation error: Payload does not suit the event requirements.");
        }
        this.payload = payload;
    }

    public boolean checkPayload(final JsonObject payload) {
        boolean payloadIsCorrect = true;

        switch (this.type) {
            case DRAWER_CONNECTED:
                payloadIsCorrect = this.checkPayload_DRAWER_CONNECTED(payload);
                break;
            case DRAWER_DISCONNECTED:
                payloadIsCorrect = this.checkPayload_DRAWER_DISCONNECTED(payload);
                break;
            case ASK_IDENTITY:
                payloadIsCorrect = this.checkPayload_ASK_IDENTITY(payload);
                break;
            case DRAWER_IDENTIFIED:
                payloadIsCorrect = this.checkPayload_DRAWER_IDENTIFIED(payload);
                break;
            case ASK_JOIN_BOARD:
                payloadIsCorrect = this.checkPayload_ASK_JOIN_BOARD(payload);
                break;
            case DRAWER_JOINED_BOARD:
                payloadIsCorrect = this.checkPayload_DRAWER_JOINED_BOARD(payload);
                break;
            case ASK_LEAVE_BOARD:
                payloadIsCorrect = this.checkPayload_ASK_LEAVE_BOARD(payload);
                break;
            case DRAWER_LEFT_BOARD:
                payloadIsCorrect = this.checkPayload_DRAWER_LEFT_BOARD(payload);
                break;
            default:
                payloadIsCorrect = false;
        }

        return payloadIsCorrect;
    }

    public boolean checkPayload_DRAWER_CONNECTED(final JsonObject payload) {
        if (payload.getString("sessionId").equals(null)) {
            return false;
        }
        return true;
    }

    public boolean checkPayload_DRAWER_DISCONNECTED(final JsonObject payload) {
        if (payload.getString("pseudo").equals(null)) {
            return false;
        } else if (payload.getString("sessionId").equals(null)) {
            return false;
        } else if (payload.getString("board").equals(null)) {
            return false;
        }
        return true;
    }

    public boolean checkPayload_ASK_IDENTITY(final JsonObject payload) {
        if (payload.getString("pseudo").equals(null)) {
            return false;
        }
        return true;
    }

    public boolean checkPayload_DRAWER_IDENTIFIED(final JsonObject payload) {
        if (payload.getString("pseudo").equals(null)) {
            return false;
        }
        return true;
    }

    public boolean checkPayload_ASK_JOIN_BOARD(final JsonObject payload) {
        if (payload.getString("pseudo").equals(null)) {
            return false;
        } else if (payload.getString("board").equals(null)) {
            return false;
        }
        return true;
    }

    public boolean checkPayload_DRAWER_JOINED_BOARD(final JsonObject payload) {
        if (payload.getString("pseudo").equals(null)) {
            return false;
        } else if (payload.getString("board").equals(null)) {
            return false;
        } else if (payload.getString("userlist").equals(null)) {
            return false;
        }
        return true;
    }

    public boolean checkPayload_ASK_LEAVE_BOARD(final JsonObject payload) {
        if (payload.getString("pseudo").equals(null)) {
            return false;
        } else if (payload.getString("board").equals(null)) {
            return false;
        }
        return true;
    }

    public boolean checkPayload_DRAWER_LEFT_BOARD(final JsonObject payload) {
        if (payload.getString("pseudo").equals(null)) {
            return false;
        } else if (payload.getString("board").equals(null)) {
            return false;
        } else if (payload.getString("userlist").equals(null)) {
            return false;
        }
        return true;
    }


}
