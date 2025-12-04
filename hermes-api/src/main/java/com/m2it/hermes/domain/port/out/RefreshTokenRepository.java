package com.m2it.hermes.domain.port.out;

import com.m2it.hermes.domain.model.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserId(UUID userId);
    void deleteExpiredTokens();
}
