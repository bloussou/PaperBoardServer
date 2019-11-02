package com.paperboard.server.socket;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import java.io.StringWriter;
import java.util.Date;

public class Message {
    private String type;
    private String from;
    private String to;
    private Date date;
    private JsonObject payload;

    public Message(final String type, final String from, final String to, final JsonObject payload) throws IncorrectMessageException {

        if (!MessageType.contains(type)) {
            throw new IncorrectMessageException("Message type [" + type + "] is not allowed.");
        }
        if (from == null || from == "") {
            throw new IncorrectMessageException("You must give the sender name");
        }
        if (to == null || to == "") {
            throw new IncorrectMessageException("You must give the receiver name (server='server' and broadcast='broadcast'");
        }

        this.type = type;
        this.from = from;
        this.to = to;
        this.date = new Date();
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(final String to) {
        this.to = to;
    }

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(final JsonObject payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonObject json = factory.createObjectBuilder()
                .add("type", this.type)
                .add("from", this.from)
                .add("to", this.to)
                .add("payload", this.payload)
                .build();

        final StringWriter writer = new StringWriter();
        Json.createWriter(writer).write(json);
        return writer.toString();
    }
}
