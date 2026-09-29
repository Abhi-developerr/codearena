package com.codearena.codearena.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.codearena.codearena.entity.RefreshToken;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.exception.RefreshTokenExpiredException;
import com.codearena.codearena.exception.RefreshTokenNotFoundException;
import com.codearena.codearena.repository.RefreshTokenRepository;
import com.codearena.codearena.security.JwtService;
import com.codearena.codearena.security.TokenHashService;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() throws Exception {
        refreshTokenService = new RefreshTokenService(
                refreshTokenRepository,
                jwtService,
                new TokenHashService()
        );
        Field field = RefreshTokenService.class.getDeclaredField("refreshTokenExpiration");
        field.setAccessible(true);
        field.set(refreshTokenService, Duration.ofDays(7));
    }

    @Test
    void createRefreshTokenStoresHashAndFutureExpiry() {
        User user = new User();
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var generated = refreshTokenService.createRefreshToken(user);

        assertEquals(36, generated.getRawToken().length());
        assertEquals(user, generated.getRefreshToken().getUser());
        assertEquals(64, generated.getRefreshToken().getTokenHash().length());
        assertEquals(true, generated.getRefreshToken().getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void findByTokenRejectsUnknownToken() {
        when(refreshTokenRepository.findByTokenHash(any(String.class)))
                .thenReturn(Optional.empty());

        assertThrows(RefreshTokenNotFoundException.class,
                () -> refreshTokenService.findByToken("missing"));
    }

    @Test
    void verifyExpirationRejectsExpiredToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiresAt(Instant.now().minusSeconds(1));

        assertThrows(RefreshTokenExpiredException.class,
                () -> refreshTokenService.verifyExpiration(token));
    }

    @Test
    void refreshAccessTokenRotatesRefreshToken() {
        User user = new User();
        user.setEmail("user@example.com");
        RefreshToken oldToken = new RefreshToken();
        oldToken.setUser(user);
        oldToken.setExpiresAt(Instant.now().plus(Duration.ofDays(1)));
        when(refreshTokenRepository.findByTokenHash(any(String.class)))
                .thenReturn(Optional.of(oldToken));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(user)).thenReturn("access");

        var response = refreshTokenService.refreshAccessToken("old-refresh");

        assertEquals("access", response.getAccessToken());
        verify(refreshTokenRepository).delete(oldToken);
        assertEquals(36, response.getRefreshToken().length());
    }
}