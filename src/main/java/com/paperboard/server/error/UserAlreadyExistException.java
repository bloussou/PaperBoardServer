package com.paperboard.server.error;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(final String pseudo) {
        super("User : " + pseudo + " already exists");
    }
}