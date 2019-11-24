package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public class Shape extends Drawing {
    private String lineColor = "red";
    private Double lineWidth = 10.0;
    private String lineStyle = "normal";

    public Shape(final String type, final User user, final Position position) {
        super(type, user, position);
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        return null;
    }

    public Shape(final String type, final User user) {
        super(type, user);
    }

    @Override
    public void resize() {
    }

    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(final String lineColor) {
        this.lineColor = lineColor;
    }

    public Double getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(final Double lineWidth) {
        this.lineWidth = lineWidth;
    }

    public String getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(final String lineStyle) {
        this.lineStyle = lineStyle;
    }
}
