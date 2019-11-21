package com.paperboard.drawings;

import com.paperboard.server.User;

public class Shape extends Drawing {

    private String color = "#000000";
    private Double lineWidth = 1.0;
    private String lineStyle = "normal";

    public Shape(final User user) {
        super(user);
    }

    @Override
    public void resize() {

    }
}
