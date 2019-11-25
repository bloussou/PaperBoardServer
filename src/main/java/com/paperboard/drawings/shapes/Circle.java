package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

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
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("fillColor", this.fillColor).add("radius", this.radius);
        return jsonBuilder;
    }
}
