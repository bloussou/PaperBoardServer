package com.paperboard.server;

import com.paperboard.Error.UserAlreadyExistException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;

@SpringBootApplication
public class ServerApplication {
    private static ServerApplication instance = null;
    private HashSet<User> connectedUsers = new HashSet<User>();

    public ServerApplication() {

    }

    public static ServerApplication getInstance() {
        if (instance == null) {
            instance = new ServerApplication();
        }
        return instance;
    }

    public static void main(String[] args) {
        User user1 = new User("Regis");
        User user2 = new User("Robert");
        SpringApplication.run(ServerApplication.class, args);
    }

    public void addUser(User user) {
        if (connectedUsers.contains(user)) {
            throw new UserAlreadyExistException(user);
        } else {
            connectedUsers.add(user);
        }
    }

}
