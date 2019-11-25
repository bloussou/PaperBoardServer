package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public class Rectangle extends Shape {
    private Double width = 50.0;
    private String backgroundColor = "transparent";
    private Double height = 40.0;

    public Rectangle(final User user, final Position position) {
        super(DrawingType.RECTANGLE.str, user, position);
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("positionBottomLeft", this.width.toString())
                .add("positionBottomRight", this.height.toString())
                .add("backgroundColor", this.backgroundColor);
        return jsonBuilder;
    }
}
