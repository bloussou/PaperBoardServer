package com.paperboard.server.events;

import com.paperboard.server.socket.Message;

import javax.json.JsonObject;
import java.util.Date;

public class Event {

    public Date firedAt;
    public EventType type;
    public Message message;
    public JsonObject payload;
    public StackTraceElement source;

    public Event(final EventType type) {
        this.type = type;
        this.source = new Throwable().getStackTrace()[1];
    }

    public Event(final EventType type, final Message message) {
        this.type = type;
        this.source = new Throwable().getStackTrace()[1];
        this.message = message;
    }

    public Event(final EventType type, final JsonObject payload) {
        this.type = type;
        this.source = new Throwable().getStackTrace()[1];
        this.payload = payload;
    }
}
