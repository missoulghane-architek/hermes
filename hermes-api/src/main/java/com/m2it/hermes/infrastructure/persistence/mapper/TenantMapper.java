package com.m2it.hermes.infrastructure.persistence.mapper;

import com.m2it.hermes.domain.model.Tenant;
import com.m2it.hermes.domain.valueobject.Email;
import com.m2it.hermes.infrastructure.persistence.entity.TenantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantMapper {

    private final AddressMapper addressMapper;

    public Tenant toDomain(TenantEntity entity) {
        if (entity == null) {
            return null;
        }
        return Tenant.builder()
            .id(entity.getId())
            .lastName(entity.getLastName())
            .firstName(entity.getFirstName())
            .email(new Email(entity.getEmail()))
            .phone(entity.getPhone())
            .birthDate(entity.getBirthDate())
            .address(addressMapper.toDomain(entity.getAddress()))
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public TenantEntity toEntity(Tenant domain) {
        if (domain == null) {
            return null;
        }
        return TenantEntity.builder()
            .id(domain.getId())
            .lastName(domain.getLastName())
            .firstName(domain.getFirstName())
            .email(domain.getEmail().getValue())
            .phone(domain.getPhone())
            .birthDate(domain.getBirthDate())
            .address(addressMapper.toEntity(domain.getAddress()))
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
}
