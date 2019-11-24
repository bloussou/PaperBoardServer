package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public class Image extends Drawing {
    public Image(final User user) {
        super(DrawingType.TEXT_BOX.str, user);
    }

    @Override
    public void resize() {

    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        return null;
    }
}
