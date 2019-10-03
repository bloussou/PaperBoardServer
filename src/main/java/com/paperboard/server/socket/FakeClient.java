package com.paperboard.server.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FakeClient extends Thread {

    private Socket client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private static AtomicLong botCount = new AtomicLong(0);
    private String name;
    private String serverAddr;
    private Integer serverPort;

    public FakeClient(final String address, final Integer port) {
        this.name = "BOT" + botCount.getAndIncrement();
        this.serverAddr = address;
        this.serverPort = port;
    }

    public FakeClient(final String address, final Integer port, final String clientName) {
        this.name = name;
        this.serverAddr = address;
        this.serverPort = port;
    }

    public void sendMessage(final String type, final String content) throws IOException {
        if (!this.client.isClosed()) {
            System.out.println("[" + this.name + "] : Send [" + type + "] <" + content + ">");
            final SocketMessage message1 = new SocketMessage("A", "[" + this.name + "]: " + content);
            this.output.writeObject(message1);
        }
    }

    public void run() {

        final Random random = new Random();

        try {
            // Connection to the socket server
            Thread.sleep(random.nextInt(10) * 1000);
            System.out.println("[" + this.name + "] - Trying to connect...");
            this.client = new Socket(this.serverAddr, this.serverPort);
            this.output = new ObjectOutputStream(this.client.getOutputStream());
            this.input = new ObjectInputStream(this.client.getInputStream());
            System.out.println("[" + this.name + "] - Connected to socketServer (" + this.client.getRemoteSocketAddress() + " !");

            // Join Board
            Thread.sleep(random.nextInt(10) * 1000);
            this.sendMessage(SocketConstants.TYPE_JOIN_BOARD, "I want to join a board");

            // Draw something
            Thread.sleep(random.nextInt(10) * 1000);
            this.sendMessage(SocketConstants.TYPE_DRAW, "I draw something");

            // Move a drawing
            Thread.sleep(random.nextInt(10) * 1000);
            this.sendMessage(SocketConstants.TYPE_MOVE, "Move a drawing");

            // Leave the board
            Thread.sleep(random.nextInt(10) * 1000);
            this.sendMessage(SocketConstants.TYPE_LEAVE_BOARD, "I leave the board");

        } catch (final InterruptedException e) {
            e.printStackTrace();
        } catch (final UnknownHostException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (this.client != null) {
                try {
                    Thread.sleep(random.nextInt(10) * 1000);
                    this.client.close();
                    System.out.println("[" + this.name + "] - Closed socket connection.");
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
