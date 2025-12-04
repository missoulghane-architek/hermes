package com.m2it.hermes.infrastructure.persistence.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.m2it.hermes.infrastructure.persistence.entity.FileEntity;

@Repository
public interface JpaFileRepository extends JpaRepository<FileEntity, UUID> {
    
    List<FileEntity> findByRefId(UUID refId);
    List<FileEntity> findByBucketType(String bucket);
    List<FileEntity> findByBucketTypeAndRefId(String bucketType, UUID refId);
}
