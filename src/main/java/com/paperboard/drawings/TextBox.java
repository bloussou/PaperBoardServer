package com.paperboard.drawings;

import com.paperboard.server.User;

public class TextBox extends Drawing {
    private String text;
    private Double textSize;
    private String textColor;

    public TextBox(User user) {
        super(user);
    }

    @Override
    public void resize() {

    }
}
