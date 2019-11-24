package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.Json;
import javax.json.JsonObjectBuilder;

public class Circle extends Shape {
    private Double radius = 50.0;
    private String fillColor = "transparent";

    public Circle(final User user, final Position position) {
        super(DrawingType.CIRCLE.str, user, position);
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(final Double radius) {
        this.radius = radius;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(final String fillColor) {
        this.fillColor = fillColor;
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("id", this.getId());
        jsonBuilder.add("fillColor", this.getFillColor());
        jsonBuilder.add("lineColor", this.getLineColor());
        jsonBuilder.add("lineStyle", this.getLineStyle());
        jsonBuilder.add("lineWidth", this.getLineWidth());
        jsonBuilder.add("isLocked", this.isLocked());
        jsonBuilder.add("lockedBy", this.getLockedBy());
        jsonBuilder.add("radius", this.getRadius());
        jsonBuilder.add("type", this.getType());
        jsonBuilder.add("position", this.getPosition().encodeToJsonObjectBuilder());
        jsonBuilder.add("owner", this.getOwner().encodeToJsonObjectBuilder());
        return jsonBuilder;
    }
}
