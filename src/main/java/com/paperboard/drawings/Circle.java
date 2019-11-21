package com.paperboard.drawings;

import com.paperboard.server.User;

public class Circle extends Shape {
    private Double radius = 10.0;
    private String backgroundColor;

    public Circle(final User user) {
        super(user);
    }
}
