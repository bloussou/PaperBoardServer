package com.paperboard.drawings;

/**
 * Enum of the different types of drawings
 */
public enum DrawingType {
    CIRCLE("circle"),
    HANDWRITING("handwriting"),
    RECTANGLE("rectangle"),
    TEXT_BOX("text"),
    IMAGE("image"),
    TRIANGLE("triangle"),
    LINE("line"),
    NULL("");

    public final String str;

    DrawingType(final String str) {
        this.str = str;
    }

    public static DrawingType getEnum(final String str) {
        for (final DrawingType drawingType : DrawingType.values()) {
            if (drawingType.str.equals(str)) {
                return drawingType;
            }
        }
        return NULL;
    }
}
