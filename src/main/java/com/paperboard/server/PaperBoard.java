package com.paperboard.server;

import com.paperboard.drawings.Drawing;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class PaperBoard {
    private static AtomicLong idCounter = new AtomicLong(0);
    final private String id;
    final private String title;
    private String backgroundColor;
    private java.util.Set<User> drawers = new ConcurrentSkipListSet<User>();
    private java.util.concurrent.CopyOnWriteArrayList<com.paperboard.drawings.Drawing> drawings =
            new CopyOnWriteArrayList<Drawing>();
    private String backgroundImageName;


    private LocalDateTime creationDate;


    public class PaperBoardInfo {
        private int numberOfConnectedUser;
        private String title;


        private LocalDateTime creationDate;

        public PaperBoardInfo(final String title, final int connectedUser, final LocalDateTime creationDate) {
            this.numberOfConnectedUser = connectedUser;
            this.title = title;
            this.creationDate = creationDate;
        }

        public int getNumberOfConnectedUser() {
            return numberOfConnectedUser;
        }

        public String getTitle() {
            return title;
        }

        public LocalDateTime getCreationDate() {
            return creationDate;
        }

    }

    public PaperBoard(final String title) {
        this.id = String.valueOf(idCounter.getAndIncrement());
        this.title = title;
        this.creationDate = LocalDateTime.now();
    }

    public PaperBoard(final String title, final Optional<String> backgroundColor, final Optional<String> imageName) {
        this.id = String.valueOf(idCounter.getAndIncrement());
        this.title = title;
        this.creationDate = LocalDateTime.now();
        if (!imageName.isEmpty()) {
            this.setBackgroundImageName(imageName.get());
        } else if (!backgroundColor.isEmpty()) {
            this.setBackgroundColor(backgroundColor.get());
        }
    }

    public PaperBoardInfo getInfo() {
        return new PaperBoardInfo(this.getTitle(), this.getDrawers().size(), this.getCreationDate());
    }


    public LocalDateTime getCreationDate() {
        return creationDate;
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


    public String getBackgroundImageName() {
        return backgroundImageName;
    }

    public void setBackgroundImageName(final String backgroundImageName) {
        this.backgroundImageName = backgroundImageName;
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
}
