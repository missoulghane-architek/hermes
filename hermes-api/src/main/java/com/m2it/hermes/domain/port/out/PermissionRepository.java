package com.m2it.hermes.domain.port.out;

import com.m2it.hermes.domain.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {
    Permission save(Permission permission);
    Optional<Permission> findById(UUID id);
    Optional<Permission> findByName(String name);
    List<Permission> findAll();
    void delete(UUID id);
    boolean existsByName(String name);
}
