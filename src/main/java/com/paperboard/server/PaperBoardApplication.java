package com.paperboard.server;

import com.paperboard.server.error.PaperBoardAlreadyExistException;
import com.paperboard.server.error.UserAlreadyExistException;
import com.paperboard.server.events.Event;
import com.paperboard.server.events.EventManager;
import com.paperboard.server.events.Subscriber;
import com.paperboard.server.socket.WebSocketServer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import static com.paperboard.server.events.EventType.*;

public class PaperBoardApplication implements Subscriber {
    private static PaperBoardApplication instance = null;
    private final HashMap<String, User> connectedUsers = new HashMap<>();
    private final HashSet<PaperBoard> paperBoards = new HashSet<>();
    private static Logger LOGGER = Logger.getLogger(PaperBoardApplication.class.getName());

    private PaperBoardApplication() {
    }


    private static PaperBoardApplication getInstance() {
        if (instance == null) {
            instance = new PaperBoardApplication();
            instance.registerToEvent(ASK_IDENTITY, DRAWER_DISCONNECTED);
        }
        return instance;
    }

    public static void main(final String[] args) {
        PaperBoardApplication.getInstance();
        WebSocketServer.runServer();
        try {
            LOGGER.info("[IMPORTANT INFO] ... Server running until it crashes ...");
            while (true) {
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            instance.unregisterFromAllEvent();
            WebSocketServer.stopServer();
        }
    }

    /**
     * Method to add a paperboard to the set of paperboards
     *
     * @param paperBoard the paperboard you want to add to the Set
     * @throws PaperBoardAlreadyExistException The error triggered if you try to add two paperboards with the same
     *                                         title in the set
     */
    public static void addPaperBoard(final PaperBoard paperBoard) throws PaperBoardAlreadyExistException {
        if (getPaperBoards().contains(paperBoard)) {
            throw new PaperBoardAlreadyExistException(paperBoard);
        } else {
            getPaperBoards().add(paperBoard);
        }
    }

    /**
     * Method to add a user to the set of connectedUser
     *
     * @param pseudo the user you want to add to the Set
     * @throws UserAlreadyExistException The error triggered if you try to add two users with the same pseudo in the set
     */
    private static void addUser(final String pseudo, final Event e) throws UserAlreadyExistException {
        final PaperBoardApplication app = PaperBoardApplication.getInstance();
        final JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
                .add("pseudo", e.payload.getString("pseudo"))
                .add("sessionId", e.payload.getString("sessionId"));
        if (app.getConnectedUsers().containsKey(pseudo)) {
            payloadBuilder.add("isAvailable", "false");
        } else {
            app.getConnectedUsers().put(pseudo, new User(pseudo));
            payloadBuilder.add("isAvailable", "true");
        }
        final JsonObject payload = payloadBuilder.build();
        EventManager.getInstance().fireEvent(new Event(DRAWER_IDENTIFICATION, payload), null);
    }

    private static void disconnectUser(final String pseudo) {
        if (pseudo != null) {
            final PaperBoardApplication app = PaperBoardApplication.getInstance();
            app.getConnectedUsers().remove(pseudo);
        }
    }

    public static PaperBoard getPaperBoard(final String title) throws UserAlreadyExistException {
        final PaperBoard paperboard = new PaperBoard(title);
        if (getPaperBoards().contains(paperboard)) {
            for (final PaperBoard obj : getPaperBoards()) {
                if (obj.equals(paperboard))
                    return obj;
            }
        }
        // TODO throw error
        throw new PaperBoardAlreadyExistException(paperboard);
    }

    static User getConnectedUser(final String pseudo) {
        return instance.connectedUsers.get(pseudo);
    }

    private HashMap<String, User> getConnectedUsers() {
        return connectedUsers;
    }

    public static HashSet<PaperBoard> getPaperBoards() {
        return instance.paperBoards;
    }

    @Override
    public void updateFromEvent(final Event e) {
        switch (e.type) {
            case ASK_IDENTITY:
                assert e.payload != null;
                addUser(e.payload.getString("pseudo"), e);
                break;
            case DRAWER_DISCONNECTED:
                assert e.payload != null;
                disconnectUser(e.payload.containsKey("pseudo") ? e.payload.getString("pseudo") : null);
                break;
            default:
                System.out.println("Untracked event occurs in server");
        }
    }
}
