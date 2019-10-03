package com.paperboard.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class SocketServerManager extends Thread {

    private ServerSocket socketServer;
    private boolean keepRunning;

    public SocketServerManager(final int port) throws IOException {
        socketServer = new ServerSocket(port);
        keepRunning = true;
    }

    public void stoplistening() throws IOException {
        keepRunning = false;
        if (!socketServer.isClosed()) socketServer.close();
    }

    public void run() {
        while (keepRunning) {
            try {
                System.out.println("Waiting for next client on port " + socketServer.getLocalPort() + "...");
                final Socket socket = socketServer.accept();
                System.out.println("New user connected !! Launching new SocketThread...");
                final SocketThread th = new SocketThread(socket);
                th.start();
            } catch (final SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (final SocketException s) {
                //on est ici car on a utilise stoplistening()
                System.out.println("SocketException !!!");
                break;
            } catch (final IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(final String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        System.out.println("================ SCENARIO START ================");
        // Start the server
        final Integer port = 4000;
        final SocketServerManager socketServerManager = new SocketServerManager(port);
        socketServerManager.start();

        // Wait a second before starting test scenario
        Thread.sleep(1000);

        // Simulate Clients connections
        final ArrayList<Thread> clients = new ArrayList<>();

        final Thread client1 = new Thread(new FakeClient("localhost", port));
        clients.add(client1);
        client1.start();

        Thread.sleep(3000);
        final Thread client2 = new Thread(new FakeClient("localhost", port));
        clients.add(client2);
        client2.start();


        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).join();
        }
        Thread.sleep(2000);

        System.out.println("================ SCENARIO END ================");
        return;
    }


}
