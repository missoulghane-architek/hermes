package com.m2it.hermes.domain.port.out;

import com.m2it.hermes.domain.model.Property;
import com.m2it.hermes.domain.model.PropertyStatus;
import com.m2it.hermes.domain.model.PropertyType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository {
    Property save(Property property);
    Optional<Property> findById(UUID id);
    List<Property> findAll();
    void delete(UUID id);
    List<Property> findByStatus(PropertyStatus status);
    List<Property> findByPropertyType(PropertyType propertyType);
    List<Property> findByCity(String city);
}
