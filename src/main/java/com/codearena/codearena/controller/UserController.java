package com.codearena.codearena.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codearena.codearena.dto.ChangePasswordRequest;
import com.codearena.codearena.dto.LoginRequest;
import com.codearena.codearena.dto.LoginResponse;
import com.codearena.codearena.dto.RefreshResponse;
import com.codearena.codearena.dto.RefreshTokenRequest;
import com.codearena.codearena.dto.UserRequest;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.security.CustomUserDetails;
import com.codearena.codearena.service.RefreshTokenService;
import com.codearena.codearena.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public UserController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public String adminOnly() {
    return "Welcome Admin!";
}

    @PostMapping
    public User createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
    }

 @PostMapping("/login")
public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return userService.login(request);
}

@PostMapping("/refresh")
public RefreshResponse refreshToken(
        @Valid @RequestBody RefreshTokenRequest request) {

    return refreshTokenService
            .refreshAccessToken(
                    request.getRefreshToken()
            );
}

@PutMapping("/change-password")
public String changePassword(
        @Valid @RequestBody ChangePasswordRequest request,
        Authentication authentication) {

    CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();

    Long userId =
            userDetails.getUser().getId();

    userService.changePassword(
            userId,
            request
    );

    return "Password changed successfully";
}

@PostMapping("/logout")
public Map<String, String> logout(
        @Valid @RequestBody RefreshTokenRequest request) {

    refreshTokenService.deleteByToken(
            request.getRefreshToken()
    );

    return Map.of(
            "message",
            "Logged out successfully"
    );
}

}