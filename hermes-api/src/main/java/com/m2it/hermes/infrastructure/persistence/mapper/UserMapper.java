package com.m2it.hermes.infrastructure.persistence.mapper;

import com.m2it.hermes.domain.model.User;
import com.m2it.hermes.domain.valueobject.Email;
import com.m2it.hermes.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final RoleMapper roleMapper;

    public User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(new Email(entity.getEmail()))
                .password(entity.getPassword())
                .enabled(entity.isEnabled())
                .emailVerified(entity.isEmailVerified())
                .emailVerificationToken(entity.getEmailVerificationToken())
                .emailVerificationTokenExpiry(entity.getEmailVerificationTokenExpiry())
                .resetPasswordToken(entity.getResetPasswordToken())
                .resetPasswordTokenExpiry(entity.getResetPasswordTokenExpiry())
                .roles(entity.getRoles().stream()
                        .map(roleMapper::toDomain)
                        .collect(Collectors.toSet()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .email(domain.getEmail().getValue())
                .password(domain.getPassword())
                .enabled(domain.isEnabled())
                .emailVerified(domain.isEmailVerified())
                .emailVerificationToken(domain.getEmailVerificationToken())
                .emailVerificationTokenExpiry(domain.getEmailVerificationTokenExpiry())
                .resetPasswordToken(domain.getResetPasswordToken())
                .resetPasswordTokenExpiry(domain.getResetPasswordTokenExpiry())
                .roles(domain.getRoles().stream()
                        .map(roleMapper::toEntity)
                        .collect(Collectors.toSet()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
