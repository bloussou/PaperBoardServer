package com.paperboard.drawings;

public enum DrawingType {
    CIRCLE("circle"), HANDWRITING("hand writing"), RECTANGLE("rectangle"), TEXT_BOX("text"), IMAGE("image");

    public final String str;

    DrawingType(final String str) {
        this.str = str;
    }
}
