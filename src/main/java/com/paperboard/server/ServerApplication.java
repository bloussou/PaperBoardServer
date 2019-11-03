package com.paperboard.server;

import com.paperboard.Error.PaperBoardAlreadyExistException;
import com.paperboard.Error.UserAlreadyExistException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.HashSet;

@SpringBootApplication
@ComponentScan(basePackages = "com.paperboard")
public class ServerApplication {
    private static ServerApplication instance = null;
    private final HashSet<User> connectedUsers = new HashSet<>();
    private final HashSet<PaperBoard> paperBoards = new HashSet<>();

    public HashMap<String, String> getBackgroundImage() {
        return backgroundImage;
    }

    private final HashMap<String, String> backgroundImage = new HashMap<>();

    public ServerApplication() {

    }


    public static ServerApplication getInstance() {
        if (instance == null) {
            instance = new ServerApplication();
        }
        return instance;
    }

    public static void main(final String[] args) {
        SpringApplication.run(ServerApplication.class, args);
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
        return server.getBackgroundImage().get(boardName);
    }

    public static void addBackgroundImage(final String boardName, final String storePath) {
        final ServerApplication server = ServerApplication.getInstance();
        server.getBackgroundImage().put(boardName, storePath);
    }

    public HashSet<User> getConnectedUsers() {
        return connectedUsers;
    }

    public HashSet<PaperBoard> getPaperBoards() {
        return paperBoards;
    }
}
