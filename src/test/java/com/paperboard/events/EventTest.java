package com.paperboard.events;

import com.paperboard.drawings.ModificationType;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventType;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventTest {

    /**
     * Test static method checkPayloadContains_String of Event
     */
    @Test
    public void testCheckPayloadContains_String() {
        final JsonObject payload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("test", "test")
                .add("test2", "test")
                .build();
        assertTrue(Event.checkPayloadContains_String(payload, "test", "test2"));
        assertFalse(Event.checkPayloadContains_String(payload, "bob"));
    }

    /**
     * Test static method checkPayloadContains_ModificationType of Event
     */
    @Test
    public void testCheckPayloadContains_ModificationType() {
        final JsonObject payload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add(ModificationType.RADIUS.str, "test")
                .add(ModificationType.LINE_STYLE.str, "test")
                .add(ModificationType.LINE_COLOR.str, "test")
                .add(ModificationType.LINE_WIDTH.str, "test")
                .add(ModificationType.FILL_COLOR.str, "test")
                .add(ModificationType.X.str, "test")
                .add(ModificationType.Y.str, "test")
                .build();
        final JsonObject wrongPayload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add(ModificationType.RADIUS.str, "test")
                .add(ModificationType.LINE_STYLE.str, "test")
                .add(ModificationType.LINE_COLOR.str, "test")
                .add(ModificationType.LINE_WIDTH.str, "test")
                .add(ModificationType.FILL_COLOR.str, "test")
                .add(ModificationType.X.str, "test")
                .add("false", "test")
                .build();
        assertTrue(Event.checkPayloadContains_ModificationType(payload));
        assertFalse(Event.checkPayloadContains_ModificationType(wrongPayload));
    }

    /**
     * Test that checkPayload method of Event is working on one case
     */
    @Test
    public void testCheckPayload() {
        final JsonObject payload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("sessionId", "test")
                .build();
        final Event event = new Event(EventType.DRAWER_CONNECTED, payload);
        assertTrue(event.checkPayload(payload));
        final JsonObject wrongPayload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("test", "test")
                .build();
        assertFalse(event.checkPayload(wrongPayload));
    }
}
