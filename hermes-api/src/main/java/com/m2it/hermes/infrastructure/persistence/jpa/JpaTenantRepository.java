package com.m2it.hermes.infrastructure.persistence.jpa;

import com.m2it.hermes.infrastructure.persistence.entity.TenantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaTenantRepository extends JpaRepository<TenantEntity, UUID> {
    boolean existsByEmail(String email);
    Optional<TenantEntity> findByEmail(String email);
}
