package com.m2it.hermes.infrastructure.persistence.jpa;

import com.m2it.hermes.infrastructure.persistence.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaFileRepository extends JpaRepository<FileEntity, UUID> {
    List<FileEntity> findByPropertyId(UUID propertyId);
    void deleteByPropertyId(UUID propertyId);
}
