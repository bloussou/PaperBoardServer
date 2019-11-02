package com.paperboard.server.socket;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(
        value = "/v1/paperboard/{board}",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class,
        configurator = WebSocketServerConfigurator.class)
public class SocketServerEndPoint {
    private static HashSet<Session> sessions = new HashSet<Session>();
    private Logger log = Logger.getLogger(getClass().getName());

    @OnOpen
    public void open(final Session session, @PathParam("board") final String board) {
        log.info("New user connected to board [" + board + "] !! userId:" + session.getId());
        session.getUserProperties().put("board", board);
        this.sessions.add(session);
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
        System.out.println("[" + board + "] Received something from " + session.getId());
        System.out.println("Message from " + session.getId() + ": " + message.toString());

        switch (MessageType.getEnum(message.getType())) {
            case JOIN_BOARD:
                System.out.println("Should call handleJoinBoardMsg");
                this.handleJoinBoard(session, message.getFrom());
                break;
            case LEAVE_BOARD:
                System.out.println("Should call handleLeaveBoard");
                this.handleLeaveBoard(session, message.getFrom());
                break;
            case CREATE_OBJECT:
                System.out.println("Should call handleCreateObject");
                break;
            case EDIT_OBJECT:
                System.out.println("Should call handleEditObject");
                break;
            case LOCK_OBJECT:
                System.out.println("Should call handleLockObject");
                break;
            case UNLOCK_OBJECT:
                System.out.println("Should call handleUnlockObject");
                break;
            case ASK_DELETION:
                System.out.println("Should call handleAskDeletion");
                break;
            case CHAT_MESSAGE:
                System.out.println("Should call handleChatMessage");
                this.handleChatMessage(message);
                break;
            default:
                System.out.println("Message Type Not Recognized !!");
                break;
        }
    }

    @OnClose
    public void onClose(final Session session, final CloseReason closeReason) {
        log.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }

    public void sendMessageToUser(final String username, final Message message) {
        try {
            for (final Session s : this.sessions) {
                if (s.isOpen()
                        && username.equals(s.getUserProperties().get("username"))) {
                    s.getBasicRemote().sendObject(message);
                    break;
                }
            }
        } catch (final IOException | EncodeException e) {
            log.log(Level.WARNING, "sendMessageToUser [" + username + "] failed", e);
        }
    }

    public void sendMessageToBoard(final String board, final Message msg) {
        try {
            for (final Session s : this.sessions) {
                if (s.isOpen()
                        && board.equals(s.getUserProperties().get("board"))) {
                    s.getBasicRemote().sendObject(msg);
                }
            }
        } catch (final IOException | EncodeException e) {
            log.log(Level.WARNING, "sendMessageToBoard [" + board + "] failed", e);
        }
    }

    public void handleJoinBoard(final Session session, final String newUser) {
        session.getUserProperties().put("username", newUser);
        final String board = (String) session.getUserProperties().get("board");

        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArrayBuilder users = factory.createArrayBuilder();
        for (final Session s : this.sessions) {
            final String username = (String) s.getUserProperties().get("username");
            if (s.isOpen() && username != null) {
                users.add(username);
            }
        }

        final JsonObject payload = Json.createBuilderFactory(null).createObjectBuilder()
                .add("username", newUser)
                .add("userlist", users)
                .build();

        final Message answer = new Message(MessageType.DRAWER_CONNECTED.str, "server", "all-board", payload);
        this.sendMessageToBoard(board, answer);
    }

    public void handleLeaveBoard(final Session session, final String oldUser) {
        session.getUserProperties().put("username", oldUser);
        final String board = (String) session.getUserProperties().get("board");

        final JsonBuilderFactory factory = Json.createBuilderFactory(null);
        final JsonArrayBuilder users = factory.createArrayBuilder();
        for (final Session s : this.sessions) {
            final String username = (String) s.getUserProperties().get("username");
            if (s.isOpen() && username != null && username != oldUser) {
                users.add(username);
            }
        }

        final JsonObject payload = Json.createBuilderFactory(null).createObjectBuilder()
                .add("username", oldUser)
                .add("userlist", users)
                .build();

        try {
            session.close();
        } catch (final IOException ex) {
            System.err.println("Could not close session properly with user " + oldUser);
        }
        final Message answer = new Message(MessageType.DRAWER_DISCONNECTED.str, "server", "all-board", payload);
        this.sendMessageToBoard(board, answer);
    }

    public void handleChatMessage(final Message msg) {
        this.sendMessageToUser(msg.getTo(), msg);
    }
}