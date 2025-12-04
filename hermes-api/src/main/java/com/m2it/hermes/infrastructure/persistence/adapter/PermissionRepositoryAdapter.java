package com.m2it.hermes.infrastructure.persistence.adapter;

import com.m2it.hermes.domain.model.Permission;
import com.m2it.hermes.domain.port.out.PermissionRepository;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaPermissionRepository;
import com.m2it.hermes.infrastructure.persistence.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final JpaPermissionRepository jpaPermissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public Permission save(Permission permission) {
        return permissionMapper.toDomain(jpaPermissionRepository.save(permissionMapper.toEntity(permission)));
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return jpaPermissionRepository.findById(id)
                .map(permissionMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return jpaPermissionRepository.findByName(name)
                .map(permissionMapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return jpaPermissionRepository.findAll().stream()
                .map(permissionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaPermissionRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaPermissionRepository.existsByName(name);
    }
}
