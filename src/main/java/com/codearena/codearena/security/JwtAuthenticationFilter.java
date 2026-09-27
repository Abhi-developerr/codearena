package com.codearena.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.codearena.entity.User;
import com.codearena.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import com.codearena.security.CustomUserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
        JwtService jwtService,
        UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }

    String token = authHeader.substring(7);
try {

 SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

    String email = jwtService.extractEmail(token);
    String role = jwtService.extractRole(token);

    User user = userRepository.findByEmail(email)
        .orElse(null);

if (user != null && jwtService.isTokenValid(token, user)) {

    UserDetails userDetails =
            new CustomUserDetails(user);

    UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

    SecurityContextHolder
            .getContext()
            .setAuthentication(authentication);
}
} catch (Exception exception) {

    SecurityContextHolder
            .clearContext();
}

    filterChain.doFilter(request, response);
}
}