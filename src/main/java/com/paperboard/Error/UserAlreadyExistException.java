package com.paperboard.Error;

import com.paperboard.server.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exists")
public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(final User user) {
        super("User : " + user.toString() + " already exists");
    }
}
