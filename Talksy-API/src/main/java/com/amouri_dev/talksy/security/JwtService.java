package com.amouri_dev.talksy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String TOKEN_TYPE = "token_type";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    public JwtService() throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey("/keys/local-only/private_key.pem");
        this.publicKey = KeyUtils.loadPublicKey("/keys/local-only/public_key.pem");
    }

    public String generateAccessToken(final String username) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "ACCESS_TOKEN");
        return buildToken(username, claims, this.accessTokenExpiration);
    }

    public String generateRefreshToken(final String username) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "REFRESH_TOKEN");
        return buildToken(username, claims, this.refreshTokenExpiration);
    }

    private String buildToken(String username, final Map<String, Object> claims, final long tokenExpiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(this.privateKey)
                .compact()
                ;
    }

    public boolean isTokenValid(final String token, final String expectedUsername) {
        final String username = extractUsernameFromToken(token);
        return username.equals(expectedUsername) && !isTokenExpired(token);
    }


    public String extractUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (final JwtException e) {
            throw new RuntimeException("Invalid JWT token ", e.getCause());
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration()
                .before(new Date(System.currentTimeMillis()));
    }

    public String refreshAccessToken(final String refreshToken) {
        final Claims claims = extractClaims(refreshToken);
        if (!"REFRESH_TOKEN".equals(claims.get(TOKEN_TYPE))) {
            throw new RuntimeException("Invalid refresh token");
        }
        if(isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token expired");
        }
        final String username = claims.getSubject();
        return generateAccessToken(username);
    }

}
