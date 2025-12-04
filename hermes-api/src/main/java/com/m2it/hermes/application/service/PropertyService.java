package com.m2it.hermes.application.service;
import com.m2it.hermes.application.port.in.*;
import com.m2it.hermes.domain.exception.PropertyNotFoundException;
import com.m2it.hermes.domain.model.*;
import com.m2it.hermes.domain.port.out.PropertyRepository;
import com.m2it.hermes.domain.valueobject.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
@Service
@RequiredArgsConstructor
public class PropertyService implements PropertyUseCase {
    private final PropertyRepository propertyRepository;
    @Override
    @Transactional
    public Property create(CreatePropertyCommand command) {
        Address address = new Address(command.getStreet(), command.getCity(), command.getPostalCode(), command.getCountry());
        Property property = Property.builder()
            .id(UUID.randomUUID())
            .ownerId(command.getOwnerId())
            .name(command.getName())
            .description(command.getDescription())
            .propertyType(command.getPropertyType())
            .address(address)
            .area(command.getArea())
            .status(command.getStatus() != null ? command.getStatus() : PropertyStatus.AVAILABLE)
            .photos(command.getPhotos() != null ? new ArrayList<>(command.getPhotos()) : new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        return propertyRepository.save(property);
    }
    @Override
    @Transactional
    public Property update(UpdatePropertyCommand command) {
        Property existing = propertyRepository.findById(command.getId())
            .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + command.getId()));
        Address address = new Address(command.getStreet(), command.getCity(), command.getPostalCode(), command.getCountry());
        Property updated = Property.builder()
            .id(command.getId())
            .ownerId(command.getOwnerId())
            .name(command.getName())
            .description(command.getDescription())
            .propertyType(command.getPropertyType())
            .address(address)
            .area(command.getArea())
            .status(command.getStatus())
            .photos(command.getPhotos() != null ? new ArrayList<>(command.getPhotos()) : new ArrayList<>())
            .createdAt(existing.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();
        return propertyRepository.save(updated);
    }
    @Override
    public Property getById(UUID id) {
        return propertyRepository.findById(id)
            .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + id));
    }
    @Override
    public List<Property> getAll() {
        return propertyRepository.findAll();
    }
    @Override
    @Transactional
    public void delete(UUID id) {
        if (!propertyRepository.findById(id).isPresent()) {
            throw new PropertyNotFoundException("Property not found with ID: " + id);
        }
        propertyRepository.delete(id);
    }
    @Override
    public List<Property> getByStatus(PropertyStatus status) {
        return propertyRepository.findByStatus(status);
    }
    @Override
    public List<Property> getByPropertyType(PropertyType propertyType) {
        return propertyRepository.findByPropertyType(propertyType);
    }
    @Override
    public List<Property> getByCity(String city) {
        return propertyRepository.findByCity(city);
    }
    @Override
    @Transactional
    public Property addPhoto(UUID id, String photoUrl) {
        Property property = getById(id);
        property.addPhoto(photoUrl);
        return propertyRepository.save(property);
    }
    @Override
    @Transactional
    public Property removePhoto(UUID id, String photoUrl) {
        Property property = getById(id);
        property.removePhoto(photoUrl);
        return propertyRepository.save(property);
    }
}
