package com.paperboard.server.events;

import reactor.util.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

public class EventManager {

    private static EventManager instance = null;
    private HashMap<Subscriber, ArrayList<Subscription>> subscribers = new HashMap<>();
    private static String ALL_BOARDS = "--all boards--";
    private static Logger LOGGER = Logger.getLogger(EventManager.class.getName());

    //SINGLETON PATTERN
    private EventManager() {
    }

    // public getInstance method
    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void addSubscriber(final Subscriber s, final EventType type, @Nullable final String specificBoard) {
        if (!subscribers.containsKey(s)) {
            subscribers.put(s, new ArrayList<Subscription>());
        }
        final String board = specificBoard != null ? specificBoard : ALL_BOARDS;
        subscribers.get(s).add(new Subscription(type, board));
        LOGGER.info("Added new subscription for " + s.getClass()
                .getName() + " a subscriber for eventType : " + type + " for board " + specificBoard);
    }

    public void removeSubscriber(final Subscriber s) {
        if (subscribers.containsKey(s)) {
            subscribers.remove(s);
        }
    }

    public void removeSubscriber(final Subscriber s, @Nullable final EventType e,
            @Nullable final String specificBoard) {
        if (e == null && specificBoard == null) {
            this.removeSubscriber(s);
        } else if (subscribers.containsKey(s)) {
            final String board = specificBoard != null ? specificBoard : ALL_BOARDS;
            int index = 0;
            while (index < subscribers.get(s).size()) {
                if ((e == null || subscribers.get(s).get(index).eventType.equals(e)) && subscribers.get(s)
                        .get(index).board.equals(board)) {
                    subscribers.get(s).remove(index);
                } else {
                    index += 1;
                }
            }
        }
    }

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
                        LOGGER.warning("An error occurred in the method updateFromEvent. Or Subscriber object is now "
                                + "null. It should have unsubscribed before being destroyed !");
                        LOGGER.warning(e.getStackTrace().toString());
                    }
                    fired = true;
                }
            }
        }
    }
}

class Subscription {
    public final EventType eventType;
    public final String board;

    public Subscription(final EventType eventType, final String board) {
        this.eventType = eventType;
        this.board = board;
    }
}
