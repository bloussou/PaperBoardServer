package com.paperboard.Error;

import com.paperboard.server.User;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(User user) {
        super(user.toString() + " already exists");
    }
}
