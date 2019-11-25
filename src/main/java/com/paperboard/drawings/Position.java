package com.paperboard.drawings;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

/**
 * Util class to store point coordinates
 */
public class Position {
    private Double x;
    private Double y;

    public Position(final Double x, final Double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the Json description of a Position object
     *
     * @return JsonObjectBuilder
     */
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("x", this.x);
        jsonBuilder.add("y", this.y);
        return jsonBuilder;
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
}
