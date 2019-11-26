package com.paperboard.server;

import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;
import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

import static org.junit.Assert.assertEquals;

public class PaperboardTest {

    /**
     * Reset and set default value before each test
     */
    @Before
    public void before() {
        PaperboardApplication.getInstance();
        PaperboardApplication.getConnectedUsers().clear();
        PaperboardApplication.getPaperboards().clear();
        final JsonObject payload = Json.createObjectBuilder()
                .add("pseudo", "defaultUser")
                .add("sessionId", "sessionId")
                .build();
        final Event event = new Event(EventType.ASK_IDENTITY, payload);
        PaperboardApplication.addUser("defaultUser", event);
        PaperboardApplication.addPaperboard(new Paperboard("defaultPaperboard"));
    }

    @Test
    public void testAskJoinBoard() {
        final SubscriberTest subscriberTest = new SubscriberTest(EventType.DRAWER_JOINED_BOARD);
        final Paperboard paperboard = PaperboardApplication.getPaperboard("defaultPaperboard");
        final JsonObject payload = Json.createObjectBuilder()
                .add("pseudo", "defaultUser")
                .add("board", "defaultPaperboard")
                .build();
        final Event event = new Event(EventType.ASK_JOIN_BOARD, payload);
        paperboard.handleAskJoinBoard(event);
        assertEquals(1, subscriberTest.drawerJoinedBoard);
        assertEquals(1, paperboard.getDrawers().size());
    }

//    @Test
//    public void testHandleAskLeaveBoard() {
//        final SubscriberTest subscriberTest = new SubscriberTest(EventType.DRAWER_JOINED_BOARD);
//        final Paperboard paperboard = PaperboardApplication.getPaperboard("defaultPaperboard");
//        final JsonObject payload = Json.createObjectBuilder()
//                .add("pseudo", "defaultUser")
//                .add("board", "defaultPaperboard")
//                .build();
//        final Event event = new Event(EventType.ASK_JOIN_BOARD, payload);
//        paperboard.handleAskJoinBoard(event);
//        assertEquals(1, subscriberTest.drawerJoinedBoard);
//        assertEquals(1, paperboard.getDrawers().size());
//    }


    /**
     * Count the events to check they have been fired
     */
    class SubscriberTest implements Subscriber {
        int drawerJoinedBoard = 0;

        public SubscriberTest(final EventType... e) {
            for (final EventType eventType : e) {
                registerToEvent(eventType);
            }
        }

        @Override
        public void updateFromEvent(final Event e) {
            switch (e.type) {
                case DRAWER_JOINED_BOARD:
                    drawerJoinedBoard += 1;
                    break;
            }
        }
    }
}
