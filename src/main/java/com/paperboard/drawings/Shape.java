package com.paperboard.drawings;

import com.paperboard.server.User;

public class Shape extends Drawing {

    private String color;
    private Double lineWidth;
    private String lineStyle;

    public Shape(User user) {
        super(user);
    }

    @Override
    public void resize() {

    }
}
