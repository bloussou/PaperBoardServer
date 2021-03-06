package com.paperboard.server;

import com.paperboard.drawings.*;
import com.paperboard.drawings.shapes.*;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;
import com.paperboard.server.socket.Message;
import com.paperboard.server.socket.MessageType;
import com.paperboard.server.socket.WebSocketServerEndPoint;
import reactor.util.annotation.Nullable;

import javax.json.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class Paperboard implements Subscriber {

    final private static AtomicLong idCounter = new AtomicLong(0);
    final private String id;
    final private String title;
    final private LocalDateTime creationDate;
    final private static Logger LOGGER = Logger.getLogger(Paperboard.class.getName());
    private String backgroundColor = "";
    private java.util.Set<User> drawers = new HashSet<>();
    private ConcurrentHashMap<String, Drawing> drawings = new ConcurrentHashMap<>();
    private String backgroundImage = "";

    /**
     * Constructor if only a title is given
     *
     * @param title String
     */
    Paperboard(final String title) {
        this.id = String.valueOf(idCounter.getAndIncrement());
        this.title = title;
        this.creationDate = LocalDateTime.now();
        this.registerToEvent(title,
                EventType.ASK_JOIN_BOARD,
                EventType.ASK_CREATE_OBJECT,
                EventType.ASK_LEAVE_BOARD,
                EventType.ASK_LOCK_OBJECT,
                EventType.ASK_UNLOCK_OBJECT,
                EventType.ASK_EDIT_OBJECT,
                EventType.ASK_DELETE_OBJECT);
    }

    /**
     * Constructor to instantiate a Paperboard with a backgroundColor or a backgroundImage
     *
     * @param title           String
     * @param backgroundColor @Nullable String
     * @param backgroundImage @Nullable String
     */
    public Paperboard(final String title,
                      final @Nullable String backgroundColor,
                      final @Nullable String backgroundImage) {
        this(title);
        if (backgroundImage != null) {
            this.backgroundImage = backgroundImage;
        } else if (backgroundColor != null) {
            this.backgroundColor = backgroundColor;
        }
    }

    /**
     * Action when the EventType.ASK_JOIN_BOARD is received
     * <p>
     * send Event DRAWER_JOINED_BOARD at the end
     *
     * @param e Event
     */
    void handleAskJoinBoard(final Event e) {
        // Get the connected user
        final User user = PaperboardApplication.getConnectedUser(e.payload.getString("pseudo"));
        // Add the user to the board
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

    /**
     * Action when the EventType.ASK_LEAVE_BOARD is received
     * <p>
     * send Event DRAWER_LEFT_BOARD at the end
     *
     * @param e Event
     */
    private void handleAskLeaveBoard(final Event e) {
        // Broadcast a message with the updated list of users connected to the board
        final User user = PaperboardApplication.getConnectedUser(e.payload.getString("pseudo"));
        final String board = e.payload.getString("board");
        // Remove the user from the list of drawers
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

    /**
     * Action when the EventType.ASK_CREATE_OBJECT is received
     * <p>
     * send Event OBJECT_CREATED at the end
     *
     * @param e Event
     */
    private void handleAskCreateObject(final Event e) {
        final User user = PaperboardApplication.getConnectedUser(e.payload.getString("pseudo"));
        final String board = e.payload.getString("board");
        final Double positionX = Double.parseDouble(e.payload.getString("positionX"));
        final Double positionY = Double.parseDouble(e.payload.getString("positionY"));

        // If drawers is not in this board nothing happen
        if (drawers.contains(user)) {
            final String shape = e.payload.getString("shape");
            final JsonObject description = e.payload.getJsonObject("description");

            // For each type of object, create the object with the needed parameters according to the event
            switch (DrawingType.getEnum(shape)) {
                case CIRCLE:
                    final Circle circle = new Circle(user, new Position(positionX, positionY));
                    drawings.put(circle.getId(), circle);
                    final JsonObject payloadCircle = circle.encodeToJsonObjectBuilder()
                            .add("pseudo", user.getPseudo())
                            .build();
                    EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_CREATED, payloadCircle), board);
                    break;
                case IMAGE:
                    final Double width = Double.parseDouble(description.getString(ModificationType.WIDTH.str));
                    final Double height = Double.parseDouble(description.getString(ModificationType.HEIGHT.str));
                    final String srcURI = description.getString("srcURI");
                    final Image image = new Image(user, new Position(positionX, positionY), height, width, srcURI);
                    drawings.put(image.getId(), image);
                    final JsonObject payloadImage = image.encodeToJsonObjectBuilder()
                            .add("pseudo", user.getPseudo())
                            .build();
                    EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_CREATED, payloadImage), board);
                    break;
                case HANDWRITING:
                    final HandWriting handwriting = new HandWriting(user,
                            new Position(positionX, positionY));
                    drawings.put(handwriting.getId(), handwriting);
                    final JsonObject payloadHandwriting = handwriting.encodeToJsonObjectBuilder()
                            .add("pseudo", user.getPseudo())
                            .build();
                    EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_CREATED, payloadHandwriting), board);
                    break;
                case RECTANGLE:
                    final Rectangle rectangle = new Rectangle(user, new Position(positionX, positionY));
                    drawings.put(rectangle.getId(), rectangle);
                    final JsonObject payloadRectangle = rectangle.encodeToJsonObjectBuilder()
                            .add("pseudo", user.getPseudo())
                            .build();
                    EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_CREATED, payloadRectangle), board);
                    break;
                case TEXT_BOX:
                    final String text = description.getString(ModificationType.TEXT.str);
                    final TextBox textBox = new TextBox(user, new Position(positionX, positionY), text);
                    drawings.put(textBox.getId(), textBox);
                    final JsonObject payloadTextBox = textBox.encodeToJsonObjectBuilder()
                            .add("pseudo", user.getPseudo())
                            .build();
                    EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_CREATED, payloadTextBox), board);
                    break;
                case TRIANGLE:
                    final Triangle triangle = new Triangle(user, new Position(positionX, positionY));
                    drawings.put(triangle.getId(), triangle);
                    final JsonObject payloadTriangle = triangle.encodeToJsonObjectBuilder()
                            .add("pseudo", user.getPseudo())
                            .build();
                    EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_CREATED, payloadTriangle), board);
                    break;
                case LINE:
                    final Line line = new Line(user, new Position(positionX, positionY));
                    drawings.put(line.getId(), line);
                    final JsonObject payloadLine = line.encodeToJsonObjectBuilder()
                            .add("pseudo", user.getPseudo())
                            .build();
                    EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_CREATED, payloadLine), board);
                    break;
                default:
                    LOGGER.warning("This shape is not yet implemented " + shape);
            }
        }
    }

    /**
     * Action when the EventType.ASK_LOCK_OBJECT is received
     * <p>
     * send Event OBJECT_LOCKED at the end
     *
     * @param e Event
     */
    private void handleAskLockObject(final Event e) {
        final User user = PaperboardApplication.getConnectedUser(e.payload.getString("pseudo"));
        final String board = this.title;
        final String drawingId = e.payload.getString("drawingId");

        // Cannot lock multiple shape
        final Drawing drawing = this.drawings.get(drawingId);
        for (final String drawId : this.drawings.keySet()) {
            if (this.drawings.get(drawId).getLockedBy().equals(user.getPseudo())) {
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

    /**
     * Action when the EventType.ASK_UNLOCK_OBJECT is received
     * <p>
     * send Event OBJECT_UNLOCKED at the end
     *
     * @param e Event
     */
    private void handleAskUnlockObject(final Event e) {
        final User user = PaperboardApplication.getConnectedUser(e.payload.getString("pseudo"));
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
    }

    /**
     * Action when the EventType.ASK_DELETE_OBJECT is received
     * <p>
     * 1) if the creator (owner) of the shape is not connected the shape is deleted : event OBJECT_DELETED
     * 2) if the creator (owner) want to delete one of its shape, this one is deleted : event OBJECT_DELETED
     * 2) if a user want to delete the shape of a connected user, and alert is displayed in the frontend to ask him
     * to agree or disagree : message MSG_DELETE_OBJECT
     *
     * @param e Event
     */
    private void handleAskDeleteObject(final Event e) {
        final User user = PaperboardApplication.getConnectedUser(e.payload.getString("pseudo"));
        final String board = this.title;
        final String drawingId = e.payload.getString("drawingId");

        final Drawing drawing = this.drawings.get(drawingId);

        if (drawing.getOwner().equals(user) || !this.drawers.contains(drawing.getOwner())) {
            this.drawings.remove(drawingId);
            final JsonObject payload = Json.createBuilderFactory(null)
                    .createObjectBuilder()
                    .add("pseudo", user.getPseudo())
                    .add("drawingId", drawingId)
                    .add("board", board)
                    .build();
            EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_DELETED, payload), board);
        } else {
            final JsonObject payload = Json.createBuilderFactory(null)
                    .createObjectBuilder()
                    .add("pseudo", user.getPseudo())
                    .add("drawingId", drawingId)
                    .add("board", board)
                    .add("type", drawing.getType())
                    .build();
            final Message msg = new Message(MessageType.MSG_DELETE_OBJECT.str,
                    user.getPseudo(),
                    drawing.getOwner().getPseudo(),
                    payload);
            WebSocketServerEndPoint.sendMessageToUser(msg);
        }
    }

    /**
     * Action when the EventType.ASK_EDIT_OBJECT is received
     * <p>
     * send Event OBJECT_EDITED at the end
     * <p>
     * call drawing.editDrawing() on each drawing type. Modification contains only the needed parameters.
     *
     * @param e Event
     */
    private void handleAskEditObject(final Event e) {
        final User user = PaperboardApplication.getConnectedUser(e.payload.getString("pseudo"));
        final String board = this.title;
        final String drawingId = e.payload.getString("drawingId");

        final Drawing drawing = this.drawings.get(drawingId);
        final String drawingType = drawing.getType();
        final JsonObject payload = e.payload;

        JsonObjectBuilder modifications = Json.createObjectBuilder();
        // Check that you can modify the drawing
        if (drawing.isLocked() && user.getPseudo().equals(drawing.getLockedBy())) {
            // Default modification
            switch (DrawingType.getEnum(drawingType)) {
                case CIRCLE:
                    final Circle circle = (Circle) drawing;
                    modifications = circle.editDrawing(payload, board);
                    break;
                case HANDWRITING:
                    final HandWriting handWriting = (HandWriting) drawing;
                    modifications = handWriting.editDrawing(payload, board);
                    break;
                case LINE:
                    final Line line = (Line) drawing;
                    modifications = line.editDrawing(payload, board);
                    break;
                case RECTANGLE:
                    final Rectangle rectangle = (Rectangle) drawing;
                    modifications = rectangle.editDrawing(payload, board);
                    break;
                case TRIANGLE:
                    final Triangle triangle = (Triangle) drawing;
                    modifications = triangle.editDrawing(payload, board);
                    break;
                case IMAGE:
                    final Image image = (Image) drawing;
                    modifications = image.editDrawing(payload, board);
                    break;
                case TEXT_BOX:
                    final TextBox textBox = (TextBox) drawing;
                    modifications = textBox.editDrawing(payload, board);
                    break;
                default:
                    LOGGER.warning("This shape is not yet implemented for edition" + drawingType);
            }
            EventManager.getInstance().fireEvent(new Event(EventType.OBJECT_EDITED, modifications.build()), board);
        }
    }

    /**
     * Serialize paperboard in a JsonObject
     *
     * @return JsonObjectBuilder
     */
    public JsonObjectBuilder encodeToJsonObjectBuilder() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("backgroundColor", this.backgroundColor);
        builder.add("backgroundImage", this.backgroundImage);
        builder.add("creationDate", String.valueOf(this.creationDate));
        builder.add("numberOfConnectedUser", this.drawers.size());
        builder.add("title", this.title);
        builder.add("backgroundColor", this.backgroundColor);

        // Build list of drawers
        final JsonArrayBuilder drawers = Json.createArrayBuilder();
        for (final User user : this.drawers) {
            drawers.add(user.encodeToJsonObjectBuilder());
        }
        builder.add("drawers", drawers);

        // Build list of Drawings
        final JsonObjectBuilder drawings = Json.createObjectBuilder();
        for (final String s : this.drawings.keySet()) {
            final Drawing d = this.drawings.get(s);
            drawings.add(d.getId(), d.encodeToJsonObjectBuilder());
        }
        builder.add("drawings", drawings);

        return builder;
    }

    public String getTitle() {
        return title;
    }

    public ConcurrentHashMap<String, Drawing> getDrawings() {
        return drawings;
    }

    public Set<User> getDrawers() {
        return drawers;
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
        final Paperboard paperboard = (Paperboard) obj;
        return this.getTitle().equals(paperboard.getTitle());
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
            case ASK_DELETE_OBJECT:
                handleAskDeleteObject(e);
                break;
            default:
                LOGGER.info("Detected Event " + e.type.toString() + " Not implemented");
        }
    }
}
