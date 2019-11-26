package com.paperboard.drawings;

import com.paperboard.drawings.shapes.Circle;
import com.paperboard.server.User;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

import static org.junit.Assert.*;

public class DrawingTest {

    @Test
    public void testLock() {
        final User user1 = new User("user1");
        final User user2 = new User("user2");
        final Circle circle = new Circle(user1, new Position(0.0, 0.0));
        circle.lockDrawing(user1);
        circle.lockDrawing(user2);
        assertTrue(circle.isLocked());
        assertEquals(circle.getLockedBy(), user1.getPseudo());
    }

    @Test
    public void testUnLock() {
        final User user1 = new User("user1");
        final Circle circle = new Circle(user1, new Position(0.0, 0.0));
        circle.lockDrawing(user1);
        circle.unlockDrawing(user1);
        assertTrue(!circle.isLocked());
        assertEquals(circle.getLockedBy(), "");
    }

    @Test
    public void TestEditDrawing() {
        final User user1 = new User("user1");
        final Circle circle = new Circle(user1, new Position(0.0, 0.0));
        final JsonObject modification = Json.createObjectBuilder()
                .add(ModificationType.RADIUS.str, "12.0")
                .add("pseudo", user1.getPseudo())
                .add("drawingId", circle.getId())
                .build();
        final JsonObject answer = circle.editDrawing(modification, "board").build();
        assertTrue(answer.containsKey("pseudo"));
        assertTrue(answer.containsKey("drawingId"));
        assertTrue(answer.containsKey("board"));
        assertTrue(answer.containsKey(ModificationType.RADIUS.str));
        assertFalse(answer.containsKey(ModificationType.LINE_WIDTH.str));
    }
}
