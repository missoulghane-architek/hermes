package com.m2it.hermes.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.m2it.hermes.domain.model.File;

public interface FileRepository {

    File save(File file, String bucketType, UUID refId);
    List<File> findByBucketId(String bucketId);
    List<File> findByRefId(String bucketId, UUID refId);
    Optional<File> findById(UUID id);
    void delete(UUID id);
    
}
