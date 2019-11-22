package com.paperboard.server;

import com.paperboard.drawings.*;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;

import javax.json.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.paperboard.drawings.DrawingType.CIRCLE;

public class PaperBoard implements Subscriber {

    private static AtomicLong idCounter = new AtomicLong(0);
    final private String id;
    final private String title;
    private String backgroundColor;
    private java.util.Set<User> drawers = new HashSet<>();
    private ConcurrentHashMap<String, Drawing> drawings = new ConcurrentHashMap<String, Drawing>();
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
        this.registerToEvent(EventType.ASK_LOCK_OBJECT, title);
        this.registerToEvent(EventType.ASK_UNLOCK_OBJECT, title);
        this.registerToEvent(EventType.ASK_EDIT_OBJECT, title);
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

    public ConcurrentHashMap<String, Drawing> getDrawings() {
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

        // unlock object if the drawers has locked one
        for (final String drawingId : this.drawings.keySet()) {
            final Drawing drawing = this.drawings.get(drawingId);
            if (drawing.getLockedBy().equals(user.getPseudo())) {
                drawing.unlockDrawing(user);
            }
        }

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
                    drawings.put(circle.getId(), circle);
                    final JsonObject payload = Json.createBuilderFactory(null)
                            .createObjectBuilder()
                            .add("pseudo", user.getPseudo())
                            .add("shape", CIRCLE.str)
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

    private void handleAskLockObject(final Event e) {
        final User user = ServerApplication.getInstance().getConnectedUsers().get(e.payload.getString("pseudo"));
        final String board = this.title;
        final String drawingId = e.payload.getString("drawingId");

        final Drawing drawing = this.drawings.get(drawingId);
        for (final String drawId : this.drawings.keySet()) {
            if (this.drawings.get(drawId).getLockedBy().equals(user.getPseudo())) {
                //TODO throw error, ot possible to lock two paperboard
                return;
            }
        }
        if (drawing.lockDrawing(user)) {
            final JsonObject payload = Json.createBuilderFactory(null)
                    .createObjectBuilder()
                    .add("pseudo", user.getPseudo())
                    .add("drawingId", drawingId)
                    .add("board", board)
                    .build();
            EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_LOCKED, payload), board);
        }
    }

    private void handleAskUnlockObject(final Event e) {
        final User user = ServerApplication.getInstance().getConnectedUsers().get(e.payload.getString("pseudo"));
        final String board = this.title;
        final String drawingId = e.payload.getString("drawingId");

        final Drawing drawing = this.drawings.get(drawingId);
        if (drawing.unlockDrawing(user)) {
            final JsonObject payload = Json.createBuilderFactory(null)
                    .createObjectBuilder()
                    .add("pseudo", user.getPseudo())
                    .add("drawingId", drawingId)
                    .add("board", board)
                    .build();
            EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_UNLOCKED, payload), board);
        }
        return;
    }

    private void handleAskEditObject(final Event e) {
        final User user = ServerApplication.getInstance().getConnectedUsers().get(e.payload.getString("pseudo"));
        final String board = this.title;
        final String drawingId = e.payload.getString("drawingId");

        final Drawing drawing = this.drawings.get(drawingId);
        final String drawingType = drawing.getType();
        final JsonObject payload = e.payload;
        final Set<String> keys = payload.keySet();

        final JsonObjectBuilder modifications = Json.createBuilderFactory(null).createObjectBuilder();
        modifications.add("pseudo", user.getPseudo()).add("drawingId", drawingId).add("board", board);

        // Check that you can modify the drawing
        if (drawing.isLocked() && user.getPseudo().equals(drawing.getLockedBy())) {
            // Default modification
            if (payload.containsKey("X") && payload.containsKey("Y")) {
                final Double x = Double.parseDouble(payload.getString("X"));
                final Double y = Double.parseDouble(payload.getString("Y"));
                drawing.setPosition(new Position(x, y));
                modifications.add("X", x.toString()).add("Y", y.toString());
            }
            switch (DrawingType.getEnum(drawingType)) {
                case CIRCLE:
                    final Circle circle = (Circle) drawing;
                    for (final String key : keys) {
                        switch (ModificationType.getEnum(key)) {
                            case LINE_WIDTH:
                                final Double lineWidth =
                                        Double.parseDouble(payload.getString(ModificationType.LINE_WIDTH.str));
                                circle.setLineWidth(lineWidth);
                                modifications.add(ModificationType.LINE_WIDTH.str, lineWidth.toString());
                                break;
                            case LINE_COLOR:
                                final String lineColor = payload.getString(ModificationType.LINE_COLOR.str);
                                circle.setLineColor(lineColor);
                                modifications.add(ModificationType.LINE_COLOR.str, lineColor);
                                break;
                            case RADIUS:
                                final Double radius =
                                        Double.parseDouble(payload.getString(ModificationType.RADIUS.str));
                                circle.setRadius(radius);
                                modifications.add(ModificationType.RADIUS.str, radius.toString());
                                break;
                            default:
                                LOGGER.warning("This modification is not yet implemented for edition" + key);
                        }
                    }
                    break;
                default:
                    LOGGER.warning("This shape is not yet implemented for edition" + drawingType);
            }
            EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_EDITED, modifications.build()), board);
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
            case ASK_LOCK_OBJECT:
                handleAskLockObject(e);
                break;
            case ASK_UNLOCK_OBJECT:
                handleAskUnlockObject(e);
                break;
            case ASK_EDIT_OBJECT:
                handleAskEditObject(e);
                break;
            default:
                LOGGER.info("Detected Event " + e.type.toString() + " Not implemented");
        }
    }
}
