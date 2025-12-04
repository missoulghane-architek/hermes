package com.m2it.hermes.application.port.in;

import java.util.List;
import java.util.UUID;

import com.m2it.hermes.domain.model.Lease;
import com.m2it.hermes.domain.model.LeaseStatus;

public interface LeaseUseCase {

    Lease create(CreateLeaseCommand command);
    Lease update(UpdateLeaseCommand command);
    Lease getById(UUID id);
    List<Lease> getAll();
    void delete(UUID id);
    List<Lease> getByPropertyId(UUID propertyId);
    List<Lease> getByStatus(LeaseStatus status);
    List<Lease> getActiveLeases();
    Lease start(UUID id);
    Lease complete(UUID id);
    Lease cancel(UUID id);
    Lease addTenant(UUID leaseId, UUID tenantId);
    Lease removeTenant(UUID leaseId, UUID tenantId);
    
}
