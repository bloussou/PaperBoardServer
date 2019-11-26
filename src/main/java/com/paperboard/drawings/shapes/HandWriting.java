package com.paperboard.drawings.shapes;

import com.paperboard.drawings.DrawingType;
import com.paperboard.drawings.ModificationType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;

/**
 * HandWriting class for jscanvas HandWriting
 * pathX and pathY have same length and describe point by point the handWriting shape
 */
public class HandWriting extends Shape {
    private ArrayList<Double> pathX = new ArrayList<>();
    private ArrayList<Double> pathY = new ArrayList<>();

    public HandWriting(final User user,
                       final Position position) {
        super(DrawingType.HANDWRITING.str, user, position);
    }

    /**
     * Util class to build a JsonArrayBuilder from an ArrayList<Double>
     *
     * @param path ArrayList<Double>
     * @return JsonArrayBuilder
     */
    private JsonArrayBuilder ArrayListToAJsonArrayBuilder(final ArrayList<Double> path) {
        final JsonArrayBuilder pathJson = Json.createBuilderFactory(null).createArrayBuilder();
        for (final Double u : path) {
            pathJson.add(u);
        }
        return pathJson;
    }

    /**
     * See Base class
     *
     * @return JsonObjectBuilder
     */
    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("pathX", ArrayListToAJsonArrayBuilder(this.pathX))
                .add("pathY", ArrayListToAJsonArrayBuilder(this.pathY));
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
                case PATH_X:
                    final ArrayList<Double> pathX = new ArrayList<>();
                    for (final Object x : payload.getJsonArray(key).toArray()) {
                        pathX.add(Double.parseDouble(x.toString()));
                    }
                    this.setPathX(pathX);
                    modifications.add(key, ArrayListToAJsonArrayBuilder(pathX));
                    break;
                case PATH_Y:
                    final ArrayList<Double> pathY = new ArrayList<>();
                    for (final Object y : payload.getJsonArray(key).toArray()) {
                        pathY.add(Double.parseDouble(y.toString()));
                    }
                    this.setPathY(pathY);
                    modifications.add(key, ArrayListToAJsonArrayBuilder(pathY));
                    break;
            }
        }
        return modifications;
    }

    public ArrayList<Double> getPathX() {
        return pathX;
    }

    public void setPathX(final ArrayList<Double> pathX) {
        this.pathX = pathX;
    }

    public ArrayList<Double> getPathY() {
        return pathY;
    }

    public void setPathY(final ArrayList<Double> pathY) {
        this.pathY = pathY;
    }
}
