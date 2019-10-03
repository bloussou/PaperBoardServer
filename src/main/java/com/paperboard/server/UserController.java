package com.paperboard.server;

import com.paperboard.Error.UserAlreadyExistException;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public void greeting(@RequestParam(value = "pseudo") String pseudo) {
        User user = new User(pseudo);
        ServerApplication.getInstance().addUser(user);
        System.out.println(user);
    }

    @ExceptionHandler({UserAlreadyExistException.class})
    public void handleException(RuntimeException e) {
        System.out.println("ERROR " + e.getMessage());
    }
}
