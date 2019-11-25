package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.ModificationType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Triangle class to describe jscanvas triangle
 */
public class Triangle extends Shape {
    private Position positionBottomLeft;
    private Position positionBottomRight;
    private String fillColor = "transparent";

    public Triangle(final User user, final Position position) {
        super(DrawingType.TRIANGLE.str, user, position);
        this.positionBottomLeft  = new Position(position.getX() - 20.0, position.getY() - 20.0);
        this.positionBottomRight = new Position(position.getX() + 20.0, position.getY() - 20.0);
    }

    /**
     * See Base class
     *
     * @return JsonObjectBuilder
     */
    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("positionBottomLeft", this.positionBottomLeft.encodeToJsonObjectBuilder())
                .add("positionBottomRight", this.positionBottomRight.encodeToJsonObjectBuilder())
                .add("fillColor", this.fillColor);
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
                case POSITION_BOTTOM_LEFT:
                    final JsonObject positionGetRight = payload.getJsonObject(key);
                    final Position positionRight = new Position(Double.parseDouble(positionGetRight.getString("x")),
                                                                Double.parseDouble(positionGetRight.getString("y")));
                    this.setPositionBottomRight(positionRight);
                    modifications.add(key, positionRight.encodeToJsonObjectBuilder());
                    break;
                case POSITION_BOTTOM_RIGHT:
                    final JsonObject positionGetLeft = payload.getJsonObject(key);
                    final Position positionLeft = new Position(Double.parseDouble(positionGetLeft.getString("x")),
                                                               Double.parseDouble(positionGetLeft.getString("y")));
                    this.setPositionBottomLeft(positionLeft);
                    modifications.add(key, positionLeft.encodeToJsonObjectBuilder());
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

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(final String fillColor) {
        this.fillColor = fillColor;
    }
}
