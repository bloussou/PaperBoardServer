package com.paperboard.server.events;

/**
 * Define methods a component subscribing to event must and should use.
 */
public interface Subscriber {

    /**
     * Method called when an event is throwed to all component or to a specific board.
     *
     * @param e Event
     */
    void updateFromEvent(Event e);

    /**
     * Register to an Event without specifying any board.
     *
     * @param eventTypes EventType
     */
    default void registerToEvent(final EventType... eventTypes) {
        for (final EventType e : eventTypes) {
            EventManager.getInstance().addSubscriber(this, e, null);
        }

    }

    /**
     * Register for an Event and a  specific board
     *
     * @param events        Varargs of EventType
     * @param specificBoard Title of the board (unique)
     */
    default void registerToEvent(final String specificBoard, final EventType... events) {
        for (final EventType e : events) {
            EventManager.getInstance().addSubscriber(this, e, specificBoard);
        }
    }

    /**
     * unregisterFrom all event you have registered
     */
    default void unregisterFromAllEvent() {
        EventManager.getInstance().removeSubscriber(this);
    }
}
