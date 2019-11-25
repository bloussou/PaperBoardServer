package com.paperboard.drawings.shapes;

import com.paperboard.drawings.Drawing;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public abstract class Shape extends Drawing {
    private String lineColor = "red";
    private Double lineWidth = 10.0;
    private String lineStyle = "normal";

    public Shape(final String type, final User user, final Position position) {
        super(type, user, position);
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("lineColor", this.lineColor).add("lineStyle", this.lineStyle).add("lineWidth", this.lineWidth);
        return jsonBuilder;
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
