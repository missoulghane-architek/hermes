package com.m2it.hermes.infrastructure.persistence.adapter;

import com.m2it.hermes.domain.model.Tenant;
import com.m2it.hermes.domain.port.out.TenantRepository;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaTenantRepository;
import com.m2it.hermes.infrastructure.persistence.mapper.TenantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TenantRepositoryAdapter implements TenantRepository {

    private final JpaTenantRepository jpaRepository;
    private final TenantMapper mapper;

    @Override
    public Tenant save(Tenant tenant) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(tenant)));
    }

    @Override
    public Optional<Tenant> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Tenant> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<Tenant> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }
}
