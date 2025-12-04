package com.m2it.hermes.application.port.in;

import com.m2it.hermes.domain.model.Property;
import com.m2it.hermes.domain.model.PropertyStatus;
import com.m2it.hermes.domain.model.PropertyType;

import java.util.List;
import java.util.UUID;

public interface PropertyUseCase {
    Property create(CreatePropertyCommand command);
    Property update(UpdatePropertyCommand command);
    Property getById(UUID id);
    List<Property> getAll();
    void delete(UUID id);
    List<Property> getByStatus(PropertyStatus status);
    List<Property> getByPropertyType(PropertyType propertyType);
    List<Property> getByCity(String city);
    Property addPhoto(UUID id, String photoUrl);
    Property removePhoto(UUID id, String photoUrl);
}
