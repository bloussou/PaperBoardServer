package com.paperboard.server.socket;


import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventType;
import com.paperboard.server.events.Subscriber;
import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class WebSocketServer implements Subscriber {

    private static WebSocketServer instance = null;
    private static String HOSTNAME = "localhost";
    private static int SOCKET_PORT = 8025;
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
        }
        return instance;
    }

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

    public static void runServer() {
        LOGGER.info("---> Starting WebSocket Server !");
        server = new Server(HOSTNAME, SOCKET_PORT, SOCKET_API, WebSocketServerEndPoint.class);
        try {
            server.start();
            getInstance();
        } catch (final DeploymentException e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        server.stop();
        LOGGER.info("---> WebSocket Server stopped");
    }

    @Override
    public void updateFromEvent(final Event e) {
        System.out.println("Coucou identifié");
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
        }
    }
}
