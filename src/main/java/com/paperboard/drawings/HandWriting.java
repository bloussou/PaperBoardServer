package com.paperboard.drawings;

import com.paperboard.server.User;

public class HandWriting extends Shape {
    private java.util.ArrayList<Double> pathX;
    private java.util.ArrayList<Double> pathY;

    public HandWriting(final User user) {
        super(DrawingType.HANDWRITING.str, user);
    }
}
