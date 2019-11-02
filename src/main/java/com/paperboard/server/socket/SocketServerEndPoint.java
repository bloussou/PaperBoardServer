package com.paperboard.server.socket;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Logger;

@ServerEndpoint(
        value = "/v1/paperboard/{board}",
        encoders = MessageEncoder.class,
        decoders = MessageDecoder.class,
        configurator = WebSocketServerConfigurator.class)
public class SocketServerEndPoint {
    private Logger log = Logger.getLogger(getClass().getName());

    @OnOpen
    public void open(final Session session, @PathParam("board") final String board) {
        log.info("New user connected to board [" + board + "] !! userId:" + session.getId());
        session.getUserProperties().put("board", board);
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
                System.out.println("Should call handleJoinBoardMsg(message, session)");
                break;
            case LEAVE_BOARD:
                System.out.println("Should call handleLeaveBoard(message, session)");
                break;
            case CREATE_OBJECT:
                System.out.println("Should call handleCreateObject(message, session)");
                break;
            case EDIT_OBJECT:
                System.out.println("Should call handleEditObject(message, session)");
                break;
            case LOCK_OBJECT:
                System.out.println("Should call handleLockObject(message, session)");
                break;
            case UNLOCK_OBJECT:
                System.out.println("Should call handleUnlockObject(message, session)");
                break;
            case ASK_DELETION:
                System.out.println("Should call handleAskDeletion(message, session)");
                break;
            case CHAT_MESSAGE:
                System.out.println("Should call handleChatMessage(message, session)");
                break;
            default:
                System.out.println("Message Type Not Recognized !!");
                break;
        }

        /* A garder pour l'inspi pour un broadcast
        try {
            for (Session s : session.getOpenSessions()) {
                if (s.isOpen()
                        && board.equals(s.getUserProperties().get("board"))) {
                    s.getBasicRemote().sendObject(chatMessage);
                }
            }
        } catch (IOException | EncodeException e) {
            logger.log(Level.WARNING, "onMessage failed", e);
        }
        */
    }

    @OnClose
    public void onClose(final Session session, final CloseReason closeReason) {
        log.info(String.format("Session %s closed because of %s", session.getId(), closeReason));
    }

    // TODO
    public void sendMessageToUser(final String username) {

    }

    // TODO
    public void sendMessageToBoard(final String board) {
    }
}