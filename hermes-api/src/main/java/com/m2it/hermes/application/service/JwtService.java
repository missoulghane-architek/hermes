package com.m2it.hermes.application.service;
import com.m2it.hermes.domain.model.User;
import io.jsonwebtoken.Claims;
import java.util.function.Function;
public interface JwtService {
    String extractUsername(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    String generateToken(User user);
    String generateRefreshToken(User user);
    boolean isTokenValid(String token, String username);
    long getAccessTokenExpiration();
}
