package com.paperboard.drawings;

public enum ModificationType {
    LINE_WIDTH("lineWidth"),
    LINE_COLOR("lineColor"),
    RADIUS("radius"),
    X("X"),
    Y("Y"),
    LINE_STYLE("lineStyle"),
    FILL_COLOR("fillColor"),
    HEIGHT("height"),
    WIDTH("width"),
    TEXT("text"),
    TEXT_SIZE("textSize"),
    TEXT_COLOR("textColor"),
    POSITION_BOTTOM_LEFT("positionBottomLeft"),
    POSITION_BOTTOM_RIGHT("positionBottomRight"),
    POSITION_END_POINT("positionEndPoint"),
    PATH_X("pathX"),
    PATH_Y("pathY"),
    NULL("");


    public final String str;

    ModificationType(final String str) {
        this.str = str;
    }

    public static boolean contains(final String test) {
        for (final ModificationType modificationType : ModificationType.values()) {
            if (modificationType.str.equals(test)) {
                return true;
            }
        }
        return false;
    }

    public static ModificationType getEnum(final String str) {
        for (final ModificationType modificationType : ModificationType.values()) {
            if (modificationType.str.equals(str)) {
                return modificationType;
            }
        }
        return NULL;
    }
}
