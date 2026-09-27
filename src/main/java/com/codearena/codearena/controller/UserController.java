package com.codearena.controller;

import com.codearena.codearena.dto.UserRequest;
import com.codearena.dto.UserRequest;
import com.codearena.entity.User;
import com.codearena.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
public User createUser(@RequestBody UserRequest request) {
    return userService.createUser(request);
}
}