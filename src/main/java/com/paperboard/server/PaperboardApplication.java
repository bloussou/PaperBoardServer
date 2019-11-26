package com.paperboard.server;

import com.paperboard.server.error.PaperboardAlreadyExistException;
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

public class PaperboardApplication implements Subscriber {
    private static PaperboardApplication instance = null;
    private final HashMap<String, User> connectedUsers = new HashMap<>();
    private final HashSet<Paperboard> paperboards = new HashSet<>();
    private static Logger LOGGER = Logger.getLogger(PaperboardApplication.class.getName());

    private PaperboardApplication() {
    }


    public static PaperboardApplication getInstance() {
        if (instance == null) {
            instance = new PaperboardApplication();
            instance.registerToEvent(ASK_IDENTITY, DRAWER_DISCONNECTED);
        }
        return instance;
    }

    public static void main(final String[] args) {
        PaperboardApplication.getInstance();
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
     * @param paperboard the paperboard you want to add to the Set
     * @throws PaperboardAlreadyExistException The error triggered if you try to add two paperboards with the same
     *                                         title in the set
     */
    public static void addPaperboard(final Paperboard paperboard) throws PaperboardAlreadyExistException {
        if (getPaperboards().contains(paperboard)) {
            throw new PaperboardAlreadyExistException(paperboard);
        } else {
            getPaperboards().add(paperboard);
        }
    }

    /**
     * Method to add a user to the set of connectedUser
     *
     * @param pseudo the user you want to add to the Set
     * @throws UserAlreadyExistException The error triggered if you try to add two users with the same pseudo in the set
     */
    static void addUser(final String pseudo, final Event e) throws UserAlreadyExistException {
        final PaperboardApplication app = PaperboardApplication.getInstance();
        final JsonObjectBuilder payloadBuilder = Json.createObjectBuilder()
                .add("pseudo", e.payload.getString("pseudo"))
                .add("sessionId", e.payload.getString("sessionId"));
        if (app.getConnectedUsers().containsKey(pseudo)) {
            payloadBuilder.add("isAvailable", "false");
            throw new UserAlreadyExistException("User with pseudo " + pseudo + "already exists");
        } else {
            app.getConnectedUsers().put(pseudo, new User(pseudo));
            payloadBuilder.add("isAvailable", "true");
        }
        final JsonObject payload = payloadBuilder.build();
        EventManager.getInstance().fireEvent(new Event(DRAWER_IDENTIFICATION, payload), null);
    }

    /**
     * remove the user for a given pseudo
     *
     * @param pseudo String
     */
    static void disconnectUser(final String pseudo) {
        if (pseudo != null) {
            final PaperboardApplication app = PaperboardApplication.getInstance();
            app.getConnectedUsers().remove(pseudo);
        }
    }

    /**
     * Get a Paperboard using its title
     *
     * @param title String
     * @return Paperboard
     * @throws UserAlreadyExistException
     */
    public static Paperboard getPaperboard(final String title) {
        final Paperboard paperboard = new Paperboard(title);
        if (getPaperboards().contains(paperboard)) {
            for (final Paperboard obj : getPaperboards()) {
                if (obj.equals(paperboard))
                    return obj;
            }
        }
        return null;
    }

    static User getConnectedUser(final String pseudo) {
        return instance.connectedUsers.get(pseudo);
    }

    static HashMap<String, User> getConnectedUsers() {
        return instance.connectedUsers;
    }

    public static HashSet<Paperboard> getPaperboards() {
        return instance.paperboards;
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
