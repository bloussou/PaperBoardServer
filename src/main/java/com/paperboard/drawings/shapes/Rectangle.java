package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public class Rectangle extends Shape {
    private Double width = 50.0;
    private String fillColor = "transparent";
    private Double height = 40.0;

    public Rectangle(final User user, final Position position) {
        super(DrawingType.RECTANGLE.str, user, position);
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("positionBottomLeft", this.width.toString())
                .add("positionBottomRight", this.height.toString())
                .add("backgroundColor", this.fillColor);
        return jsonBuilder;
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
