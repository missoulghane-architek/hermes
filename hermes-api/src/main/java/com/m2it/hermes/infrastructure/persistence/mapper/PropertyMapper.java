package com.m2it.hermes.infrastructure.persistence.mapper;

import com.m2it.hermes.domain.model.Property;
import com.m2it.hermes.infrastructure.persistence.entity.PropertyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PropertyMapper {

    private final AddressMapper addressMapper;
    private final FileMapper fileMapper;

    public Property toDomain(PropertyEntity entity) {
        if (entity == null) {
            return null;
        }
        return Property.builder()
            .id(entity.getId())
            .ownerId(entity.getOwnerId())
            .name(entity.getName())
            .description(entity.getDescription())
            .propertyType(entity.getPropertyType())
            .address(addressMapper.toDomain(entity.getAddress()))
            .area(entity.getArea())
            .status(entity.getStatus())
            .photos(entity.getPhotos() != null ? new ArrayList<>(entity.getPhotos()) : new ArrayList<>())
            .pictures(entity.getPictures() != null ?
                entity.getPictures().stream()
                    .map(fileMapper::toDomain)
                    .collect(Collectors.toList()) : new ArrayList<>())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    public PropertyEntity toEntity(Property domain) {
        if (domain == null) {
            return null;
        }
        return PropertyEntity.builder()
            .id(domain.getId())
            .ownerId(domain.getOwnerId())
            .name(domain.getName())
            .description(domain.getDescription())
            .propertyType(domain.getPropertyType())
            .address(addressMapper.toEntity(domain.getAddress()))
            .area(domain.getArea())
            .status(domain.getStatus())
            .photos(domain.getPhotos() != null ? new ArrayList<>(domain.getPhotos()) : new ArrayList<>())
            .pictures(domain.getPictures() != null ?
                domain.getPictures().stream()
                    .map(fileMapper::toEntity)
                    .collect(Collectors.toList()) : new ArrayList<>())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
}
