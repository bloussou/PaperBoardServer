package com.paperboard.server.socket;

import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.EventType;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

@ServerEndpoint(
        value = "/{board}",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class,
        configurator = WebSocketServerConfigurator.class)
public class WebSocketServerEndPoint {

    private static HashMap<String, HashSet<Session>> sessionsMap = new HashMap<String, HashSet<Session>>();
    private static final Logger LOGGER = Logger.getLogger(WebSocketServerEndPoint.class.getName());

    @OnOpen
    public void open(final Session session, @PathParam("board") final String board) {
        session.getUserProperties().put("board", board);
        if (!this.sessionsMap.containsKey(board)) {
            this.sessionsMap.put(board, new HashSet<Session>());
        }
        this.sessionsMap.get(board).add(session);
        LOGGER.info("[Board-" + board + "] New user connected !! (id:" + session.getId() + ").");
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
        final String board = (String) session.getUserProperties().get("board");
        final String user = (String) session.getUserProperties().get("username");
        LOGGER.info("[" + board + "] Received [" + message.getType() + "] from [" + user + "].");

        switch (MessageType.getEnum(message.getType())) {
            case MSG_JOIN_BOARD:
                this.handleJoinBoard(session, message.getFrom());
                EventManager.getInstance().fireEvent(new Event(EventType.JOIN_BOARD, message), board);
                break;
            case MSG_LEAVE_BOARD:
                this.handleLeaveBoard(session, message.getFrom());
                EventManager.getInstance().fireEvent(new Event(EventType.LEAVE_BOARD, message), board);
                break;
            case MSG_CREATE_OBJECT:
                EventManager.getInstance().fireEvent(new Event(EventType.CREATE_OBJECT, message), board);
                break;
            case MSG_EDIT_OBJECT:
                EventManager.getInstance().fireEvent(new Event(EventType.EDIT_OBJECT, message), board);
                break;
            case MSG_LOCK_OBJECT:
                EventManager.getInstance().fireEvent(new Event(EventType.LOCK_OBJECT, message), board);
                break;
            case MSG_UNLOCK_OBJECT:
                EventManager.getInstance().fireEvent(new Event(EventType.UNLOCK_OBJECT, message), board);
                break;
            case MSG_DELETE_OBJECT:
                EventManager.getInstance().fireEvent(new Event(EventType.DELETE_OBJECT, message), board);
                break;
            case MSG_CHAT_MESSAGE:
                EventManager.getInstance().fireEvent(new Event(EventType.CHAT_MESSAGE, message), board);
                this.handleChatMessage(session, message);
                break;
            default:
                LOGGER.info("Message Type Unhandled: " + message.getType() + "!!");
                break;
        }
    }

    @OnError
    public void onError(final Session session, final Throwable t) {
        LOGGER.warning("SocketEndPointError: " + t.getMessage());
    }

    @OnClose
    public void onClose(final Session session, final CloseReason closeReason) {

        final String oldUser = (String) session.getUserProperties().get("username");
        final String board = (String) session.getUserProperties().get("board");

        if (oldUser != null) {
            final JsonBuilderFactory factory = Json.createBuilderFactory(null);
            final JsonArrayBuilder users = factory.createArrayBuilder();
            for (final Session s : this.sessionsMap.get(board)) {
                final String username = (String) s.getUserProperties().get("username");
                if (s.isOpen() && username != null && username != oldUser) {
                    users.add(username);
                }
            }

            final JsonObject payload = Json.createBuilderFactory(null).createObjectBuilder()
                    .add("username", oldUser)
                    .add("userlist", users)
                    .build();
            final Message msg = new Message(MessageType.MSG_DRAWER_DISCONNECTED.str, oldUser, "board members", payload);
            this.sendMessageToBoard(board, msg);
        }

        this.sessionsMap.get(board).remove(session);
        if (this.sessionsMap.get(board).size() == 0) {
            this.sessionsMap.remove(board);
        }
        EventManager.getInstance().fireEvent(new Event(EventType.DRAWER_DISCONNECTED), board);
        LOGGER.info("[Board-" + board + "] user [" + oldUser + "] disconnected.");
    }

    public void sendMessageToUser(final Session session, final Message msg) {
        final String username = msg.getTo();
        final String board = (String) session.getUserProperties().get("board");
        try {
            for (final Session s : this.sessionsMap.get(board)) {
                if (s.isOpen()
                        && username.equals(s.getUserProperties().get("username"))) {
                    s.getBasicRemote().sendObject(msg);
                    break;
                }
            }
            LOGGER.info("Server sent [" + msg.getType() + "] to User [" + username + "].");
        } catch (final IOException | EncodeException e) {
            LOGGER.warning("SendMessageToUser [" + username + "] failed");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(e.getStackTrace().toString());
        }
    }

    public void sendMessageToBoard(final String board, final Message msg) {
        try {
            for (final Session s : this.sessionsMap.get(board)) {
                if (s.isOpen()
                        && board.equals(s.getUserProperties().get("board"))) {
                    s.getBasicRemote().sendObject(msg);
                }
            }
            LOGGER.info("Server sent [" + msg.getType() + "] to Board [" + board + "].");
        } catch (final IOException | EncodeException e) {
            LOGGER.warning("SendMessageToBoard [" + board + "] failed");
            LOGGER.warning(e.getMessage());
            LOGGER.warning(e.getStackTrace().toString());
        }
    }

    public void handleJoinBoard(final Session session, final String newUser) {
        session.getUserProperties().put("username", newUser);
        final String board = (String) session.getUserProperties().get("board");

        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArrayBuilder boardUsers = factory.createArrayBuilder();
        for (final Session s : this.sessionsMap.get(board)) {
            final String username = (String) s.getUserProperties().get("username");
            if (s.isOpen() && username != null) {
                boardUsers.add(username);
            }
        }

        final JsonObject payload = Json.createBuilderFactory(null).createObjectBuilder()
                .add("username", newUser)
                .add("userlist", boardUsers)
                .build();

        LOGGER.info("[" + board + "] User [" + session.getUserProperties().get("username") + "] identified.");
        final Message answer = new Message(MessageType.MSG_DRAWER_CONNECTED.str, "server", "all board members", payload);
        this.sendMessageToBoard(board, answer);
    }

    public void handleLeaveBoard(final Session session, final String oldUser) {
        session.getUserProperties().put("username", oldUser);
        final String board = (String) session.getUserProperties().get("board");

        try {
            session.close();
        } catch (final IOException ex) {
            LOGGER.warning("Could not close session properly with user " + oldUser);
        }
    }

    public void handleChatMessage(final Session session, final Message msg) {
        this.sendMessageToUser(session, msg);
    }
}