package com.m2it.hermes.domain.port.out;

import com.m2it.hermes.domain.model.File;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository {
    File save(File file);
    File saveForProperty(File file, UUID propertyId);
    Optional<File> findById(UUID id);
    List<File> findByPropertyId(UUID propertyId);
    void delete(UUID id);
    void deleteByPropertyId(UUID propertyId);
}
