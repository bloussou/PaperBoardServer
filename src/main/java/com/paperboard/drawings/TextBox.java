package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public class TextBox extends Drawing {
    private String text;
    private Double textSize = 10.0;
    private String textColor = "black";

    public TextBox(final User user, final Position position, final String text) {
        super(DrawingType.TEXT_BOX.str, user, position);
        this.text = text;
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder jsonBuilder = super.encodeToJsonObjectBuilder();
        jsonBuilder.add("text", this.text).add("textSize", this.textSize.toString()).add("textColor", this.textColor);
        return jsonBuilder;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public Double getTextSize() {
        return textSize;
    }

    public void setTextSize(final Double textSize) {
        this.textSize = textSize;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(final String textColor) {
        this.textColor = textColor;
    }
}
