package com.paperboard.server.socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketThread extends Thread {

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;

    public SocketThread(final Socket socket) throws IOException {
        if (socket == null)
            throw new NullPointerException("The socket cannot be null");
        this.socket = socket;
        this.input = new ObjectInputStream(socket.getInputStream());
        this.output = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Connection set with " + this.socket + " !");
    }

    private void error(final String message) {
        System.out.println(message);
        try {
            this.input.close();
            this.output.close();
            this.socket.close();
        } catch (final Exception e) {
            System.out.println("Error on socket connection close");
        }
    }

    public void run() {

        System.out.println("[" + Thread.currentThread().getName() + "][" + this.socket.getInetAddress().getHostAddress() + "] New socket connection launched.");

        Boolean done = false;
        while (!done) {
            try {
                final SocketMessage message = (SocketMessage) this.input.readObject();
                System.out.println("[" + Thread.currentThread().getName() + "] " + "Received " + message.getText());

                if (message.getType().equals(SocketConstants.TYPE_LEAVE_BOARD)) {
                    done = true;
                }
            } catch (final EOFException e) {
                // it just means that nothing is received yet in the inputObjectStream
            } catch (final IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        try {
            this.output.close();
            this.input.close();
            this.socket.close();
            System.out.println("[" + Thread.currentThread().getName() + "] - Connection closed.");
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
