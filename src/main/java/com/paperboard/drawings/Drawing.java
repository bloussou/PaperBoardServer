package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Drawing {
    private static AtomicLong idCounter = new AtomicLong(0);
    private final User owner;
    private boolean isLocked;
    private String lockedBy;
    private final String id;
    private final String type;
    private Position position;

    public Drawing(final String type, final User user, final Position position) {
        this.id       = "drawing" + String.valueOf(idCounter.getAndIncrement());
        this.owner    = user;
        this.type     = type;
        this.isLocked = false;
        this.lockedBy = "";
        this.position = position;
    }

    public void setPosition(final Position position) {
        this.position = position;
    }

    public boolean lockDrawing(final User user) {
        synchronized (this) {
            if (!this.isLocked) {
                this.isLocked = true;
                this.lockedBy = user.getPseudo();
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean unlockDrawing(final User user) {
        if (user.getPseudo().equals(this.lockedBy) && this.isLocked) {
            isLocked = false;
            lockedBy = "";
            return true;
        }
        return false;
    }

    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("drawingId", this.id)
                .add("isLocked", this.isLocked)
                .add("lockedBy", this.lockedBy)
                .add("type", this.type)
                .add("position", this.position.encodeToJsonObjectBuilder())
                .add("owner", this.owner.encodeToJsonObjectBuilder());
        return jsonBuilder;
    }

    public JsonObjectBuilder editDrawing(final JsonObject payload, final String board) {
        final JsonObjectBuilder modifications = Json.createObjectBuilder();
        modifications.add("pseudo", payload.getString("pseudo"))
                .add("drawingId", payload.getString("drawingId"))
                .add("board", board);
        if (payload.containsKey("X") && payload.containsKey("Y")) {
            final Double x = Double.parseDouble(payload.getString("X"));
            final Double y = Double.parseDouble(payload.getString("Y"));
            this.setPosition(new Position(x, y));
            modifications.add(ModificationType.X.str, x.toString()).add(ModificationType.Y.str, y.toString());
        }
        return modifications;
    }

    public User getOwner() {
        return owner;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getLockedBy() {
        return lockedBy;
    }
}
