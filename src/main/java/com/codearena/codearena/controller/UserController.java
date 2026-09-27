package com.codearena.controller;

import com.codearena.codearena.dto.LoginRequest;
import com.codearena.dto.UserRequest;
import com.codearena.entity.User;
import com.codearena.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.codearena.dto.LoginRequest;
import com.codearena.dto.LoginResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }

 @PostMapping("/login")
public LoginResponse login(
        @Valid @RequestBody LoginRequest request) {

    return userService.login(request);
}
}