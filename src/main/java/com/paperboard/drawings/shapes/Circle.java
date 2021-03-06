package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.ModificationType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


/**
 * HandWriting class for jscanvas HandWriting
 */
public class Circle extends Shape {
    private Double radius = 50.0;
    private String fillColor = "transparent";

    public Circle(final User user, final Position position) {
        super(DrawingType.CIRCLE.str, user, position);
    }

    /**
     * See Base class
     *
     * @return JsonObjectBuilder
     */
    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("fillColor", this.fillColor).add("radius", this.radius);
        return jsonBuilder;
    }

    /**
     * See base class
     *
     * @param payload the modification payload to apply
     * @param board   String title of the board
     * @return JsonObjectBuilder
     */
    @Override
    public JsonObjectBuilder editDrawing(final JsonObject payload, final String board) {
        final JsonObjectBuilder modifications = super.editDrawing(payload, board);
        for (final String key : payload.keySet()) {
            switch (ModificationType.getEnum(key)) {
                case RADIUS:
                    final Double radius = Double.parseDouble(payload.getString(key));
                    this.setRadius(radius);
                    modifications.add(key, radius.toString());
                    break;
                case FILL_COLOR:
                    final String fillColor = payload.getString(key);
                    this.setFillColor(fillColor);
                    modifications.add(key, fillColor);
                    break;
            }
        }
        return modifications;
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
}
