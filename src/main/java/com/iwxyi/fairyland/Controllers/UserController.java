package com.iwxyi.fairyland.Controllers;

import com.iwxyi.fairyland.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    UserService userService;
    
    @RequestMapping(value = "/login/{username}/{password}", method = RequestMethod.GET)
    public boolean login(@PathVariable("username") String username, @PathVariable("password") String password) {
        return userService.login(username, password);
    }
}
