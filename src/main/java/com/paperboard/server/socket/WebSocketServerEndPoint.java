package com.paperboard.server.socket;

import com.paperboard.server.Paperboard;
import com.paperboard.server.PaperboardApplication;
import com.paperboard.server.error.PaperboardAlreadyExistException;
import com.paperboard.server.error.UserAlreadyExistException;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.EventType;

import javax.json.*;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Entry point for frontEnd messages
 */
@ServerEndpoint(value = "/v1/paperboard", encoders = MessageEncoder.class, decoders = MessageDecoder.class,
        configurator = WebSocketServerConfigurator.class)
public class WebSocketServerEndPoint {

    private static String NOT_IN_A_BOARD = "-Not In a Board-";
    private static ConcurrentHashMap<String, HashSet<Session>> sessionsMap = new ConcurrentHashMap<>();
    private static final Logger LOGGER = Logger.getLogger(WebSocketServerEndPoint.class.getName());

    @OnOpen
    public void open(final Session session) {
        session.getUserProperties().put("board", NOT_IN_A_BOARD);
        if (!sessionsMap.containsKey(NOT_IN_A_BOARD)) {
            sessionsMap.put(NOT_IN_A_BOARD, new HashSet<>());
        }
        Objects.requireNonNull(sessionsMap.get(NOT_IN_A_BOARD)).add(session);

        // Generate event DRAWER_CONNECTED
        LOGGER.info("[" + NOT_IN_A_BOARD + "] New user connected to socket server !! (id:" + session.getId() + ").");
        final JsonObject payload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("sessionId", session.getId())
                .build();
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_CONNECTED, payload), null);
    }

    /**
     * Method called when a message is received from frontend.
     * <p>
     * When you send something through a socket message, your message should be a json formatted string like
     * string s = '{"from": "Ludo", "to": "Brieuc", "type": "Edit Object", "payload":{"shape": "rectangle",
     * "drawingId": "rect-0121", "color": "blue"}}'
     *
     * @param message Message
     * @param session Session
     */
    @OnMessage
    public void onMessage(final Message message, final Session session) {
        final String user = (String) session.getUserProperties().get("username");
        final String board = (String) session.getUserProperties().get("board");
        LOGGER.info("[" + board + "] Received [" + message.getType() + "] from [" + user + "].");

        final JsonObject payload;
        switch (Objects.requireNonNull(MessageType.getEnum(message.getType()))) {
            case MSG_IDENTIFY:
                // Generate event ASK_IDENTITY
                payload = Json.createObjectBuilder()
                        .add("pseudo", message.getPayload().getString("pseudo"))
                        .add("sessionId", session.getId())
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_IDENTITY, payload), null);
                break;
            case MSG_GET_BOARD:
                this.handleMsgGetBoard(session, message);
                break;
            case MSG_GET_ALL_BOARDS:
                this.handleMsgGetAllBoards(session);
                break;
            case MSG_CREATE_BOARD:
                this.handleMsgCreateBoard(session, message);
                break;
            case MSG_JOIN_BOARD:
                // Generate event ASK_JOIN_BOARD
                payload = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", user)
                        .add("board", message.getPayload().getString("board"))
                        .build();
                EventManager.getInstance()
                        .fireEvent(new Event(EventType.ASK_JOIN_BOARD, payload),
                                message.getPayload().getString("board"));
                break;
            case MSG_LEAVE_BOARD:
                // Generate event ASK_LEAVE_BOARD
                payload = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", user)
                        .add("board", board)
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_LEAVE_BOARD, payload), board);
                break;
            case MSG_CREATE_OBJECT:
                // Generate event ASK_CREATE_OBJECT
                final JsonObjectBuilder description = message.getPayload().containsKey("description") ?
                        Json.createObjectBuilder(message.getPayload()
                                .getJsonObject("description")) :
                        Json.createObjectBuilder();
                payload = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", user)
                        .add("board", board)
                        .add("shape", message.getPayload().getString("shape"))
                        .add("positionX", message.getPayload().getString("positionX"))
                        .add("positionY", message.getPayload().getString("positionY"))
                        .add("description", description)
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_CREATE_OBJECT, payload), board);
                break;
            case MSG_EDIT_OBJECT:
                // Generate event ASK_EDIT_OBJECT
                final JsonObjectBuilder payloadBuilder = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", user)
                        .add("board", board);
                for (final String key : message.getPayload().keySet()) {
                    if (key.equals("positionEndPoint")) {
<<<<<<< HEAD
<<<<<<<HEAD
                        payloadBuilder.add("positionEndPoint", Json.createObjectBuilder(message.getPayload().getJsonObject("positionEndPoint")));
                    } else if (key.equals("pathX") || key.equals("pathY")) {
                        payloadBuilder.add(key, Json.createArrayBuilder(message.getPayload().getJsonArray(key)));
=======
                        payloadBuilder.add("positionEndPoint",
                                Json.createObjectBuilder(message.getPayload()
                                        .getJsonObject("positionEndPoint")));
=======
                        payloadBuilder.add("positionEndPoint",
                                           Json.createObjectBuilder(message.getPayload()
                                                                            .getJsonObject("positionEndPoint")));
                    } else if (key.equals("pathX") || key.equals("pathY")) {
                        payloadBuilder.add(key, Json.createArrayBuilder(message.getPayload().getJsonArray(key)));
>>>>>>> e9d6d4ef7e5489190bcce1de858382c22867b145
                    } else if (!key.equals("pseudo") && !key.equals("board")) {
                        payloadBuilder.add(key, message.getPayload().getString(key));
>>>>>>>1f 73 b4828977b650fb38a9b080c3c53eef63f017
                    }
                }
                payload = payloadBuilder.build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_EDIT_OBJECT, payload), board);
                break;
            case MSG_LOCK_OBJECT:
                // Generate event ASK_LOCK_OBJECT
                payload = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", user)
                        .add("board", board)
                        .add("drawingId", message.getPayload().getString("drawingId"))
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_LOCK_OBJECT, payload), board);
                break;
            case MSG_UNLOCK_OBJECT:
                // Generate event ASK_UNLOCK_OBJECT
                payload = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", user)
                        .add("board", board)
                        .add("drawingId", message.getPayload().getString("drawingId"))
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_UNLOCK_OBJECT, payload), board);
                break;
            case MSG_DELETE_OBJECT:
                // Generate event ASK_DELETE_OBJECT
                payload = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", user)
                        .add("board", board)
                        .add("drawingId", message.getPayload().getString("drawingId"))
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_DELETE_OBJECT, payload), board);
                break;
            case MSG_CHAT_MESSAGE:
                // Generate event CHAT_MESSAGE
                payload = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", user)
                        .add("board", board)
                        .add("msg", message.getPayload().getString("msg"))
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.CHAT_MESSAGE, payload), board);
                break;
            default:
                LOGGER.info("Message Type Unhandled : " + message.getType() + "!!");
                break;
        }
    }

    @OnError
    public void onError(final Session session, final Throwable t) {
        LOGGER.warning("SocketEndPointError: " + t.getMessage());
        final String id = session.getId();
        final String board = (String) session.getUserProperties().get("board");
        final String username = (String) session.getUserProperties().get("username");
        LOGGER.warning("Error occurred with session [Id-" + id + "] [User-" + username + "] [Board-" + board + "].");
    }

    /**
     * Method called when frontend stop connection with the backend
     *
     * @param session     session
     * @param closeReason CloseReason
     */
    @OnClose
    public void onClose(final Session session, final CloseReason closeReason) {
        final String pseudo = (String) session.getUserProperties().get("username");
        final String board = (String) session.getUserProperties().get("board");

        if (!board.equals(NOT_IN_A_BOARD)) {
            // If user in a board send event ASK_LEAVE_BOARD with isDisconnect
            final JsonObject payload = Json.createBuilderFactory(null)
                    .createObjectBuilder()
                    .add("pseudo", pseudo)
                    .add("board", board)
                    .add("isDisconnect", "true")
                    .build();
            EventManager.getInstance().fireEvent(new Event(EventType.ASK_LEAVE_BOARD, payload), board);
        } else {
            // Remove the session of the sessionMap if not in a board.
            Objects.requireNonNull(sessionsMap.get(NOT_IN_A_BOARD)).remove(session);
        }


        final JsonObjectBuilder payloadBuilder = Json.createBuilderFactory(null).createObjectBuilder();
        if (pseudo != null) {
            // if user already logged in
            payloadBuilder.add("pseudo", pseudo);
        } else {
            // if user on welcome page
            payloadBuilder.add("sessionId", session.getId());
        }
        final JsonObject payload = payloadBuilder.build();
        LOGGER.info("[" +
                NOT_IN_A_BOARD +
                "] user [" +
                pseudo +
                "] disconnected. (Close reason: " +
                closeReason.getReasonPhrase() +
                ")");
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_DISCONNECTED, payload), null);
    }


    /**
     * Get the user session with its pseudo
     *
     * @param pseudo String
     * @return Session
     */
    private static Session getSession(final String pseudo) {
        // find the session of user
        boolean found = false;
        Session session = null;
        final Iterator<String> keys = sessionsMap.keySet().iterator();
        while (!found && keys.hasNext()) {
            final Iterator<Session> sessions = Objects.requireNonNull(sessionsMap.get(keys.next())).iterator();
            while (!found && sessions.hasNext()) {
                final Session s = sessions.next();
                if (s.isOpen() && pseudo.equals(s.getUserProperties().get("username"))) {
                    session = s;
                    found = true;
                }
            }
        }
        return session;
    }

    /**
     * Send msg to a specific user
     *
     * @param msg Message
     */
    public static void sendMessageToUser(final Message msg) {
        final String recipient = msg.getTo();

        // find recipient's session
        try {
            boolean recipientFound = false;
            final Session session = getSession(recipient);
            if (session != null) {
                session.getBasicRemote().sendObject(msg);
                recipientFound = true;
                LOGGER.info("Server sent [" + msg.getType() + "] to User [" + recipient + "].");
            }
            if (!recipientFound) {
                LOGGER.warning("[User-" +
                        msg.getFrom() +
                        "] wanted to send message to " +
                        recipient +
                        " But no use " +
                        "was found with this name.");
            }
        } catch (final IOException | EncodeException e) {
            LOGGER.warning("SendMessageToUser [" + recipient + "] failed");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Send msg to all the connected drawer of a board
     *
     * @param board String
     * @param msg   Message
     */
    private static void sendMessageToBoard(final String board, final Message msg) {
        try {
            if (sessionsMap.containsKey(board)) {
                for (final Session s : Objects.requireNonNull(sessionsMap.get(board))) {
                    if (s.isOpen() && board.equals(s.getUserProperties().get("board"))) {
                        s.getBasicRemote().sendObject(msg);
                    }
                }
                LOGGER.info("Server sent [" + msg.getType() + "] to Board [" + board + "].");
            }
        } catch (final IOException | EncodeException e) {
            LOGGER.warning("SendMessageToBoard [" + board + "] failed");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Send msg to a specific user using its session
     *
     * @param session Session
     * @param msg     msg
     */
    private static void sendMessageToSession(final Session session, final Message msg) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendObject(msg);
            }

            LOGGER.info("Server sent [" + msg.getType() + "] to Session [" + session.getId() + "].");

        } catch (final IOException | EncodeException e) {
            LOGGER.warning("SendMessageToSession [" + session.getId() + "] failed");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * @param session Session
     * @param message Message
     */
    private void handleMsgGetBoard(final Session session, final Message message) {
        if (message.getPayload().containsKey("title")) {
            final Paperboard paperboard = PaperboardApplication.getPaperboard(message.getPayload().getString("title"));
            final JsonObject payload = Json.createObjectBuilder()
                    .add("paperboard", paperboard.encodeToJsonObjectBuilder())
                    .build();
            final Message answer = new Message(MessageType.MSG_ANSWER_GET_BOARD.str,
                    "server",
                    (String) session.getUserProperties().get("username"),
                    payload);

            sendMessageToSession(session, answer);
        }
    }

    /**
     * Answer to the MessageType.MSG_GET_ALL_BOARDS
     *
     * @param session Session that sent the message
     */
    private void handleMsgGetAllBoards(final Session session) {
        final HashSet<Paperboard> paperboards = PaperboardApplication.getPaperboards();
        final Iterator<Paperboard> iter = paperboards.iterator();
        final JsonArrayBuilder dataList = Json.createArrayBuilder();
        while (iter.hasNext()) {
            dataList.add(iter.next().encodeToJsonObjectBuilder());
        }
        final JsonObject payload = Json.createObjectBuilder().add("paperboards", dataList).build();
        final Message answer = new Message(MessageType.MSG_ANSWER_GET_ALL_BOARDS.str,
                "server",
                (String) session.getUserProperties().get("username"),
                payload);
        sendMessageToSession(session, answer);
    }

    /**
     * Answer to the MessageType.MSG_CREATE_BOARD
     *
     * @param session Session
     * @param message Message
     */
    private void handleMsgCreateBoard(final Session session, final Message message) {
        // Check which values are in the payload
        final JsonObject payload = message.getPayload();
        final String title = payload.containsKey("title") ? message.getPayload().getString("title") : null;
        final String backgroundColor = payload.containsKey("backgroundColor") ? payload.getString("backgroundColor") :
                null;
        final String backgroundImage = payload.containsKey("backgroundImage") ? payload.getString("backgroundImage") :
                null;

        if (title != null) {
            final Paperboard paperboard = new Paperboard(title, backgroundColor, backgroundImage);
            try {
                PaperboardApplication.addPaperboard(paperboard);
                final JsonObject payloadAnswer = Json.createObjectBuilder().add("created", true).build();
                final Message answer = new Message(MessageType.MSG_ANSWER_CREATE_BOARD.str,
                        "server",
                        (String) session.getUserProperties().get("username"),
                        payloadAnswer);
                sendMessageToSession(session, answer);
            } catch (final PaperboardAlreadyExistException e) {
                LOGGER.warning("Someone tried to create paperboard [" +
                        message.getPayload().getString("title") +
                        "] but it already exists.");
                final JsonObject payloadAnswer = Json.createObjectBuilder()
                        .add("created", false)
                        .add("reason", "Already Exists")
                        .build();
                final Message answer = new Message(MessageType.MSG_ANSWER_CREATE_BOARD.str,
                        "server",
                        (String) session.getUserProperties().get("username"),
                        payloadAnswer);
                sendMessageToSession(session, answer);
            }
        }
    }

    /**
     * Action when the EventType.DRAWER_IDENTIFICATION is received
     *
     * @param e Event with type DRAWER_IDENTIFICATION
     * @throws UserAlreadyExistException is user with same pseudo exists
     */
    static void handleEventDrawerIdentification(final Event e) {
        final String sessionId = e.payload.getString("sessionId");
        final String pseudo = e.payload.getString("pseudo");

        // Check that the pseudo is not already identified
        Session session = null;
        boolean pseudoAlreadyInUse = false;
        for (final String value : sessionsMap.keySet()) {
            for (final Session s : Objects.requireNonNull(sessionsMap.get(value))) {
                if (sessionId.equals(s.getId())) {
                    session = s;
                } else if (s.isOpen() && pseudo.equals(s.getUserProperties().get("username"))) {
                    pseudoAlreadyInUse = true;
                }
            }
        }

        final JsonObject p;
        if (!pseudoAlreadyInUse && session != null) {
            session.getUserProperties().put("username", pseudo);
            p = Json.createBuilderFactory(null).createObjectBuilder().add("pseudoAvailable", true).build();
        } else {
            p = Json.createBuilderFactory(null).createObjectBuilder().add("pseudoAvailable", false).build();
        }

        final Message answer = new Message(MessageType.MSG_IDENTITY_ANSWER.str, "server", "Unknown Yet", p);
        try {
            session.getBasicRemote().sendObject(answer);
        } catch (final IOException | EncodeException ex) {
            LOGGER.warning("Error in Identifying method.");
            LOGGER.warning(ex.getMessage());
            LOGGER.warning(Arrays.toString(ex.getStackTrace()));
        }
    }

    /**
     * Action when the EventType.DRAWER_JOINED_BOARD is received
     *
     * @param event Event with type DRAWER_JOINED_BOARD
     */
    static void handleEventDrawerJoinedBoard(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final String board = event.payload.getString("board");
        final JsonArray userlist = event.payload.getJsonArray("userlist");

        final Session session = getSession(pseudo);

        if (session != null) {
            // Add the corresponding session to the set associated with it
            Objects.requireNonNull(sessionsMap.get(NOT_IN_A_BOARD)).remove(session);
            if (!sessionsMap.containsKey(board)) {
                sessionsMap.put(board, new HashSet<>());
            }
            if (!Objects.requireNonNull(sessionsMap.get(board)).contains(session)) {
                Objects.requireNonNull(sessionsMap.get(board)).add(session);
                session.getUserProperties().put("board", board);
            }

            // Broadcast a message with the updated list of users connected to the board
            final JsonBuilderFactory factory = Json.createBuilderFactory(null);
            final JsonArrayBuilder boardConnectedUsers = factory.createArrayBuilder(userlist);
            final JsonObject payload = Json.createBuilderFactory(null)
                    .createObjectBuilder()
                    .add("joiner", pseudo)
                    .add("userlist", boardConnectedUsers)
                    .build();

            final Message broadcast = new Message(MessageType.MSG_DRAWER_JOINED_BOARD.str,
                    "server",
                    "all board members",
                    payload);
            sendMessageToBoard(board, broadcast);
        } else {
            LOGGER.warning("Drawer joined the board" + board + " but no corresponding socket session was found...");
        }

    }

    /**
     * Action when the EventType.DRAWER_LEFT_BOARD is received
     *
     * @param event Event with type DRAWER_JOINED_BOARD
     */
    static void handleEventDrawerLeftBoard(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final String board = event.payload.getString("board");

        final Session session = getSession(pseudo);

        if (!board.equals(NOT_IN_A_BOARD) && sessionsMap.containsKey(board)) {
            Objects.requireNonNull(sessionsMap.get(board)).remove(session);
        }
        if (!Objects.requireNonNull(sessionsMap.get(NOT_IN_A_BOARD)).contains(session) &&
                !event.payload.containsKey("isDisconnect")) {
            Objects.requireNonNull(sessionsMap.get(NOT_IN_A_BOARD)).add(session);
        }

        // Broadcast a message with the updated list of users connected to the board
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArrayBuilder boardConnectedUsers = factory.createArrayBuilder();
        for (final Session s : Objects.requireNonNull(sessionsMap.get(board))) {
            final String username = (String) s.getUserProperties().get("username");
            if (s.isOpen() && username != null) {
                boardConnectedUsers.add(username);
            }
        }
        final JsonObject payload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("leaver", pseudo)
                .add("userlist", boardConnectedUsers)
                .build();

        LOGGER.info("[Board-" + board + "] " + pseudo + " left the board (" + boardConnectedUsers.toString() + ".");
        final Message broadcast = new Message(MessageType.MSG_DRAWER_LEFT_BOARD.str,
                "server",
                "all board members",
                payload);
        // Broadcast message to board members and to the leaver
        sendMessageToBoard(board, broadcast);
        sendMessageToSession(session, broadcast);
    }

    /**
     * Action when the EventType.CHAT_MESSAGE is received
     *
     * @param event Event with type CHAT_MESSAGE
     */
    static void handleEventChatMessage(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final String msg = event.payload.getString("msg");
        final Session session = getSession(pseudo);
        final String board = (String) session.getUserProperties().get("board");

        final JsonObject payload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("writer", pseudo)
                .add("msg", msg)
                .build();
        final Message broadcast = new Message(MessageType.MSG_CHAT_MESSAGE.str, "server", "all board members", payload);
        if (!board.equals(NOT_IN_A_BOARD)) {
            // broadcast chat message to board members
            sendMessageToBoard(board, broadcast);
        }
    }

    /**
     * Action when the EventType.OBJECT_CREATED is received
     *
     * @param event Event with type OBJECT_CREATED
     */
    static void handleEventObjectCreated(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final Session session = getSession(pseudo);
        final String board = (String) session.getUserProperties().get("board");

        final JsonObject payload = event.payload;
        final Message broadcast = new Message(MessageType.MSG_OBJECT_CREATED.str,
                "server",
                "all board members",
                payload);
        if (!board.equals(NOT_IN_A_BOARD)) {
            sendMessageToBoard(board, broadcast);
        }
    }

    /**
     * Action when the EventType.OBJECT_LOCKED is received
     *
     * @param event Event with type OBJECT_LOCKED
     */
    static void handleEventObjectLocked(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final Session session = getSession(pseudo);
        final String board = (String) session.getUserProperties().get("board");

        final JsonObject payload = event.payload;
        final Message broadcast = new Message(MessageType.MSG_OBJECT_LOCKED.str,
                "server",
                "all board members",
                payload);
        if (!board.equals(NOT_IN_A_BOARD)) {
            sendMessageToBoard(board, broadcast);
        }
    }

    /**
     * Action when the EventType.OBJECT_UNLOCKED is received
     *
     * @param event Event with type OBJECT_UNLOCKED
     */
    static void handleEventObjectUnlocked(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final Session session = getSession(pseudo);
        final String board = (String) session.getUserProperties().get("board");

        final JsonObject payload = event.payload;
        final Message broadcast = new Message(MessageType.MSG_OBJECT_UNLOCKED.str,
                "server",
                "all board members",
                payload);
        if (!board.equals(NOT_IN_A_BOARD)) {
            sendMessageToBoard(board, broadcast);
        }
    }

    /**
     * Action when the EventType.OBJECT_EDITED is received
     *
     * @param event Event with type OBJECT_EDITED
     */
    static void handleEventObjectEdited(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final Session session = getSession(pseudo);
        final String board = (String) session.getUserProperties().get("board");

        final JsonObject payload = event.payload;
        final Message broadcast = new Message(MessageType.MSG_OBJECT_EDITED.str,
                "server",
                "all board members",
                payload);
        if (!board.equals(NOT_IN_A_BOARD)) {
            sendMessageToBoard(board, broadcast);
        }
    }

    /**
     * Action when the EventType.OBJECT_DELETED is received
     *
     * @param event Event with type OBJECT_DELETED
     */
    static void handleEventObjectDeleted(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final Session session = getSession(pseudo);
        final String board = (String) session.getUserProperties().get("board");

        final JsonObject payload = event.payload;
        final Message broadcast = new Message(MessageType.MSG_OBJECT_DELETED.str,
                "server",
                "all board members",
                payload);
        if (!board.equals(NOT_IN_A_BOARD)) {
            sendMessageToBoard(board, broadcast);
        }
    }


}