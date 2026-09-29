package com.codearena.codearena.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.codearena.codearena.dto.ChangePasswordRequest;
import com.codearena.codearena.dto.LoginRequest;
import com.codearena.codearena.dto.LoginResponse;
import com.codearena.codearena.dto.RefreshResponse;
import com.codearena.codearena.dto.RefreshTokenRequest;
import com.codearena.codearena.dto.UserRequest;
import com.codearena.codearena.entity.Role;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.security.CustomUserDetails;
import com.codearena.codearena.service.RefreshTokenService;
import com.codearena.codearena.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private Authentication authentication;

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(userService, refreshTokenService);
    }

    @Test
    void createUserDelegatesToService() {
        UserRequest request = new UserRequest();
        User user = user();
        when(userService.createUser(request)).thenReturn(user);

        assertEquals(user, controller.createUser(request));
        verify(userService).createUser(request);
    }

    @Test
    void loginDelegatesToService() {
        LoginRequest request = new LoginRequest();
        LoginResponse response = new LoginResponse(1L, "User", "user@example.com", "access", "refresh");
        when(userService.login(request)).thenReturn(response);

        assertEquals(response, controller.login(request));
        verify(userService).login(request);
    }

    @Test
    void refreshDelegatesToRefreshTokenService() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh");
        RefreshResponse response = new RefreshResponse("access", "new-refresh");
        when(refreshTokenService.refreshAccessToken("refresh")).thenReturn(response);

        assertEquals(response, controller.refreshToken(request));
        verify(refreshTokenService).refreshAccessToken("refresh");
    }

    @Test
    void changePasswordUsesAuthenticatedUserId() {
        User user = user();
        CustomUserDetails details = new CustomUserDetails(userWithId(user, 7L));
        ChangePasswordRequest request = new ChangePasswordRequest();
        when(authentication.getPrincipal()).thenReturn(details);

        assertEquals("Password changed successfully",
                controller.changePassword(request, authentication));
        verify(userService).changePassword(7L, request);
    }

    @Test
    void logoutDeletesRefreshTokenAndReturnsMessage() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh");

        Map<String, String> response = controller.logout(request);

        assertEquals("Logged out successfully", response.get("message"));
        verify(refreshTokenService).deleteByToken("refresh");
    }

    private User user() {
        User user = new User();
        user.setName("User");
        user.setEmail("user@example.com");
        user.setRole(Role.USER);
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