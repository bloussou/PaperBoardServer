package com.paperboard.drawings;

import com.paperboard.server.User;

public class Rectangle extends Shape {
    private Double width;
    private String backgroundColor;
    private Double height;

    public Rectangle(final User user) {
        super(DrawingType.RECTANGLE.str, user);
    }
}
