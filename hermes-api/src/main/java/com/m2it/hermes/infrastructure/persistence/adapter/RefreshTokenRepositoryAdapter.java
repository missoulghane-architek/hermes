package com.m2it.hermes.infrastructure.persistence.adapter;

import com.m2it.hermes.domain.model.RefreshToken;
import com.m2it.hermes.domain.port.out.RefreshTokenRepository;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaRefreshTokenRepository;
import com.m2it.hermes.infrastructure.persistence.mapper.RefreshTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenMapper.toDomain(
                jpaRefreshTokenRepository.save(refreshTokenMapper.toEntity(refreshToken))
        );
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token)
                .map(refreshTokenMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        jpaRefreshTokenRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        jpaRefreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
