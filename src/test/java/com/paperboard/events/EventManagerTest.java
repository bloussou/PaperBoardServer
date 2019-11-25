package com.paperboard.events;

import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

import static org.junit.Assert.assertEquals;

public class EventManagerTest implements Subscriber {

    /**
     * Class to test that the event flow is working.
     * <p>
     * Is only updated for ASK_IDENTITY and ASK_LEAVE_BOARD and count the number of event received
     */
    class SubscriberTest implements Subscriber {
        int askIdentityCounter = 0;
        int askLeaveBoardCounter = 0;

        public SubscriberTest(final EventType... e) {
            for (final EventType eventType : e) {
                registerToEvent(eventType);
            }
        }

        public SubscriberTest(final String board, final EventType... e) {
            for (final EventType eventType : e) {
                registerToEvent(board, eventType);
            }

        }

        @Override
        public void updateFromEvent(final Event e) {
            switch (e.type) {
                case ASK_IDENTITY:
                    askIdentityCounter += 1;
                    break;
                case ASK_LEAVE_BOARD:
                    askLeaveBoardCounter += 1;
                    break;
            }
        }
    }


    /**
     * Test that events are received and sent in a good way
     */
    @Test
    public void testFireEvent() {
        final SubscriberTest subscriber = new SubscriberTest(EventType.ASK_IDENTITY, EventType.ASK_LEAVE_BOARD);
        final SubscriberTest subscriberBoard = new SubscriberTest("board",
                                                                  EventType.ASK_IDENTITY,
                                                                  EventType.ASK_LEAVE_BOARD);
        final SubscriberTest wrongSubscriber = new SubscriberTest(EventType.ASK_UNLOCK_OBJECT);
        final JsonObject askIdentityPayload = Json.createObjectBuilder()
                .add("pseudo", "pseudo")
                .add("sessionId", "id")
                .build();
        EventManager.getInstance().fireEvent(new Event(EventType.ASK_IDENTITY, askIdentityPayload), null);
        assertEquals(1, subscriber.askIdentityCounter);
        assertEquals(0, subscriberBoard.askIdentityCounter);
        assertEquals(0, wrongSubscriber.askIdentityCounter);
        assertEquals(0, subscriber.askLeaveBoardCounter);
        assertEquals(0, subscriberBoard.askLeaveBoardCounter);
        assertEquals(0, wrongSubscriber.askLeaveBoardCounter);
        EventManager.getInstance().fireEvent(new Event(EventType.ASK_IDENTITY, askIdentityPayload), "board");
        assertEquals(2, subscriber.askIdentityCounter);
        assertEquals(1, subscriberBoard.askIdentityCounter);
        assertEquals(0, wrongSubscriber.askIdentityCounter);
        assertEquals(0, subscriber.askLeaveBoardCounter);
        assertEquals(0, subscriberBoard.askLeaveBoardCounter);
        assertEquals(0, wrongSubscriber.askLeaveBoardCounter);
    }


    @Override
    public void updateFromEvent(final Event e) {

    }
}
