package com.paperboard.server;

import com.paperboard.Error.PaperBoardAlreadyExistException;
import com.paperboard.Error.UserAlreadyExistException;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.Subscriber;
import com.paperboard.server.socket.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import static com.paperboard.server.events.EventType.*;

@SpringBootApplication
@ComponentScan(basePackages = "com.paperboard")
public class ServerApplication implements Subscriber {
    private static ServerApplication instance = null;
    private static ConfigurableApplicationContext ctx;
    private final HashMap<String, User> connectedUsers = new HashMap<>();
    private final HashSet<PaperBoard> paperBoards = new HashSet<>();
    private static Logger LOGGER = Logger.getLogger(ServerApplication.class.getName());

    public ServerApplication() {
    }


    public static ServerApplication getInstance() {
        if (instance == null) {
            instance = new ServerApplication();
            instance.registerToEvent(ASK_IDENTITY);
            instance.registerToEvent(DRAWER_DISCONNECTED);
        }
        return instance;
    }

    public static void runServer() {
        LOGGER.info("---> Starting Http Server !");
        ctx = SpringApplication.run(ServerApplication.class);
        // Initialize singleton
        getInstance();
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
        /*final PaperBoard pb = new PaperBoard("tableau de papier", Optional.of("rouge"), Optional.empty());
        pb.registerToEvent(EventType.JOIN_BOARD, pb.getTitle());*/
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
     * @throws PaperBoardAlreadyExistException The error triggered if you try to add two paperboards with the same
     * title in the set
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
     * @param pseudo the user you want to add to the Set
     * @throws UserAlreadyExistException The error triggered if you try to add two users with the same pseudo in the set
     */
    public static void addUser(final String pseudo, final Event e) throws UserAlreadyExistException {
        final ServerApplication server = ServerApplication.getInstance();
        if (server.getConnectedUsers().keySet().contains(pseudo)) {
            throw new UserAlreadyExistException(pseudo);
        } else {
            server.getConnectedUsers().put(pseudo, new User(pseudo));
            EventManager.getInstance().fireEvent(new Event(DRAWER_IDENTIFIED, e.payload), null);
        }
    }

    public static void disconnectUser(final String pseudo) {
        if (pseudo != null) {
            final ServerApplication server = ServerApplication.getInstance();
            server.getConnectedUsers().remove(pseudo);
        }
    }

    public static PaperBoard getPaperBoard(final String title) throws UserAlreadyExistException {
        final PaperBoard paperboard = new PaperBoard(title);
        final ServerApplication server = ServerApplication.getInstance();
        if (server.getPaperBoards().contains(paperboard)) {
            for (final PaperBoard obj : server.getPaperBoards()) {
                if (obj.equals(paperboard))
                    return obj;
            }
        }
        // TODO throw error
        throw new PaperBoardAlreadyExistException(paperboard);
    }

    public static String getBackgroundImagePath(final String boardName) {
        final ServerApplication server = ServerApplication.getInstance();
        return "todo";
//        return server.getBackgroundImage().get(boardName);
    }

    public static void addBackgroundImage(final String boardName, final String storePath) {
        final ServerApplication server = ServerApplication.getInstance();
//        server.getBackgroundImage().put(boardName, storePath);
    }

    public HashMap<String, User> getConnectedUsers() {
        return connectedUsers;
    }

    public HashSet<PaperBoard> getPaperBoards() {
        return paperBoards;
    }

    @Override
    public void updateFromEvent(final Event e) {
        System.out.println("Coucou identifié");
        switch (e.type) {
            case ASK_IDENTITY:
                addUser(e.payload.getString("pseudo"), e);
                break;
            case DRAWER_DISCONNECTED:
                disconnectUser(e.payload.containsKey("pseudo") ? e.payload.getString("pseudo") : null);
                break;
            default:
                System.out.println("Untracked event occurs in server");
        }
    }
}
