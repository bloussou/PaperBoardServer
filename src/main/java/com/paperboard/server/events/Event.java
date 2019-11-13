package com.paperboard.server.events;

import com.paperboard.server.socket.Message;

import java.util.Date;

public class Event {

    public Date firedAt;
    public EventType type;
    public Message message;

    public Event(final EventType type) {
        this.type = type;
    }

    public Event(final EventType type, final Message message) {
        this.type = type;
        this.message = message;
    }
}
