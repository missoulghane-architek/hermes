package com.m2it.hermes.domain.port.out;

import com.m2it.hermes.domain.model.Tenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(UUID id);
    List<Tenant> findAll();
    void delete(UUID id);
    boolean existsByEmail(String email);
    Optional<Tenant> findByEmail(String email);
}
