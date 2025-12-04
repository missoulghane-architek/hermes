package com.m2it.hermes.infrastructure.persistence.mapper;

import com.m2it.hermes.domain.model.File;
import com.m2it.hermes.infrastructure.persistence.entity.FileEntity;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {

    public FileEntity toEntity(File file) {
        if (file == null) {
            return null;
        }

        return FileEntity.builder()
                .id(file.getId())
                .name(file.getName())
                .url(file.getUrl())
                .contentType(file.getContentType())
                .size(file.getSize())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }

    public File toDomain(FileEntity entity) {
        if (entity == null) {
            return null;
        }

        return File.builder()
                .id(entity.getId())
                .name(entity.getName())
                .url(entity.getUrl())
                .contentType(entity.getContentType())
                .size(entity.getSize())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
