package com.paperboard.server.events;

public interface Subscriber {

    void updateFromEvent(Event e);

    default void registerToEvent(final EventType e) {
        EventManager.getInstance().addSubscriber(this, e, null);
    }

    default void registerToEvent(final EventType e, final String specificBoard) {
        EventManager.getInstance().addSubscriber(this, e, specificBoard);
    }

    default void unregisterFromAllEvent() {
        EventManager.getInstance().removeSubscriber(this);
    }

    default void unregisterFromEvent(final EventType type) {
        EventManager.getInstance().removeSubscriber(this, type, null);
    }

    default void unregisterFromEvent(final EventType type, final String specificBoard) {
        EventManager.getInstance().removeSubscriber(this, type, specificBoard);
    }

    default void unregisterFromBoard(final String specificBoard) {
        EventManager.getInstance().removeSubscriber(this, null, specificBoard);
    }

}
