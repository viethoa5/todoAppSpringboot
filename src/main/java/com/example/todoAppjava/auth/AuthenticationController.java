package com.example.todoAppjava.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationServer authenticationServer;
    @Value("${spring.refresh.expiration}")
    private Long maxAgeCookie;

    @PostMapping(path = "/register")
    public ResponseEntity<AuthenticationResponse> register(@ModelAttribute RegisterRequest request) {
        return authenticationResponse(authenticationServer.register(request));
    }

    private ResponseEntity<AuthenticationResponse> authenticationResponse(AuthenticationResponse authenticationResponse) {
        if (authenticationResponse != null) {
            String accessToken = authenticationResponse.getAccess_Token();
            String refreshToken = authenticationResponse.getRefresh_Token();
            var accessCookie = ResponseCookie.from("access_Token", accessToken).httpOnly(true).maxAge(maxAgeCookie).build();
            var refreshCookie = ResponseCookie.from("refresh_token", refreshToken).httpOnly(true).maxAge(maxAgeCookie).build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(authenticationResponse);
        } else {
            return  ResponseEntity.status(401).build();
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@ModelAttribute AuthenticationRequest request) {
        return authenticationResponse(authenticationServer.authenticate(request));
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request,
                                                               HttpServletResponse response,
                                                               @CookieValue("refresh_token") String refreshToken,
                                                               @CookieValue("access_Token") String accessTokenReal) {
        return authenticationResponse(authenticationServer.refresh(request,response,refreshToken,accessTokenReal));
    }
}
