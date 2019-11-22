package com.paperboard.drawings;

import com.paperboard.server.User;

public class Circle extends Shape {
    private Double radius = 10.0;
    private String backgroundColor;

    public Circle(final User user) {
        super(user);
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(final Double radius) {
        this.radius = radius;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(final String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
