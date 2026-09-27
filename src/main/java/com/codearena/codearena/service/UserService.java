package com.codearena.codearena.service;

import com.codearena.codearena.dto.LoginRequest;
import com.codearena.codearena.dto.LoginResponse;
import com.codearena.codearena.exception.UserNotFoundException;
import com.codearena.codearena.security.CustomUserDetails;
import com.codearena.codearena.dto.UserRequest;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.exception.EmailAlreadyExistsException;
import com.codearena.codearena.repository.UserRepository;
import org.springframework.security.core.Authentication;
import com.codearena.codearena.security.JwtService;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        if (userRepository.existsByEmail(request.getEmail())) {

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

    Authentication authentication =
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

    CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();

    User user = userDetails.getUser();

    String token = jwtService.generateToken(user);

    return new LoginResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            token
    );
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
