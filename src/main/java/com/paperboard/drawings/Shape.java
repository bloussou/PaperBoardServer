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

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public Double getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(final Double lineWidth) {
        this.lineWidth = lineWidth;
    }

    public String getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(final String lineStyle) {
        this.lineStyle = lineStyle;
    }
}
