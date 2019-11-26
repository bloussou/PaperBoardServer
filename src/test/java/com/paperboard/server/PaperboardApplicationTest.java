package com.paperboard.server;

import com.paperboard.drawings.ModificationType;
import com.paperboard.server.error.PaperboardAlreadyExistException;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PaperboardApplicationTest {

    @Before
    public void before() {
        PaperboardApplication.getInstance();
        PaperboardApplication.getConnectedUsers().clear();
        PaperboardApplication.getPaperboards().clear();
    }

    @Test
    public void addPaperboardTest() {
        final Paperboard paperboard1 = new Paperboard("board");
        final Paperboard paperboard3 = new Paperboard("board3");
        PaperboardApplication.addPaperboard(paperboard1);
        PaperboardApplication.addPaperboard(paperboard3);
        assertEquals(PaperboardApplication.getPaperboards().size(), 2);
    }

    @Test(expected = PaperboardAlreadyExistException.class)
    public void testPaperboardAlreadyExistException() {
        final Paperboard paperboard1 = new Paperboard("board");
        final Paperboard paperboard3 = new Paperboard("board");
        PaperboardApplication.addPaperboard(paperboard1);
        PaperboardApplication.addPaperboard(paperboard3);
    }

    @Test
    public void testAddUser() {
        final SubscriberTest subscriberTest = new SubscriberTest(EventType.DRAWER_IDENTIFICATION);
        final JsonObject payload1 = Json.createObjectBuilder()
                .add("pseudo", "pseudo")
                .add("sessionId", "sessionId")
                .build();
        final Event event1 = new Event(EventType.ASK_IDENTITY, payload1);
        final JsonObject payload2 = Json.createObjectBuilder()
                .add("pseudo", "pseudo1")
                .add("sessionId", "sessionId")
                .build();
        final Event event2 = new Event(EventType.ASK_IDENTITY, payload2);

        PaperboardApplication.addUser("pseudo", event1);
        PaperboardApplication.addUser("pseudo1", event2);

        // Check DRAWER_IDENTIFICATION is sent 2 times
        assertEquals(2, subscriberTest.drawerIdentificationCounter);
        assertEquals(2, PaperboardApplication.getConnectedUsers().size());
    }

    @Test
    public void testDisconnectUser() {
        final JsonObject payload = Json.createObjectBuilder()
                .add(ModificationType.RADIUS.str, "12.0")
                .add("pseudo", "pseudo")
                .add("sessionId", "sessionId")
                .build();
        final Event event = new Event(EventType.ASK_IDENTITY, payload);

        // Connect the user
        PaperboardApplication.addUser("pseudo", event);
        assertEquals(1, PaperboardApplication.getConnectedUsers().size());

        // Disconnect the user
        PaperboardApplication.disconnectUser("pseudo");
        assertEquals(0, PaperboardApplication.getConnectedUsers().size());
    }

    @Test
    public void testGetPaperboard() {
        final Paperboard paperboard1 = new Paperboard("board");
        final Paperboard paperboard3 = new Paperboard("board3");
        PaperboardApplication.addPaperboard(paperboard1);
        PaperboardApplication.addPaperboard(paperboard3);


        assertEquals(PaperboardApplication.getPaperboards().size(), 2);
        // Test with wrong title
        assertNull(PaperboardApplication.getPaperboard("joe"));
        // Test with good title
        assertEquals(paperboard1, PaperboardApplication.getPaperboard("board"));
    }

    @Test
    public void testGetConnectUser() {
        final JsonObject payload1 = Json.createObjectBuilder()
                .add("pseudo", "pseudo")
                .add("sessionId", "sessionId")
                .build();
        final Event event1 = new Event(EventType.ASK_IDENTITY, payload1);
        final JsonObject payload2 = Json.createObjectBuilder()
                .add("pseudo", "pseudo1")
                .add("sessionId", "sessionId")
                .build();
        final Event event2 = new Event(EventType.ASK_IDENTITY, payload2);
        PaperboardApplication.addUser("pseudo", event1);
        PaperboardApplication.addUser("pseudo1", event2);

        // Test user "pseudo" is connected
        assertEquals(PaperboardApplication.getConnectedUser("pseudo").getPseudo(), "pseudo");

        // Test with a wrong user
        assertNull(PaperboardApplication.getConnectedUser("joe"));
    }

    class SubscriberTest implements Subscriber {
        int drawerIdentificationCounter = 0;

        public SubscriberTest(final EventType... e) {
            for (final EventType eventType : e) {
                registerToEvent(eventType);
            }
        }

        @Override
        public void updateFromEvent(final Event e) {
            switch (e.type) {
                case DRAWER_IDENTIFICATION:
                    drawerIdentificationCounter += 1;
                    break;
            }
        }
    }
}
