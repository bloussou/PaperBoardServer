package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

public class HandWriting extends Shape {
    private java.util.ArrayList<Double> pathX;
    private java.util.ArrayList<Double> pathY;

    public HandWriting(final User user,
                       final Position position,
                       final java.util.ArrayList<Double> pathX,
                       final java.util.ArrayList<Double> pathY) {
        super(DrawingType.HANDWRITING.str, user, position);
        this.pathX = pathX;
        this.pathY = pathY;
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();

        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArrayBuilder pathXJson = factory.createArrayBuilder();
        for (final Double u : this.pathX) {
            pathXJson.add(u);
        }
        final JsonArrayBuilder pathYJson = factory.createArrayBuilder();
        for (final Double u : this.pathY) {
            pathYJson.add(u);
        }
        jsonBuilder.add("pathX", pathXJson).add("pathY", pathYJson);
        return jsonBuilder;
    }
}
