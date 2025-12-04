package com.m2it.hermes.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.m2it.hermes.domain.model.File;
import com.m2it.hermes.domain.port.out.FileRepository;
import com.m2it.hermes.infrastructure.persistence.entity.FileEntity;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaFileRepository;
import com.m2it.hermes.infrastructure.persistence.mapper.FileMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepository {

    private final JpaFileRepository jpaRepository;
    private final FileMapper mapper;

    @Override
    public File save(File file, String bucketType, UUID refId) {
        FileEntity fileEntity = mapper.toEntity(file);
        fileEntity.setBucketType(bucketType);
        fileEntity.setRefId(refId);

        return mapper.toDomain(jpaRepository.save(fileEntity));
    }

    @Override
    public Optional<File> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<File> findByRefId(String bucketType, UUID propertyId) {
        return jpaRepository.findByBucketTypeAndRefId(bucketType, propertyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<File> findByBucketId(String bucketId) {
        return jpaRepository.findByBucketType(bucketId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
