package com.paperboard.server;

import com.paperboard.drawings.Circle;
import com.paperboard.drawings.Drawing;
import com.paperboard.drawings.Position;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;

import javax.json.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

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
        this.registerToEvent(EventType.ASK_JOIN_BOARD, title);
        this.registerToEvent(EventType.ASK_CREATE_OBJECT, title);
        this.registerToEvent(EventType.ASK_LEAVE_BOARD, title);
    }

    public PaperBoard(final String title, final Optional<String> backgroundColor, final Optional<String> imageName) {
        this(title);
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

    private void handleAskJoinBoard(final Event e) {
        final User user = ServerApplication.getInstance().getConnectedUsers().get(e.payload.getString("pseudo"));
        this.drawers.add(user);

        // Broadcast a message with the updated list of users connected to the board
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArrayBuilder boardConnectedUsers = factory.createArrayBuilder();
        for (final User u : this.drawers) {
            boardConnectedUsers.add(u.getPseudo());
        }
        final JsonObject payload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("pseudo", user.getPseudo())
                .add("userlist", boardConnectedUsers)
                .add("board", this.title)
                .build();
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_JOINED_BOARD, payload), this.title);
    }

    private void handleAskLeaveBoard(final Event e) {
        // Broadcast a message with the updated list of users connected to the board
        final User user = ServerApplication.getInstance().getConnectedUsers().get(e.payload.getString("pseudo"));
        final String board = e.payload.getString("board");
        this.drawers.remove(user);

        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArrayBuilder boardConnectedUsers = factory.createArrayBuilder();
        for (final User u : this.drawers) {
            boardConnectedUsers.add(u.getPseudo());
        }
        final JsonObjectBuilder payloadFactory = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("pseudo", user.getPseudo())
                .add("userlist", boardConnectedUsers)
                .add("board", this.title);
        if (e.payload.containsKey("isDisconnect")) {
            payloadFactory.add("isDisconnect", "true");
        }
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_LEFT_BOARD, payloadFactory.build()), board);
    }

    private void handleAskCreateObject(final Event e) {
        final User user = ServerApplication.getInstance().getConnectedUsers().get(e.payload.getString("pseudo"));
        final String board = e.payload.getString("board");
        final Double positionX = Double.parseDouble(e.payload.getString("positionX"));
        final Double positionY = Double.parseDouble(e.payload.getString("positionY"));
        if (drawers.contains(user)) {
            final String shape = e.payload.getString("shape");
            switch (shape) {
                case "Circle":
                    final Circle circle = new Circle(user, new Position(positionX, positionY));
                    drawings.add(circle);
                    final JsonObject payload = Json.createBuilderFactory(null)
                            .createObjectBuilder()
                            .add("pseudo", user.getPseudo())
                            .add("shape", "circle")
                            .add("id", circle.getId())
                            .add("X", circle.getPosition().getX().toString())
                            .add("Y", circle.getPosition().getY().toString())
                            .add("radius", circle.getRadius().toString())
                            .add("lineWidth", circle.getLineWidth().toString())
                            .add("lineColor", circle.getLineColor())
                            .build();
                    EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_CREATED, payload), board);
                    break;
                default:
                    LOGGER.warning("This shape is not yet implemented" + shape);
            }
        } else {
            // TODO throw error
        }
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
            case ASK_JOIN_BOARD:
                handleAskJoinBoard(e);
                break;
            case ASK_LEAVE_BOARD:
                handleAskLeaveBoard(e);
                break;
            case ASK_CREATE_OBJECT:
                handleAskCreateObject(e);
                break;
            default:
                LOGGER.info("Detected Event " + e.type.toString() + " Not implemented");
        }
    }
}
