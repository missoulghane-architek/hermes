package com.m2it.hermes.infrastructure.persistence.mapper;

import com.m2it.hermes.domain.model.RefreshToken;
import com.m2it.hermes.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.builder()
                .id(entity.getId())
                .token(entity.getToken())
                .userId(entity.getUserId())
                .expiryDate(entity.getExpiryDate())
                .createdAt(entity.getCreatedAt())
                .revoked(entity.isRevoked())
                .build();
    }

    public RefreshTokenEntity toEntity(RefreshToken domain) {
        return RefreshTokenEntity.builder()
                .id(domain.getId())
                .token(domain.getToken())
                .userId(domain.getUserId())
                .expiryDate(domain.getExpiryDate())
                .createdAt(domain.getCreatedAt())
                .revoked(domain.isRevoked())
                .build();
    }
}
