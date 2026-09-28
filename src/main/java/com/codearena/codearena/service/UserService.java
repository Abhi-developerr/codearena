package com.codearena.codearena.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codearena.codearena.dto.LoginRequest;
import com.codearena.codearena.dto.LoginResponse;
import com.codearena.codearena.dto.UserRequest;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.exception.EmailAlreadyExistsException;
import com.codearena.codearena.exception.UserNotFoundException;
import com.codearena.codearena.repository.UserRepository;
import com.codearena.codearena.security.CustomUserDetails;
import com.codearena.codearena.security.JwtService;

import io.jsonwebtoken.JwtException;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

   public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticationManager authenticationManager) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
}

    public User createUser(UserRequest request) {
String email =
        request.getEmail().trim().toLowerCase();

if (userRepository.existsByEmail(email)) {
    throw new EmailAlreadyExistsException(
            "Email already exists"
    );
}

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        String hashedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(hashedPassword);

        user.setRole("USER");

        return userRepository.save(user);
    }

public LoginResponse login(LoginRequest request) {

    try {

        String email =
        request.getEmail().trim().toLowerCase();

Authentication authentication =
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        String token =
                jwtService.generateToken(user);

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );

    } catch (AuthenticationException exception) {

        throw new UserNotFoundException(
                "Invalid email or password"
        );
    }
}
public boolean isTokenValid(String token, User user) {

    try {

        String email = jwtService.extractEmail(token);

        return email.equals(user.getEmail());

    } catch (JwtException | IllegalArgumentException exception) {

        return false;
    }
}
}
