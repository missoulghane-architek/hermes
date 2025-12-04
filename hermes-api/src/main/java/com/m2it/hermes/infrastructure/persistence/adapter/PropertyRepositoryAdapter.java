package com.m2it.hermes.infrastructure.persistence.adapter;

import com.m2it.hermes.domain.model.Property;
import com.m2it.hermes.domain.model.PropertyStatus;
import com.m2it.hermes.domain.model.PropertyType;
import com.m2it.hermes.domain.port.out.PropertyRepository;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaPropertyRepository;
import com.m2it.hermes.infrastructure.persistence.mapper.PropertyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PropertyRepositoryAdapter implements PropertyRepository {

    private final JpaPropertyRepository jpaRepository;
    private final PropertyMapper mapper;

    @Override
    public Property save(Property property) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(property)));
    }

    @Override
    public Optional<Property> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Property> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Property> findByStatus(PropertyStatus status) {
        return jpaRepository.findByStatus(status).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Property> findByPropertyType(PropertyType propertyType) {
        return jpaRepository.findByPropertyType(propertyType).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Property> findByCity(String city) {
        return jpaRepository.findByCity(city).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
}
