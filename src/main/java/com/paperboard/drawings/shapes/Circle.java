package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.ModificationType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObject;
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

    @Override
    public JsonObjectBuilder editDrawing(final JsonObject payload, final String board) {
        final JsonObjectBuilder modifications = super.editDrawing(payload, board);
        for (final String key : payload.keySet()) {
            switch (ModificationType.getEnum(key)) {
                case RADIUS:
                    final Double radius = Double.parseDouble(payload.getString(ModificationType.RADIUS.str));
                    this.setRadius(radius);
                    modifications.add(ModificationType.RADIUS.str, radius.toString());
                    break;
                case FILL_COLOR:
                    final String fillColor = payload.getString(ModificationType.FILL_COLOR.str);
                    this.setFillColor(fillColor);
                    modifications.add(ModificationType.FILL_COLOR.str, fillColor);
                    break;
            }
        }
        return modifications;
    }
}
