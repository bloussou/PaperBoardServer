package com.paperboard.server;

import com.paperboard.drawings.Drawing;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.Subscriber;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class PaperBoard implements Subscriber {

    private static AtomicLong idCounter = new AtomicLong(0);
    final private String id;
    final private String title;
    private String backgroundColor;
    private java.util.Set<User> drawers = new ConcurrentSkipListSet<User>();
    private java.util.concurrent.CopyOnWriteArrayList<com.paperboard.drawings.Drawing> drawings =
            new CopyOnWriteArrayList<Drawing>();
    private byte[] backgroundImage;
    private static final Logger LOGGER = Logger.getLogger(PaperBoard.class.getName());


    public PaperBoard(final String title) {
        this.id = String.valueOf(idCounter.getAndIncrement());
        this.title = title;
    }

    public PaperBoard(final String title, final String backgroundColor) {
        this.id = String.valueOf(idCounter.getAndIncrement());
        this.title = title;
        this.setBackgroundColor(backgroundColor);
    }

    public PaperBoard(final String title, final byte[] image) {
        this.id = String.valueOf(idCounter.getAndIncrement());
        this.title = title;
        this.setBackgroundImage(image);
    }

    public String getTitle() {
        return title;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(final String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    public Set<User> getDrawers() {
        return drawers;
    }

    public CopyOnWriteArrayList<Drawing> getDrawings() {
        return drawings;
    }


    public byte[] getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(final byte[] backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        final PaperBoard paperBoard = (PaperBoard) obj;
        return this.getTitle().equals(paperBoard.getTitle());
    }

    @Override
    public int hashCode() {
        return this.getTitle().length();
    }

    @Override
    public void updateFromEvent(final Event e) {
        LOGGER.info("Detected Event " + e.type.toString() + " firing. Ready to react.");
        final Event eventWithData = e;
    }
}
