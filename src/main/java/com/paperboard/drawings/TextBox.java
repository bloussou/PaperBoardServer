package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.JsonObject;
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

    @Override
    public JsonObjectBuilder editDrawing(final JsonObject payload, final String board) {
        final JsonObjectBuilder modifications = super.editDrawing(payload, board);
        for (final String key : payload.keySet()) {
            switch (ModificationType.getEnum(key)) {
                case TEXT_SIZE:
                    final Double size = Double.parseDouble(payload.getString(key));
                    this.setTextSize(size);
                    modifications.add(key, size.toString());
                    break;
                case TEXT_COLOR:
                    final String textColor = payload.getString(key);
                    this.setTextColor(textColor);
                    modifications.add(key, textColor);
                    break;
                case TEXT:
                    final String text = payload.getString(key);
                    this.setText(text);
                    modifications.add(key, text);
                    break;
            }
        }
        return modifications;
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
