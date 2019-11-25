package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public class Line extends Shape {
    private Position positionEndPoint;

    public Line(final User user, final Position position) {
        super(DrawingType.LINE.str, user, position);
        this.positionEndPoint = new Position(position.getX() + 20.0, position.getY());
    }

    public void setPositionEndPoint(final Position positionEndPoint) {
        this.positionEndPoint = positionEndPoint;
    }

    public Position getPositionEndPoint() {
        return positionEndPoint;
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("positionEndPoint", this.positionEndPoint.encodeToJsonObjectBuilder());
        return jsonBuilder;
    }
}
