package com.paperboard.drawings;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class Position {
    private Double x;
    private Double y;

    public Position(final Double x, final Double y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(final Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(final Double y) {
        this.y = y;
    }

    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("x", this.x);
        jsonBuilder.add("y", this.y);
        return jsonBuilder;
    }
}
