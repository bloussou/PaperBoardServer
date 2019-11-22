package com.paperboard.drawings;

import com.paperboard.server.User;

public class Circle extends Shape {
    private Double radius = 50.0;
    private String fillColor;

    public Circle(final User user, final Position position) {
        super(DrawingType.CIRCLE.str, user, position);
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(final Double radius) {
        this.radius = radius;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(final String fillColor) {
        this.fillColor = fillColor;
    }
}
