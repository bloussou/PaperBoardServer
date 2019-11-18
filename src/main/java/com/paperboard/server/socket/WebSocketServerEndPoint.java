package com.paperboard.server.socket;

import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.EventType;
import org.springframework.util.ConcurrentReferenceHashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

@ServerEndpoint(
        value = "/v1/paperboard",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class,
        configurator = WebSocketServerConfigurator.class)
public class WebSocketServerEndPoint {

    public static String NOT_IN_A_BOARD = "-Not In a Board-";
    private static ConcurrentReferenceHashMap<String, HashSet<Session>> sessionsMap = new ConcurrentReferenceHashMap<>();
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
        final JsonObject payload = Json.createBuilderFactory(null).createObjectBuilder()
                .add("sessionId", session.getId())
                .build();
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_CONNECTED, payload), null);
    }

    /**
     * When you send something through a socket message, your message should be a json formatted string like
     * string s = '{"from": "Ludo", "to": "Brieuc", "type": "Edit Object", "payload":{"shapeType": "rectangle", "shapeId": "rect-0121", "color": "blue"}}'
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
                // Generate event DRAWER_CONNECTED
                payload = Json.createBuilderFactory(null).createObjectBuilder()
                        .add("pseudo", message.getPayload().getString("pseudo"))
                        .add("sessionId", session.getId())
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_IDENTITY, payload), null);
                break;
            case MSG_JOIN_BOARD:
                // Generate event ASK_JOIN_BOARD
                payload = Json.createBuilderFactory(null).createObjectBuilder()
                        .add("pseudo", (String) session.getUserProperties().get("username"))
                        .add("board", message.getPayload().getString("board"))
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_JOIN_BOARD, payload), message.getPayload().getString("board"));
                break;
            case MSG_LEAVE_BOARD:
                // Generate event ASK_LEAVE_BOARD
                payload = Json.createBuilderFactory(null).createObjectBuilder()
                        .add("pseudo", (String) session.getUserProperties().get("username"))
                        .add("board", message.getPayload().getString("board"))
                        .build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_LEAVE_BOARD, payload), message.getPayload().getString("board"));
                break;
            case MSG_CREATE_OBJECT:
                // Generate event ASK_CREATE_OBJECT
                payload = Json.createBuilderFactory(null).createObjectBuilder().build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_CREATE_OBJECT, payload), board);
                break;
            case MSG_EDIT_OBJECT:
                // Generate event ASK_EDIT_OBJECT
                payload = Json.createBuilderFactory(null).createObjectBuilder().build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_EDIT_OBJECT, payload), board);
                break;
            case MSG_LOCK_OBJECT:
                // Generate event ASK_LOCK_OBJECT
                payload = Json.createBuilderFactory(null).createObjectBuilder().build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_LOCK_OBJECT, payload), board);
                break;
            case MSG_UNLOCK_OBJECT:
                // Generate event ASK_UNLOCK_OBJECT
                payload = Json.createBuilderFactory(null).createObjectBuilder().build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_UNLOCK_OBJECT, payload), board);
                break;
            case MSG_DELETE_OBJECT:
                // Generate event ASK_DELETE_OBJECT
                payload = Json.createBuilderFactory(null).createObjectBuilder().build();
                EventManager.getInstance().fireEvent(new Event(EventType.ASK_DELETE_OBJECT, payload), board);
                break;
            case MSG_CHAT_MESSAGE:
                // Generate event CHAT_MESSAGE
                payload = Json.createBuilderFactory(null).createObjectBuilder().build();
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
        final String oldUser = (String) session.getUserProperties().get("username");
        final String board = (String) session.getUserProperties().get("board");

        if (this.sessionsMap.containsKey(board)) {
            if (this.sessionsMap.get(board).contains(session)) {
                this.handleMsgLeaveBoard(session);
            }
            if (this.sessionsMap.get(board).size() == 0) {
                this.sessionsMap.remove(board);
            }
        }
        this.sessionsMap.get(NOT_IN_A_BOARD).remove(session);
        final JsonObject payload = Json.createBuilderFactory(null).createObjectBuilder()
                .add("pseudo", oldUser)
                .build();
        LOGGER.info("[" + NOT_IN_A_BOARD + "] user [" + oldUser + "] disconnected.");
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_DISCONNECTED, payload), null);
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
                LOGGER.warning("[User-" + msg.getFrom() + "] wanted to send message to " + recipient + " But no use was found with this name.");
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
                    if (s.isOpen()
                            && board.equals(s.getUserProperties().get("board"))) {
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


    public void handleMsgIdentify(final Session session, final Message msg) {
        final String pseudo = msg.getPayload().getString("pseudo");
        boolean pseudoAlreadyInUse = false;
        final Iterator<String> iter1 = this.sessionsMap.keySet().iterator();
        while (!pseudoAlreadyInUse && iter1.hasNext()) {
            final Iterator<Session> iter2 = this.sessionsMap.get(iter1.next()).iterator();
            while (!pseudoAlreadyInUse && iter2.hasNext()) {
                final Session s = iter2.next();
                if (s.isOpen() && !s.equals(session) && pseudo.equals((String) s.getUserProperties().get("username"))) {
                    pseudoAlreadyInUse = true;
                }
            }
        }

        final JsonObject payload = Json.createBuilderFactory(null).createObjectBuilder()
                .add("pseudoAvailable", !pseudoAlreadyInUse)
                .build();
        final Message message = new Message(MessageType.MSG_IDENTITY_ANSWER.str, "server", "Unknown Yet", payload);

        try {
            session.getBasicRemote().sendObject(message);
            if (!pseudoAlreadyInUse) {
                session.getUserProperties().put("username", pseudo);
                EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_IDENTIFIED, msg), null);
            }
        } catch (final IOException | EncodeException e) {
            LOGGER.warning("Send Pseudo Request Answer failed");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(e.getStackTrace().toString());
        }
    }

    public static void handleEventJoinedBoard(final Event event) {
        final String user = event.message.getPayload().getString("joiner");
        final String board = event.message.getPayload().getString("board");

        // find the session of user
        boolean found = false;
        Session session = null;
        final Iterator<String> keys = sessionsMap.keySet().iterator();
        while (keys.hasNext()) {
            final Iterator<Session> sessions = sessionsMap.get(keys.next()).iterator();
            while (!found && sessions.hasNext()) {
                final Session s = sessions.next();
                if (s.isOpen() && user.equals((String) s.getUserProperties().get("username"))) {
                    session = s;
                    found = true;
                }
            }
        }

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

            final Message broadcast = new Message(MessageType.MSG_DRAWER_JOINED_BOARD.str, "server", "all board members", event.message.getPayload());
            sendMessageToBoard(board, broadcast);
        } else {
            LOGGER.warning("Drawer joined the board" + board + " but no corresponding socket session was found...");
        }

    }

    public void handleMsgLeaveBoard(final Session session) {
        final String user = (String) session.getUserProperties().get("username");
        final String board = (String) session.getUserProperties().get("board");

        if (!board.equals(NOT_IN_A_BOARD) && this.sessionsMap.containsKey(board)) {
            this.sessionsMap.get(board).remove(session);
        }
        if (!this.sessionsMap.get(NOT_IN_A_BOARD).contains(session)) {
            this.sessionsMap.get(NOT_IN_A_BOARD).add(session);
        }

        // Broadcast a message with the updated list of users connected to the board
        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArrayBuilder boardConnectedUsers = factory.createArrayBuilder();
        for (final Session s : this.sessionsMap.get(board)) {
            final String username = (String) s.getUserProperties().get("username");
            if (s.isOpen() && username != null) {
                boardConnectedUsers.add(username);
            }
        }
        final JsonObject payload = Json.createBuilderFactory(null).createObjectBuilder()
                .add("leaver", user)
                .add("userlist", boardConnectedUsers)
                .build();

        LOGGER.info("[Board-" + board + "] " + user + " left the board (" + boardConnectedUsers.toString() + ".");
        final Message broadcast = new Message(MessageType.MSG_DRAWER_LEFT_BOARD.str, "server", "all board members", payload);
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_LEFT_BOARD, broadcast), board);
        this.sendMessageToBoard(board, broadcast);
    }

    public void handleChatMessage(final Session session, final Message msg) {
        final String board = (String) session.getUserProperties().get("board");

        if (!board.equals(NOT_IN_A_BOARD)) {
            this.sendMessageToBoard(board, msg);
        }
    }
}