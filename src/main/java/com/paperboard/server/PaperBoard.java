package com.paperboard.server;

import com.paperboard.drawings.Drawing;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;
import com.paperboard.server.socket.Message;
import com.paperboard.server.socket.MessageType;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.paperboard.server.events.EventType.JOIN_BOARD;

public class PaperBoard implements Subscriber {

    private static AtomicLong idCounter = new AtomicLong(0);
    final private String id;
    final private String title;
    private String backgroundColor;
    private java.util.Set<User> drawers = new HashSet<>();
    private java.util.concurrent.CopyOnWriteArrayList<com.paperboard.drawings.Drawing> drawings =
            new CopyOnWriteArrayList<Drawing>();
    private String backgroundImageName;
    private static final Logger LOGGER = Logger.getLogger(PaperBoard.class.getName());


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
        this.registerToEvent(JOIN_BOARD, title);
    }

    public PaperBoard(final String title, final Optional<String> backgroundColor, final Optional<String> imageName) {
        this.id = String.valueOf(idCounter.getAndIncrement());
        this.title = title;
        this.creationDate = LocalDateTime.now();
        this.registerToEvent(JOIN_BOARD, title);
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

    @Override
    public void updateFromEvent(final Event e) {
        LOGGER.info("Detected Event " + e.type.toString() + " firing. Ready to react.");
        switch (e.type) {
            case JOIN_BOARD:
                final User user = ServerApplication.getInstance().getConnectedUsers().get(e.message.getFrom());
                this.drawers.add(user);
                // Broadcast a message with the updated list of users connected to the board
                final JsonBuilderFactory factory = Json.createBuilderFactory(null);
                final JsonArrayBuilder boardConnectedUsers = factory.createArrayBuilder();
                for (final User u : this.drawers) {
                    boardConnectedUsers.add(u.getPseudo());
                }
                final JsonObject payload = Json.createBuilderFactory(null).createObjectBuilder()
                        .add("joiner", user.getPseudo())
                        .add("userlist", boardConnectedUsers)
                        .add("board", this.getTitle())
                        .build();

                LOGGER.info("[Board-" + this.getTitle() + "] " + user.getPseudo() + " joined the board (" + boardConnectedUsers.toString() + ".");
                final Message broadcast = new Message(MessageType.MSG_DRAWER_JOINED_BOARD.str, "server", "all board members", payload);
                EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_JOINED_BOARD, broadcast), this.title);
                break;
            default:
                LOGGER.info("Detected Event " + e.type.toString() + " Not implemented");
        }
    }
}
