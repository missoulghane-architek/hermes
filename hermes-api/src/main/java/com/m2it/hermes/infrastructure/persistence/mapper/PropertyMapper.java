package com.m2it.hermes.infrastructure.persistence.mapper;

import com.m2it.hermes.domain.model.Property;
import com.m2it.hermes.infrastructure.persistence.entity.PropertyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class PropertyMapper {

    private final AddressMapper addressMapper;

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
            // picturesIds will be loaded separately via FileRepository when needed
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
            // Note: pictures are not mapped here as they are managed separately via FileRepository
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
}
