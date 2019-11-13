package com.paperboard.server.socket;


import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WebSocketServer {

    private static Server server;

    public static void main(final String[] args) {
        runServer();
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            stopServer();
        }
    }

    public static void runServer() {
        System.out.println("---> Starting WebSocket Server !");
        server = new Server("localhost", 8025, "/websockets", SocketServerEndPoint.class);
        try {
            server.start();
        } catch (final DeploymentException e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        server.stop();
        System.out.println("---> WebSocket Server stopped");
    }
}
