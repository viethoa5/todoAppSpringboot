package com.example.todoAppjava.auth;

import com.example.todoAppjava.config.JwtService;
import com.example.todoAppjava.user.User;
import com.example.todoAppjava.user.UserResposity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class AuthenticationServer {
    private final UserResposity userResposity;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse register(RegisterRequest request) {
        String encodePassword = passwordEncoder.encode(request.getPassword());
        var user = new User(request.getName(), request.getEmail(), encodePassword);
        userResposity.save(user);
        return resultResponseAuthentication(user);
    }

    private AuthenticationResponse resultResponseAuthentication(UserDetails user) {
        String jwtAccessToken = jwtService.generateAccessToken(user);
        String jwtRefreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .access_Token(jwtAccessToken)
                .refresh_Token(jwtRefreshToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        var user = userResposity.findByEmail(request.getEmail()).orElseThrow();
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return resultResponseAuthentication(user);
        } else {
            return null;
        }
    }

    public AuthenticationResponse refresh(HttpServletRequest request,
                                          HttpServletResponse response,
                                          @CookieValue("refresh_token") String refreshToken,
                                          @CookieValue("access_Token") String accessTokenReal) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String accessToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        accessToken = authHeader.substring(7);
        if (accessToken == accessTokenReal) {
            userEmail = jwtService.extractUsernameFromRefreshToken(refreshToken);
            if (userEmail != null) {
                var user = this.userResposity.findByEmail(userEmail)
                        .orElseThrow();
                if (jwtService.isRefreshTokenValid(refreshToken, user)) {
                    return resultResponseAuthentication(user);
                }
                return null;
            }
            return null;
        }
        return null;
    }
}
