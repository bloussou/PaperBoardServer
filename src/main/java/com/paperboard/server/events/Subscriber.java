package com.paperboard.server.events;

public interface Subscriber {

    public void updateFromEvent(Event e);

    public default void registerToEvent(final EventType e) {
        EventManager.getInstance().addSubscriber(this, e, null);
    }

    public default void registerToEvent(final EventType e, final String specificBoard) {
        EventManager.getInstance().addSubscriber(this, e, specificBoard);
    }

    public default void unregisterFromAllEvent() {
        EventManager.getInstance().removeSubscriber(this);
    }

    public default void unregisterFromEvent(final EventType type) {
        EventManager.getInstance().removeSubscriber(this, type, null);
    }

    public default void unregisterFromEvent(final EventType type, final String specificBoard) {
        EventManager.getInstance().removeSubscriber(this, type, specificBoard);
    }

    public default void unregisterFromBoard(final String specificBoard) {
        EventManager.getInstance().removeSubscriber(this, null, specificBoard);
    }

}
