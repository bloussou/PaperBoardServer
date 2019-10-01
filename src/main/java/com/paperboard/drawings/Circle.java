package com.paperboard.drawings;

import com.paperboard.server.User;

public class Circle extends Shape {
    private Double radius;
    private String backgroundColor;

    public Circle(User user) {
        super(user);
    }
}
