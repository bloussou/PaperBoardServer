package com.paperboard.server.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exists")
public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(final String pseudo) {
        super("User : " + pseudo + " already exists");
    }
}