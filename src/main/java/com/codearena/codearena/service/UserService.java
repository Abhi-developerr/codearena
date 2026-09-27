package com.codearena.service;

import com.codearena.codearena.dto.LoginRequest;
import com.codearena.codearena.dto.LoginResponse;
import com.codearena.codearena.exception.UserNotFoundException;
import com.codearena.dto.UserRequest;
import com.codearena.entity.User;
import com.codearena.exception.EmailAlreadyExistsException;
import com.codearena.repository.UserRepository;
import org.springframework.security.crypto.password.
import com.codearena.dto.LoginRequest;
import com.codearena.exception.UserNotFoundException;
import com.codearena.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

   public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService) {

    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
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

        return userRepository.save(user);
    }

   public LoginResponse login(LoginRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() ->
                    new UserNotFoundException(
                            "Invalid email or password"
                    )
            );

    boolean passwordMatches =
            passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword()
            );

    if (!passwordMatches) {
        throw new UserNotFoundException(
                "Invalid email or password"
        );
    }

    String token = jwtService.generateToken(user);

return new LoginResponse(
        user.getId(),
        user.getName(),
        user.getEmail(),
        token
);
}
}
