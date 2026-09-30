package com.codearena.codearena.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codearena.codearena.dto.ChangePasswordRequest;
import com.codearena.codearena.dto.LoginRequest;
import com.codearena.codearena.dto.LoginResponse;
import com.codearena.codearena.dto.UserRequest;
import com.codearena.codearena.dto.UserResponse;
import com.codearena.codearena.entity.Role;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.exception.EmailAlreadyExistsException;
import com.codearena.codearena.exception.InvalidCredentialsException;
import com.codearena.codearena.exception.InvalidPasswordException;
import com.codearena.codearena.exception.UserNotFoundException;
import com.codearena.codearena.repository.UserRepository;
import com.codearena.codearena.security.CustomUserDetails;
import com.codearena.codearena.security.GeneratedRefreshToken;
import com.codearena.codearena.security.JwtService;

import io.jsonwebtoken.JwtException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    public UserResponse createUser(UserRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        if (userRepository.existsByEmail(email)) {

            throw new EmailAlreadyExistsException(
                    "Email already exists"
            );
        }

        User user = new User();

        user.setName(
                request.getName()
        );

        user.setEmail(
                email
        );

        String hashedPassword =
                passwordEncoder.encode(
                        request.getPassword()
                );

        user.setPassword(
                hashedPassword
        );

        user.setRole(
                Role.USER
        );

        User savedUser =
                userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

    public LoginResponse login(LoginRequest request) {

        try {

            String email =
                    request.getEmail()
                            .trim()
                            .toLowerCase();

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    request.getPassword()
                            )
                    );

            CustomUserDetails userDetails =
                    (CustomUserDetails)
                            authentication.getPrincipal();

            User user =
                    userDetails.getUser();

            String accessToken =
                    jwtService.generateToken(user);

            GeneratedRefreshToken generatedRefreshToken =
                    refreshTokenService.createRefreshToken(user);

            return new LoginResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    accessToken,
                    generatedRefreshToken.getRawToken()
            );

        } catch (AuthenticationException exception) {

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }
    }

    public boolean isTokenValid(
            String token,
            User user) {

        try {

            String email =
                    jwtService.extractEmail(token);

            return email.equals(
                    user.getEmail()
            );

        } catch (
                JwtException |
                IllegalArgumentException exception) {

            return false;
        }
    }

    @Transactional
    public void changePassword(
            Long userId,
            ChangePasswordRequest request) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "User not found"
                                )
                        );

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getOldPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            throw new InvalidPasswordException(
                    "Old password is incorrect"
            );
        }

        String newHashedPassword =
                passwordEncoder.encode(
                        request.getNewPassword()
                );

        user.setPassword(
                newHashedPassword
        );

        user.setTokenVersion(
                user.getTokenVersion() + 1
        );

        userRepository.save(user);

        refreshTokenService.deleteByUserId(
                user.getId()
        );
    }
}
