package com.paperboard.server.events;

import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * Singleton pattern for class EventManager,dispatch event between different subscribers
 */
public class EventManager {

    private static EventManager instance = null;
    private HashMap<Subscriber, ArrayList<Subscription>> subscribers = new HashMap<>();
    private static String ALL_BOARDS = "--all boards--";
    private static Logger LOGGER = Logger.getLogger(EventManager.class.getName());

    //SINGLETON PATTERN
    private EventManager() {
    }

    /**
     * Get the singleton instance of EventManager
     *
     * @return EventManager
     */
    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    /**
     * Method to add a subscriber for a specific Event
     *
     * @param s             Subscriber the class implemented Subscriber
     * @param type          EventType
     * @param specificBoard Nullable String specifying a board
     */
    void addSubscriber(final Subscriber s, final EventType type, @Nullable final String specificBoard) {
        if (!subscribers.containsKey(s)) {
            subscribers.put(s, new ArrayList<>());
        }
        final String board = specificBoard != null ? specificBoard : ALL_BOARDS;
        subscribers.get(s).add(new Subscription(type, board));
        LOGGER.info("Added new subscription for " +
                    s.getClass().getName() +
                    " a subscriber for eventType : " +
                    type +
                    " for board " +
                    specificBoard);
    }

    /**
     * Remove all subscriptions for a subscriber
     *
     * @param s Subscriber
     */
    void removeSubscriber(final Subscriber s) {
        subscribers.remove(s);
    }

    /**
     * Fire the event to all the subscribers registered to this Event and for a specific board if specified
     *
     * @param event         Event
     * @param specificBoard Nullable String specifying the board
     */
    public void fireEvent(final Event event, @Nullable final String specificBoard) {
        LOGGER.info("Firing Event " + event.type.toString() + " [Board-" + specificBoard + "] !");
        final String board = specificBoard != null ? specificBoard : ALL_BOARDS;
        for (final Subscriber s : subscribers.keySet()) {
            final Iterator<Subscription> iter = this.subscribers.get(s).iterator();
            boolean fired = false;
            while (!fired && iter.hasNext()) {
                final Subscription sub = iter.next();
                if (sub.eventType.equals(event.type) && (sub.board.equals(ALL_BOARDS) || sub.board.equals(board))) {
                    event.firedAt = new Date();
                    try {
                        s.updateFromEvent(event);
                    } catch (final NullPointerException e) {
                        LOGGER.warning("An error occurred in the method updateFromEvent. Or Subscriber object is now " +
                                       "null. It should have unsubscribed before being destroyed !");
                        LOGGER.warning(e.getStackTrace().toString());
                    }
                    fired = true;
                }
            }
        }
    }
}

/**
 * Subscription class
 */
class Subscription {
    final EventType eventType;
    public final String board;

    Subscription(final EventType eventType, final String board) {
        this.eventType = eventType;
        this.board     = board;
    }
}
