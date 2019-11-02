package com.paperboard.server.socket;


import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WebSocketServer {

    public static void main(final String[] args) {
        runServer();
    }

    public static void runServer() {
        final Server server = new Server("localhost", 8025, "/websockets", SocketServerEndPoint.class);

        try {
            server.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
    }
}
