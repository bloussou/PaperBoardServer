package com.paperboard.drawings;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DrawingTypeTest {

    @Test
    public void testGetEnum() {
        assertEquals(DrawingType.getEnum("circle"), DrawingType.CIRCLE);
        assertEquals(DrawingType.getEnum("joe"), DrawingType.NULL);
    }
}
