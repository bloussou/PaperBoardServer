package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.ModificationType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Rectangle class to describe jscanvas Rectangle
 */
public class Rectangle extends Shape {
    private Double width = 50.0;
    private String fillColor = "transparent";
    private Double height = 40.0;

    public Rectangle(final User user, final Position position) {
        super(DrawingType.RECTANGLE.str, user, position);
    }

    /**
     * See Base class
     *
     * @return JsonObjectBuilder
     */
    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("positionBottomLeft", this.width.toString())
                .add("positionBottomRight", this.height.toString())
                .add("backgroundColor", this.fillColor);
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
                case WIDTH:
                    final Double width = Double.parseDouble(payload.getString(key));
                    this.setWidth(width);
                    modifications.add(key, width.toString());
                    break;
                case HEIGHT:
                    final Double height = Double.parseDouble(payload.getString(key));
                    this.setHeight(height);
                    modifications.add(key, height.toString());
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

    public Double getWidth() {
        return width;
    }

    public void setWidth(final Double width) {
        this.width = width;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(final String fillColor) {
        this.fillColor = fillColor;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(final Double height) {
        this.height = height;
    }
}
