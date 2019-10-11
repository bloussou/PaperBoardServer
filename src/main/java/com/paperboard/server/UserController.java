package com.paperboard.server;

import com.paperboard.Error.UserAlreadyExistException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to manage the user HTTP requests
 */
@RestController
public class UserController {

    /**
     * Receive the request on /user?pseudo=... and if possible add the user the Set of connected User in the ServerApplication
     *
     * @param pseudo pseudo of the new user.
     * @throws UserAlreadyExistException if there is already a connected user with the same name
     */
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User createUser(@RequestParam(value = "pseudo") final String pseudo) throws UserAlreadyExistException {
        final User user = new User(pseudo);
        ServerApplication.addUser(user);
        return user;
    }
}
