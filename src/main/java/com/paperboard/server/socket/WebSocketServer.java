package com.paperboard.server.socket;


import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class WebSocketServer {

    private static String HOSTNAME = "localhost";
    private static int SOCKET_PORT = 8025;
    private static String SOCKET_API = "/websocket";
    private static Server server;
    private static final Logger LOGGER = Logger.getLogger(WebSocketServer.class.getName());

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
        } catch (final DeploymentException e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        server.stop();
        LOGGER.info("---> WebSocket Server stopped");
    }
}
