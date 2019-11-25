package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class Image extends Drawing {
    private Double height;
    private Double width;
    final private String url;

    public Image(final User user, final Position position, final Double height, final Double width, final String url) {
        super(DrawingType.IMAGE.str, user, position);
        this.height = height;
        this.width  = width;
        this.url    = url;
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("height", this.height.toString()).add("width", this.width.toString()).add("url", this.url);
        return jsonBuilder;
    }

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
                    final Double width = Double.parseDouble(payload.getString(key));
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

    public String getUrl() {
        return url;
    }
}
