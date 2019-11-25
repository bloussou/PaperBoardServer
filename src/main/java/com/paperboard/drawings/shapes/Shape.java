package com.paperboard.drawings.shapes;

import com.paperboard.drawings.Drawing;
import com.paperboard.drawings.ModificationType;
import com.paperboard.drawings.Position;
import com.paperboard.server.User;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * Abstract class extending Drawing for object with lines or border
 */
public abstract class Shape extends Drawing {
    private String lineColor = "red";
    private Double lineWidth = 10.0;
    private String lineStyle = "normal";

    public Shape(final String type, final User user, final Position position) {
        super(type, user, position);
    }

    /**
     * See base class
     *
     * @return JsonObjectBuilder
     */
    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("lineColor", this.lineColor).add("lineStyle", this.lineStyle).add("lineWidth", this.lineWidth);
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
                case LINE_WIDTH:
                    final Double lineWidth = Double.parseDouble(payload.getString(key));
                    this.setLineWidth(lineWidth);
                    modifications.add(key, lineWidth.toString());
                    break;
                case LINE_COLOR:
                    final String lineColor = payload.getString(key);
                    this.setLineColor(lineColor);
                    modifications.add(key, lineColor);
                    break;
                case LINE_STYLE:
                    final String lineStyle = payload.getString(key);
                    this.setLineStyle(lineStyle);
                    modifications.add(key, lineStyle);
                    break;
            }
        }
        return modifications;
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
