package com.paperboard.server.socket;

import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.EventType;
import org.springframework.util.ConcurrentReferenceHashMap;

import javax.json.*;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

@ServerEndpoint(value = "/v1/paperboard", encoders = MessageEncoder.class, decoders = MessageDecoder.class,
        configurator = WebSocketServerConfigurator.class)
public class WebSocketServerEndPoint {

    public static String NOT_IN_A_BOARD = "-Not In a Board-";
    private static ConcurrentReferenceHashMap<String, HashSet<Session>> sessionsMap =
            new ConcurrentReferenceHashMap<>();
    private static final Logger LOGGER = Logger.getLogger(WebSocketServerEndPoint.class.getName());

    @OnOpen
    public void open(final Session session) {
        session.getUserProperties().put("board", NOT_IN_A_BOARD);
        if (!this.sessionsMap.containsKey(NOT_IN_A_BOARD)) {
            this.sessionsMap.put(NOT_IN_A_BOARD, new HashSet<Session>());
        }
        this.sessionsMap.get(NOT_IN_A_BOARD).add(session);

        // Generate event DRAWER_CONNECTED
        LOGGER.info("[" + NOT_IN_A_BOARD + "] New user connected to socket server !! (id:" + session.getId() + ").");
        final JsonObject payload = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("sessionId", session.getId())
                .build();
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_CONNECTED, payload), null);
    }

    /**
     * When you send something through a socket message, your message should be a json formatted string like
     * string s = '{"from": "Ludo", "to": "Brieuc", "type": "Edit Object", "payload":{"shapeType": "rectangle",
     * "drawingId": "rect-0121", "color": "blue"}}'
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(final Message message, final Session session) {
        final String user = (String) session.getUserProperties().get("username");
        final String board = (String) session.getUserProperties().get("board");
        LOGGER.info("[" + board + "] Received [" + message.getType() + "] from [" + user + "].");

        final JsonObject payload;
        switch (MessageType.getEnum(message.getType())) {
            case MSG_IDENTIFY:
                // Generate event ASK_IDENTITY
                payload = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", message.getPayload().getString("pseudo"))
                        .add("sessionId", session.getId())
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_IDENTITY, payload), null);
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
                payload = Json.createBuilderFactory(null)
                        .createObjectBuilder()
                        .add("pseudo", user)
                        .add("board", board)
                        .add("shape", message.getPayload().getString("shape"))
                        .add("positionX", message.getPayload().getString("positionX"))
                        .add("positionY", message.getPayload().getString("positionY"))
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_CREATE_OBJECT, payload), board);
                break;
            case MSG_EDIT_OBJECT:
                // Generate event ASK_EDIT_OBJECT
                payload = Json.createBuilderFactory(null).createObjectBuilder().build();
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
                payload = Json.createBuilderFactory(null).createObjectBuilder().build();
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

    @OnClose
    public void onClose(final Session session, final CloseReason closeReason) {
        final String pseudo = (String) session.getUserProperties().get("username");
        final String board = (String) session.getUserProperties().get("board");

        if (!board.equals(NOT_IN_A_BOARD)) {
            final JsonObject payload = Json.createBuilderFactory(null)
                    .createObjectBuilder()
                    .add("pseudo", pseudo)
                    .add("board", board)
                    .add("isDisconnect", "true")
                    .build();
            EventManager.getInstance().fireEvent(new Event(EventType.ASK_LEAVE_BOARD, payload), board);
        } else {
            this.sessionsMap.get(NOT_IN_A_BOARD).remove(session);
        }


        final JsonObjectBuilder payloadBuilder = Json.createBuilderFactory(null).createObjectBuilder();
        if (pseudo != null) {
            payloadBuilder.add("pseudo", pseudo);
        } else {
            payloadBuilder.add("sessionId", session.getId());
        }
        final JsonObject payload = payloadBuilder.build();
        LOGGER.info("[" + NOT_IN_A_BOARD + "] user [" + pseudo + "] disconnected. (Close reason: " + closeReason.getReasonPhrase() + ")");
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_DISCONNECTED, payload), null);
    }


    private static Session getSession(final String pseudo) {
        // find the session of user
        boolean found = false;
        Session session = null;
        final Iterator<String> keys = sessionsMap.keySet().iterator();
        while (!found && keys.hasNext()) {
            final Iterator<Session> sessions = sessionsMap.get(keys.next()).iterator();
            while (!found && sessions.hasNext()) {
                final Session s = sessions.next();
                if (s.isOpen() && pseudo.equals((String) s.getUserProperties().get("username"))) {
                    session = s;
                    found = true;
                }
            }
        }
        return session;
    }

    public static void sendMessageToUser(final Message msg) {
        final String recipient = msg.getTo();

        // find recipient's session
        try {
            boolean recipientFound = false;
            final Iterator<String> keys = sessionsMap.keySet().iterator();
            while (!recipientFound && keys.hasNext()) {
                final Iterator<Session> sessions = sessionsMap.get(keys.next()).iterator();
                while (!recipientFound && sessions.hasNext()) {
                    final Session s = sessions.next();
                    if (s.isOpen() && s.getUserProperties().get("username").equals(recipient)) {
                        s.getBasicRemote().sendObject(msg);
                        recipientFound = true;
                        LOGGER.info("Server sent [" + msg.getType() + "] to User [" + recipient + "].");
                    }
                }
            }

            if (!recipientFound) {
                LOGGER.warning("[User-" + msg.getFrom() + "] wanted to send message to " + recipient + " But no use " + "was found with this name.");
            }
        } catch (final IOException | EncodeException e) {
            LOGGER.warning("SendMessageToUser [" + recipient + "] failed");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(e.getStackTrace().toString());
        }
    }

    public static void sendMessageToBoard(final String board, final Message msg) {
        try {
            if (sessionsMap.containsKey(board)) {
                for (final Session s : sessionsMap.get(board)) {
                    if (s.isOpen() && board.equals(s.getUserProperties().get("board"))) {
                        s.getBasicRemote().sendObject(msg);
                    }
                }
                LOGGER.info("Server sent [" + msg.getType() + "] to Board [" + board + "].");
            }
        } catch (final IOException | EncodeException e) {
            LOGGER.warning("SendMessageToBoard [" + board + "] failed");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(e.getStackTrace().toString());
        }
    }

    public static void sendMessageToSession(final Session session, final Message msg) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendObject(msg);
            }

            LOGGER.info("Server sent [" + msg.getType() + "] to Session [" + session.getId() + "].");

        } catch (final IOException | EncodeException e) {
            LOGGER.warning("SendMessageToSession [" + session.getId() + "] failed");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(e.getStackTrace().toString());
        }
    }


    public static void handleEventDrawerIdentified(final Event e) {
        final String sessionId = e.payload.getString("sessionId");
        final String pseudo = e.payload.getString("pseudo");

        Session session = null;
        boolean pseudoAlreadyInUse = false;
        final Iterator<String> iter1 = sessionsMap.keySet().iterator();
        while (iter1.hasNext()) {
            final Iterator<Session> iter2 = sessionsMap.get(iter1.next()).iterator();
            while (iter2.hasNext()) {
                final Session s = iter2.next();
                if (sessionId.equals(s.getId())) {
                    session = s;
                } else if (s.isOpen() && pseudo.equals((String) s.getUserProperties().get("username"))) {
                    pseudoAlreadyInUse = true;
                }
            }
        }

        if (!pseudoAlreadyInUse && session != null) {
            session.getUserProperties().put("username", pseudo);
            // Generate event CHAT_MESSAGE
            final JsonObject payload = Json.createBuilderFactory(null)
                    .createObjectBuilder()
                    .add("pseudo", pseudo)
                    .build();
        }

        final JsonObject p = Json.createBuilderFactory(null)
                .createObjectBuilder()
                .add("pseudoAvailable", !pseudoAlreadyInUse)
                .build();
        final Message answer = new Message(MessageType.MSG_IDENTITY_ANSWER.str, "server", "Unknown Yet", p);
        try {
            session.getBasicRemote().sendObject(answer);
        } catch (final IOException | EncodeException ex) {
            LOGGER.warning("Error in Identifying method.");
            LOGGER.warning(ex.getMessage());
            LOGGER.warning(ex.getStackTrace().toString());
        }
    }


    public static void handleEventDrawerJoinedBoard(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final String board = event.payload.getString("board");
        final JsonArray userlist = event.payload.getJsonArray("userlist");

        final Session session = getSession(pseudo);

        if (!session.equals(null)) {
            // Add the corresponding session to the set associated with it
            sessionsMap.get(NOT_IN_A_BOARD).remove(session);
            if (!sessionsMap.containsKey(board)) {
                sessionsMap.put(board, new HashSet<Session>());
            }
            if (!sessionsMap.get(board).contains(session)) {
                sessionsMap.get(board).add(session);
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

    public static void handleEventDrawerLeftBoard(final Event event) {
        final String pseudo = event.payload.getString("pseudo");
        final String board = event.payload.getString("board");

        final Session session = getSession(pseudo);

        if (!board.equals(NOT_IN_A_BOARD) && sessionsMap.containsKey(board)) {
            sessionsMap.get(board).remove(session);
        }
        if (!sessionsMap.get(NOT_IN_A_BOARD).contains(session) && !event.payload.containsKey("isDisconnect")) {
            sessionsMap.get(NOT_IN_A_BOARD).add(session);
        }

        // Broadcast a message with the updated list of users connected to the board
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArrayBuilder boardConnectedUsers = factory.createArrayBuilder();
        for (final Session s : sessionsMap.get(board)) {
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
        sendMessageToBoard(board, broadcast);
        sendMessageToSession(session, broadcast);
    }

    public static void handleEventChatMessage(final Event event) {
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
            sendMessageToBoard(board, broadcast);
        }
    }

    public static void handleObjectCreated(final Event event) {
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

    public static void handleEventObjectLocked(final Event event) {
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

    public static void handleEventObjectUnlocked(final Event event) {
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


}