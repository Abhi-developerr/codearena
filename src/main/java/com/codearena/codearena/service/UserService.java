package com.codearena.service;

import com.codearena.dto.UserRequest;
import com.codearena.entity.User;
import com.codearena.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.codearena.exception.EmailAlreadyExistsException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserRequest request) {

         if (userRepository.existsByEmail(request.getEmail())) {
        throw new EmailAlreadyExistsException("Email already exists");
    }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        return userRepository.save(user);
    }
}