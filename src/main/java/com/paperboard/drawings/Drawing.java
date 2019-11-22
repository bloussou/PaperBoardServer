package com.paperboard.drawings;

import com.paperboard.server.User;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Drawing implements IDrawing {
    private static AtomicLong idCounter = new AtomicLong(0);
    private final User owner;
    private final String id;
    private final String type;
    private Position position;

    public Drawing(final String type, final User user) {
        this.id = "drawing" + String.valueOf(idCounter.getAndIncrement());
        this.owner = user;
        this.type = type;
    }

    public Drawing(final String type, final User user, final Position position) {
        this.id = "drawing" + String.valueOf(idCounter.getAndIncrement());
        this.owner = user;
        this.position = position;
        this.type = type;
    }

    @Override
    public void move() {
    }

    @Override
    public void delete() {
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

    public void setPosition(final Position position) {
        this.position = position;
    }
}
