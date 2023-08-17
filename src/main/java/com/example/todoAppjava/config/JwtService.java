package com.example.todoAppjava.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${spring.secretkey}")
    private String accessKey;
    @Value("${spring.expiredTime}")
    private Long expriedTime;
    @Value("${spring.refresh.secret-key}")
    private String refreshKey;
    @Value("${spring.refresh.expiration}")
    private Long expriedRefreshTime;

    public String extractUsernameFromAccessToken(String token) {
        return extractClaim(token, Claims::getSubject, accessKey);
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractClaim(token, Claims::getSubject, refreshKey);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, expriedTime, accessKey);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(new HashMap<>(), userDetails);
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, expriedRefreshTime, refreshKey);
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsernameFromAccessToken(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpried(token, accessKey);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsernameFromRefreshToken(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpried(token, refreshKey);
    }

    private boolean isTokenExpried(String token, String key) {
        return extractExpirationFromAccessToken(token, key).before(new Date());
    }

    private Date extractExpirationFromAccessToken(String token, String key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            String key
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(key), SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey(key))
                .build()
                .parseClaimsJws(token).getBody();
    }

    private Key getSignInKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
