package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public class Triangle extends Shape {
    private Position positionBottomLeft;
    private Position positionBottomRight;
    private String backgroundColor = "transparent";

    public Triangle(final User user, final Position position) {
        super(DrawingType.TRIANGLE.str, user, position);
        this.positionBottomLeft  = new Position(position.getX() - 20.0, position.getY() - 20.0);
        this.positionBottomRight = new Position(position.getX() + 20.0, position.getY() - 20.0);
    }

    public Position getPositionBottomLeft() {
        return positionBottomLeft;
    }

    public void setPositionBottomLeft(final Position positionBottomLeft) {
        this.positionBottomLeft = positionBottomLeft;
    }

    public Position getPositionBottomRight() {
        return positionBottomRight;
    }

    public void setPositionBottomRight(final Position positionBottomRight) {
        this.positionBottomRight = positionBottomRight;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(final String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("positionBottomLeft", this.positionBottomLeft.encodeToJsonObjectBuilder())
                .add("positionBottomRight", this.positionBottomRight.encodeToJsonObjectBuilder())
                .add("backgroundColor", this.backgroundColor);
        return jsonBuilder;
    }
}
