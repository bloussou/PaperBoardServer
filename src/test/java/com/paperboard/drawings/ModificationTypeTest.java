package com.paperboard.drawings;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModificationTypeTest {

    @Test
    public void testGetEnum() {
        assertEquals(ModificationType.getEnum("lineWidth"), ModificationType.LINE_WIDTH);
        assertEquals(ModificationType.getEnum("joe"), ModificationType.NULL);
    }

    @Test
    public void testContains() {
        assertTrue(ModificationType.contains("lineWidth"));
        assertFalse(ModificationType.contains("joe"));
    }
}
