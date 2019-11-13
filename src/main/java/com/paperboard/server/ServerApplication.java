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
import java.util.logging.Logger;

@SpringBootApplication
@ComponentScan(basePackages = "com.paperboard")
public class ServerApplication {
    private static ServerApplication instance = null;
    private static ConfigurableApplicationContext ctx;
    private final HashSet<User> connectedUsers = new HashSet<>();
    private final HashSet<PaperBoard> paperBoards = new HashSet<>();
    private static Logger LOGGER = Logger.getLogger(ServerApplication.class.getName());

    public ServerApplication() {
    }

    public static ServerApplication getInstance() {
        if (instance == null) {
            instance = new ServerApplication();
        }
        return instance;
    }

    public static void runServer() {
        LOGGER.info("---> Starting Http Server !");
        ctx = SpringApplication.run(ServerApplication.class);
    }

    public static void stopServer() {
        final int exitCode = SpringApplication.exit(ctx, () -> {
            // no errors
            return 0;
        });
        System.exit(exitCode);
        LOGGER.info("---> Http Server stopped");
    }

    public static void main(final String[] args) {
        ServerApplication.runServer();
        WebSocketServer.runServer();

        /**
         * EXAMPLE OF SUBSCRIPTION (SUBSCRIBER/PUBLISHER PATTERN)
         * Here the object 'pb' subscribes to the event JOIN_BOARD (a user joins a board).
         * 1) It has to implement the Suscriber interface with a specific 'updateFromEvent(Event e)' method
         * which is triggered anytime such event is fired.
         * 2) The JOIN_BOARD event is fired by the SocketEndPoint component when it receives a message from any client
         * with type MSG_JOIN_BOARD.
         * 3) The event fired contains the Msg with all the needed information for the subscribers
         * to process there updateFromEvent
         */
        final PaperBoard pb = new PaperBoard("tableau de papier", "rouge");
        pb.registerToEvent(EventType.JOIN_BOARD, pb.getTitle());
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            LOGGER.info("[IMPORTANT INFO] To stop the Http server and the WebSocket server properly press any key.");
            reader.readLine();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            WebSocketServer.stopServer();
            ServerApplication.stopServer();
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
