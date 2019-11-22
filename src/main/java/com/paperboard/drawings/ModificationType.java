package com.paperboard.drawings;

public enum ModificationType {
    LINE_WIDTH("lineWidth"), LINE_COLOR("lineColor"), RADIUS("radius");

    public final String str;

    ModificationType(final String str) {
        this.str = str;
    }

    public static ModificationType getEnum(final String str) {
        for (final ModificationType modificationType : ModificationType.values()) {
            if (modificationType.str.equals(str)) {
                return modificationType;
            }
        }
        return null;
    }
}
