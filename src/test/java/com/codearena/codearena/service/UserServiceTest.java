package com.codearena.codearena.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.codearena.codearena.dto.ChangePasswordRequest;
import com.codearena.codearena.dto.LoginRequest;
import com.codearena.codearena.dto.UserRequest;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private Authentication authentication;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager,
                refreshTokenService
        );
    }

    @Test
    void createUserNormalizesEmailAndHashesPassword() {
        UserRequest request = new UserRequest();
        request.setName("Abhi");
        request.setEmail("  ABHI@example.com ");
        request.setPassword("password123");

        User savedUser = new User();
        when(userRepository.existsByEmail("abhi@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        assertEquals(savedUser, userService.createUser(request));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUserRejectsDuplicateEmail() {
        UserRequest request = new UserRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(request));
    }

    @Test
    void loginReturnsAccessAndRefreshTokens() {
        User user = user("user@example.com");
        CustomUserDetails details = new CustomUserDetails(user);
        GeneratedRefreshToken refreshToken = new GeneratedRefreshToken("refresh", null);
        LoginRequest request = new LoginRequest();
        request.setEmail(" USER@example.com ");
        request.setPassword("password123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(details);
        when(jwtService.generateToken(user)).thenReturn("access");
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        var response = userService.login(request);

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void loginConvertsAuthenticationFailureToInvalidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(request));
    }

    @Test
    void changePasswordUpdatesHashVersionAndDeletesRefreshTokens() {
        User user = user("user@example.com");
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(userWithId(user, 1L)));
        when(passwordEncoder.matches("oldPassword", "old-hash")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("new-hash");

        userService.changePassword(1L, request);

        assertEquals("new-hash", user.getPassword());
        assertEquals(1L, user.getTokenVersion());
        verify(refreshTokenService).deleteByUserId(1L);
    }

    @Test
    void changePasswordRejectsWrongOldPassword() {
        User user = userWithId(user("user@example.com"), 1L);
        user.setPassword("old-hash");
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("newPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "old-hash")).thenReturn(false);

        assertThrows(InvalidPasswordException.class,
                () -> userService.changePassword(1L, request));
    }

    @Test
    void changePasswordRejectsUnknownUser() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(99L, request));
    }

    private User user(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("User");
        user.setRole(Role.USER);
        user.setPassword("old-hash");
        return user;
    }

    private User userWithId(User user, Long id) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
        return user;
    }
}