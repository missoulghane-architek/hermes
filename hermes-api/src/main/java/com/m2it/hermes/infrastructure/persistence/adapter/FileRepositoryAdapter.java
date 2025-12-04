package com.m2it.hermes.infrastructure.persistence.adapter;

import com.m2it.hermes.domain.model.File;
import com.m2it.hermes.domain.port.out.FileRepository;
import com.m2it.hermes.infrastructure.persistence.entity.FileEntity;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaFileRepository;
import com.m2it.hermes.infrastructure.persistence.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileRepositoryAdapter implements FileRepository {

    private final JpaFileRepository jpaRepository;
    private final FileMapper mapper;

    @Override
    public File save(File file) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(file)));
    }

    @Override
    public File saveForProperty(File file, UUID propertyId) {
        FileEntity fileEntity = mapper.toEntity(file);
        fileEntity.setPropertyId(propertyId);

        return mapper.toDomain(jpaRepository.save(fileEntity));
    }

    @Override
    public Optional<File> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<File> findByPropertyId(UUID propertyId) {
        return jpaRepository.findByPropertyId(propertyId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteByPropertyId(UUID propertyId) {
        jpaRepository.deleteByPropertyId(propertyId);
    }
}
