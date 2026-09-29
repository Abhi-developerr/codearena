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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Operation(
        summary = "Admin-only endpoint",
        description = "Accessible only to users with the ADMIN role."
)
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public String adminOnly() {

    return "Welcome Admin!";
}

    @PostMapping
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the provided details."
    )
    public User createUser(@Valid @RequestBody UserRequest request) {
        return userService.createUser(request);
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
@Operation(
        summary = "Change password",
        description = "Changes the password of the currently authenticated user."
)
@SecurityRequirement(name = "bearerAuth")
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

@PostMapping("/login")
@Operation(
        summary = "User login",
        description = "Authenticates a user and returns an access token and refresh token."
)
public LoginResponse login(
        @Valid @RequestBody LoginRequest request) {

    return userService.login(request);
}

@GetMapping("/profile")
@Operation(
        summary = "Get user profile",
        description = "Returns the profile information of the currently authenticated user."
)
@SecurityRequirement(name = "bearerAuth")
public String getProfile(
        Authentication authentication) {

    CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();

    User user = userDetails.getUser();

    return "Welcome, " + user.getName() + "! Your email is " + user.getEmail();
}
}