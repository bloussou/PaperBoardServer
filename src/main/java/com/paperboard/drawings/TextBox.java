package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public class TextBox extends Drawing {
    private String text;
    private Double textSize;
    private String textColor;

    public TextBox(final User user, final Position position) {
        super(DrawingType.TEXT_BOX.str, user, position);
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        return null;
    }
}
