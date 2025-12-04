package com.m2it.hermes.infrastructure.persistence.adapter;

import com.m2it.hermes.domain.model.Lease;
import com.m2it.hermes.domain.model.LeaseStatus;
import com.m2it.hermes.domain.port.out.LeaseRepository;
import com.m2it.hermes.infrastructure.persistence.entity.LeaseEntity;
import com.m2it.hermes.infrastructure.persistence.entity.PropertyEntity;
import com.m2it.hermes.infrastructure.persistence.entity.TenantEntity;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaLeaseRepository;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaPropertyRepository;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaTenantRepository;
import com.m2it.hermes.infrastructure.persistence.mapper.LeaseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LeaseRepositoryAdapter implements LeaseRepository {

    private final JpaLeaseRepository jpaRepository;
    private final JpaPropertyRepository propertyRepository;
    private final JpaTenantRepository tenantRepository;
    private final LeaseMapper mapper;

    @Override
    public Lease save(Lease lease) {
        LeaseEntity entity = mapper.toEntity(lease);

        // Set Property relationship
        if (lease.getPropertyId() != null) {
            PropertyEntity property = propertyRepository.findById(lease.getPropertyId())
                .orElseThrow(() -> new IllegalArgumentException("Property not found"));
            entity.setProperty(property);
        }

        // Set Tenants relationships
        if (lease.getTenantIds() != null && !lease.getTenantIds().isEmpty()) {
            List<TenantEntity> tenants = tenantRepository.findAllById(lease.getTenantIds());
            entity.setTenants(tenants);
        }

        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Lease> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Lease> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Lease> findByPropertyId(UUID propertyId) {
        return jpaRepository.findByPropertyId(propertyId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Lease> findByStatus(LeaseStatus status) {
        return jpaRepository.findByStatus(status).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Lease> findActiveLeases() {
        return jpaRepository.findActiveLeases().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}
