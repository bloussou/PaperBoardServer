package com.paperboard.drawings;

import com.paperboard.server.User;

public class TextBox extends Drawing {
    private String text;
    private Double textSize;
    private String textColor;

    public TextBox(final User user) {
        super(DrawingType.TEXT_BOX.str, user);
    }

    @Override
    public void resize() {

    }
}
