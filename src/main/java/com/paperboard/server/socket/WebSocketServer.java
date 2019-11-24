package com.paperboard.server.socket;


import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;
import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.util.logging.Logger;

public class WebSocketServer implements Subscriber {

    private static WebSocketServer instance = null;
    private static String HOSTNAME = "localhost";
    private static String SOCKET_PORT_DEFAULT = "8081";
    private static String SOCKET_PORT = System.getenv("PORT");
    private static String SOCKET_API = "/websocket";
    private static Server server;
    private static final Logger LOGGER = Logger.getLogger(WebSocketServer.class.getName());

    public WebSocketServer() {
    }


    public static WebSocketServer getInstance() {
        if (instance == null) {
            instance = new WebSocketServer();
            instance.registerToEvent(EventType.DRAWER_IDENTIFIED);
            instance.registerToEvent(EventType.DRAWER_JOINED_BOARD);
            instance.registerToEvent(EventType.DRAWER_LEFT_BOARD);
            instance.registerToEvent(EventType.CHAT_MESSAGE);
            instance.registerToEvent(EventType.OBJECT_CREATED);
            instance.registerToEvent(EventType.OBJECT_LOCKED);
            instance.registerToEvent(EventType.OBJECT_UNLOCKED);
            instance.registerToEvent(EventType.OBJECT_EDITED);
        }
        return instance;
    }

    /*
    public static void main(final String[] args) {
        runServer();
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            LOGGER.info("[IMPORTANT INFO] To stop the server properly press any key.");
            reader.readLine();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            stopServer();
        }
    }
    */

    public static void runServer() {
        LOGGER.info("---> Starting WebSocket Server !");
        try {
            final int port = SOCKET_PORT == null
                    ? Integer.parseInt(SOCKET_PORT_DEFAULT.trim())
                    : Integer.parseInt(SOCKET_PORT.trim());
            server = new Server(HOSTNAME, port, SOCKET_API, WebSocketServerEndPoint.class);
            server.start();
            getInstance();
        } catch (final DeploymentException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        server.stop();
        LOGGER.info("---> WebSocket Server stopped");
    }

    @Override
    public void updateFromEvent(final Event e) {
        switch (e.type) {
            case DRAWER_IDENTIFIED:
                WebSocketServerEndPoint.handleEventDrawerIdentified(e);
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
                WebSocketServerEndPoint.handleObjectCreated(e);
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
        }
    }
}
