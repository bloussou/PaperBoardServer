package com.paperboard.server.socket;


import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;
import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.util.logging.Logger;

/**
 * Runnable websocket server, it's a singleton pattern with instance
 */
public class WebSocketServer implements Subscriber {

    private static WebSocketServer instance = null;
    private static String HOSTNAME = "localhost";
    private static String SOCKET_PORT_DEFAULT = "8025";
    private static String SOCKET_PORT = System.getenv("PORT");
    private static String SOCKET_API = "/websocket";
    private static Server server;
    private static final Logger LOGGER = Logger.getLogger(WebSocketServer.class.getName());

    public WebSocketServer() {
    }


    /**
     * Register to the following needed events
     *
     * @return WebSocketServer singleton instance
     */
    public static WebSocketServer getInstance() {
        if (instance == null) {
            instance = new WebSocketServer();
            instance.registerToEvent(EventType.DRAWER_IDENTIFICATION,
                                     EventType.DRAWER_JOINED_BOARD,
                                     EventType.DRAWER_LEFT_BOARD,
                                     EventType.CHAT_MESSAGE,
                                     EventType.OBJECT_CREATED,
                                     EventType.OBJECT_LOCKED,
                                     EventType.OBJECT_UNLOCKED,
                                     EventType.OBJECT_EDITED,
                                     EventType.OBJECT_DELETED);
        }
        return instance;
    }

    /**
     * Start the websocketServer
     */
    public static void runServer() {
        LOGGER.info("---> Starting WebSocket Server !");
        try {
            final int port = SOCKET_PORT == null ? Integer.parseInt(SOCKET_PORT_DEFAULT.trim()) :
                             Integer.parseInt(SOCKET_PORT.trim());
            server = new Server(HOSTNAME, port, SOCKET_API, WebSocketServerEndPoint.class);
            server.start();
            getInstance();
        } catch (final DeploymentException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the websocketServer
     */
    public static void stopServer() {
        instance.unregisterFromAllEvent();
        server.stop();
        LOGGER.info("---> WebSocket Server stopped");
    }

    /**
     * See base class
     *
     * @param e Event
     */
    @Override
    public void updateFromEvent(final Event e) {
        switch (e.type) {
            case DRAWER_IDENTIFICATION:
                WebSocketServerEndPoint.handleEventDrawerIdentification(e);
                break;
            case DRAWER_JOINED_BOARD:
                WebSocketServerEndPoint.handleEventDrawerJoinedBoard(e);
                break;
            case DRAWER_LEFT_BOARD:
                WebSocketServerEndPoint.handleEventDrawerLeftBoard(e);
                break;
            case CHAT_MESSAGE:
                WebSocketServerEndPoint.handleEventChatMessage(e);
                break;
            case OBJECT_CREATED:
                WebSocketServerEndPoint.handleEventObjectCreated(e);
                break;
            case OBJECT_LOCKED:
                WebSocketServerEndPoint.handleEventObjectLocked(e);
                break;
            case OBJECT_UNLOCKED:
                WebSocketServerEndPoint.handleEventObjectUnlocked(e);
                break;
            case OBJECT_EDITED:
                WebSocketServerEndPoint.handleEventObjectEdited(e);
                break;
            case OBJECT_DELETED:
                WebSocketServerEndPoint.handleEventObjectDeleted(e);
                break;
            default:
                LOGGER.warning("ERROR Unhandled event in WebsocketServer triggered");
        }
    }
}
