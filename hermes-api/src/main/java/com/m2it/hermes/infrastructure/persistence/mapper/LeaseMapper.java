package com.m2it.hermes.infrastructure.persistence.mapper;

import com.m2it.hermes.domain.model.Lease;
import com.m2it.hermes.infrastructure.persistence.entity.LeaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LeaseMapper {

    private final TenantMapper tenantMapper;

    public Lease toDomain(LeaseEntity entity) {
        if (entity == null) {
            return null;
        }
        return Lease.builder()
            .id(entity.getId())
            .propertyId(entity.getProperty() != null ? entity.getProperty().getId() : null)
            .status(entity.getStatus())
            .monthlyRent(entity.getMonthlyRent())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .leaseType(entity.getLeaseType())
            .tenantIds(entity.getTenants() != null
                ? entity.getTenants().stream()
                    .map(tenantEntity -> tenantEntity.getId())
                    .collect(Collectors.toList())
                : new ArrayList<>())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public LeaseEntity toEntity(Lease domain) {
        if (domain == null) {
            return null;
        }
        return LeaseEntity.builder()
            .id(domain.getId())
            .status(domain.getStatus())
            .monthlyRent(domain.getMonthlyRent())
            .startDate(domain.getStartDate())
            .endDate(domain.getEndDate())
            .leaseType(domain.getLeaseType())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
}
