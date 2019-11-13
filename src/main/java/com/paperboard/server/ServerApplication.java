package com.paperboard.server;

import com.paperboard.Error.PaperBoardAlreadyExistException;
import com.paperboard.Error.UserAlreadyExistException;
import com.paperboard.server.events.EventType;
import com.paperboard.server.socket.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;

@SpringBootApplication
@ComponentScan(basePackages = "com.paperboard")
public class ServerApplication {
    private static ServerApplication instance = null;
    private static ConfigurableApplicationContext ctx;
    private final HashSet<User> connectedUsers = new HashSet<>();
    private final HashSet<PaperBoard> paperBoards = new HashSet<>();

    public ServerApplication() {
    }

    public static ServerApplication getInstance() {
        if (instance == null) {
            instance = new ServerApplication();
        }
        return instance;
    }

    public static void runServer() {
        System.out.println("---> Starting Http Server !");
        ctx = SpringApplication.run(ServerApplication.class);
    }

    public static void stopServer() {
        final int exitCode = SpringApplication.exit(ctx, () -> {
            // no errors
            return 0;
        });
        System.exit(exitCode);
        System.out.println("---> Http Server stopped");
    }

    public static void main(final String[] args) {
        final PaperBoard pb = new PaperBoard("tableau de papier", "rouge");
        ServerApplication.runServer();
        WebSocketServer.runServer();
        pb.registerToEvent(EventType.JOIN_BOARD);
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Please press a key to stop the server.");
            reader.readLine();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            ServerApplication.stopServer();
            WebSocketServer.stopServer();
        }
    }

    /**
     * For test purpose and maybe other feature, this function clear the two Set connectedUsers and paperBoards
     */
    public static void clearData() {
        final ServerApplication server = ServerApplication.getInstance();
        server.getPaperBoards().clear();
        server.getConnectedUsers().clear();
    }

    /**
     * Method to add a paperboard to the set of paperboards
     *
     * @param paperBoard the paperboard you want to add to the Set
     * @throws PaperBoardAlreadyExistException The error triggered if you try to add two paperboards with the same title in the set
     */
    public static void addPaperBoard(final PaperBoard paperBoard) throws PaperBoardAlreadyExistException {
        final ServerApplication server = ServerApplication.getInstance();
        if (server.getPaperBoards().contains(paperBoard)) {
            throw new PaperBoardAlreadyExistException(paperBoard);
        } else {
            server.getPaperBoards().add(paperBoard);
        }
    }

    /**
     * Method to add a user to the set of connectedUser
     *
     * @param user the user you want to add to the Set
     * @throws UserAlreadyExistException The error triggered if you try to add two users with the same pseudo in the set
     */
    public static void addUser(final User user) throws UserAlreadyExistException {
        final ServerApplication server = ServerApplication.getInstance();
        if (server.getConnectedUsers().contains(user)) {
            throw new UserAlreadyExistException(user);
        } else {
            server.getConnectedUsers().add(user);
        }
    }


    public HashSet<User> getConnectedUsers() {
        return connectedUsers;
    }

    public HashSet<PaperBoard> getPaperBoards() {
        return paperBoards;
    }
}
