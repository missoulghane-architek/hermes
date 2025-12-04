package com.m2it.hermes.domain.port.out;

import com.m2it.hermes.domain.model.Lease;
import com.m2it.hermes.domain.model.LeaseStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaseRepository {
    Lease save(Lease lease);
    Optional<Lease> findById(UUID id);
    List<Lease> findAll();
    void delete(UUID id);
    List<Lease> findByPropertyId(UUID propertyId);
    List<Lease> findByStatus(LeaseStatus status);
    List<Lease> findActiveLeases();
}
