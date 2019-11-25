package com.paperboard.drawings;

import com.paperboard.server.User;

import javax.json.JsonObjectBuilder;

public class Image extends Drawing {
    public Image(final User user, final Position position) {
        super(DrawingType.IMAGE.str, user, position);
    }

    @Override
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        return null;
    }
}
