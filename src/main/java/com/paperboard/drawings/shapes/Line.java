package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.ModificationType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Line class to describe jscanvas Line
 */
public class Line extends Shape {
    private Position positionEndPoint;

    public Line(final User user, final Position position) {
        super(DrawingType.LINE.str, user, position);
        this.positionEndPoint = new Position(position.getX() + 20.0, position.getY());
    }

    /**
     * See Base class
     *
     * @return JsonObjectBuilder
     */
    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("positionEndPoint", this.positionEndPoint.encodeToJsonObjectBuilder());
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
                case POSITION_END_POINT:
                    final JsonObject positionGet = payload.getJsonObject(key);
                    final Position position = new Position(Double.parseDouble(positionGet.getString("x")),
                                                           Double.parseDouble(positionGet.getString("y")));
                    this.setPositionEndPoint(position);
                    modifications.add(key, position.encodeToJsonObjectBuilder());
                    break;
            }
        }
        return modifications;
    }

    public void setPositionEndPoint(final Position positionEndPoint) {
        this.positionEndPoint = positionEndPoint;
    }

    public Position getPositionEndPoint() {
        return positionEndPoint;
    }
}
