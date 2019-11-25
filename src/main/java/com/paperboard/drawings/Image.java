package com.paperboard.drawings;

import com.paperboard.server.User;

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
