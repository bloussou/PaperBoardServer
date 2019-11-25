package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Image object
 * srcURI : stringified image
 */
public class Image extends Drawing {
    private Double height;
    private Double width;
    final private String srcURI;

    public Image(final User user,
                 final Position position,
                 final Double height,
                 final Double width,
                 final String srcURI) {
        super(DrawingType.IMAGE.str, user, position);
        this.height = height;
        this.width  = width;
        this.srcURI = srcURI;
    }

    /**
     * See base class
     *
     * @return JsonObjectBuilder
     */
    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("height", this.height.toString())
                .add("width", this.width.toString())
                .add("srcURI", this.srcURI);
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
                case HEIGHT:
                    final Double height = Double.parseDouble(payload.getString(key));
                    this.setHeight(height);
                    modifications.add(key, height.toString());
                    break;
                case WIDTH:
                    final double width = Double.parseDouble(payload.getString(key));
                    this.setWidth(width);
                    modifications.add(key, width);
                    break;
            }
        }
        return modifications;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(final Double height) {
        this.height = height;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(final Double width) {
        this.width = width;
    }

    public String getSrcURI() {
        return srcURI;
    }
}
