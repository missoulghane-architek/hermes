package com.m2it.hermes.application.service;
import com.m2it.hermes.application.port.in.*;
import com.m2it.hermes.domain.exception.*;
import com.m2it.hermes.domain.model.*;
import com.m2it.hermes.domain.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
@Service
@RequiredArgsConstructor
public class LeaseService implements LeaseUseCase {
    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    @Override
    @Transactional
    public Lease create(CreateLeaseCommand command) {
        if (!propertyRepository.findById(command.getPropertyId()).isPresent()) {
            throw new PropertyNotFoundException("Property not found with ID: " + command.getPropertyId());
        }
        if (command.getTenantIds() != null) {
            for (UUID tenantId : command.getTenantIds()) {
                if (!tenantRepository.findById(tenantId).isPresent()) {
                    throw new TenantNotFoundException("Tenant not found with ID: " + tenantId);
                }
            }
        }
        Lease lease = Lease.builder()
            .id(UUID.randomUUID())
            .propertyId(command.getPropertyId())
            .status(command.getStatus() != null ? command.getStatus() : LeaseStatus.RESERVED)
            .monthlyRent(command.getMonthlyRent())
            .startDate(command.getStartDate())
            .endDate(command.getEndDate())
            .leaseType(command.getLeaseType())
            .tenantIds(command.getTenantIds() != null ? new ArrayList<>(command.getTenantIds()) : new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        return leaseRepository.save(lease);
    }
    @Override
    @Transactional
    public Lease update(UpdateLeaseCommand command) {
        Lease existing = leaseRepository.findById(command.getId())
            .orElseThrow(() -> new LeaseNotFoundException("Lease not found with ID: " + command.getId()));
        if (!propertyRepository.findById(command.getPropertyId()).isPresent()) {
            throw new PropertyNotFoundException("Property not found with ID: " + command.getPropertyId());
        }
        if (command.getTenantIds() != null) {
            for (UUID tenantId : command.getTenantIds()) {
                if (!tenantRepository.findById(tenantId).isPresent()) {
                    throw new TenantNotFoundException("Tenant not found with ID: " + tenantId);
                }
            }
        }
        Lease updated = Lease.builder()
            .id(command.getId())
            .propertyId(command.getPropertyId())
            .status(command.getStatus())
            .monthlyRent(command.getMonthlyRent())
            .startDate(command.getStartDate())
            .endDate(command.getEndDate())
            .leaseType(command.getLeaseType())
            .tenantIds(command.getTenantIds() != null ? new ArrayList<>(command.getTenantIds()) : new ArrayList<>())
            .createdAt(existing.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();
        return leaseRepository.save(updated);
    }
    @Override
    public Lease getById(UUID id) {
        return leaseRepository.findById(id)
            .orElseThrow(() -> new LeaseNotFoundException("Lease not found with ID: " + id));
    }
    @Override
    public List<Lease> getAll() {
        return leaseRepository.findAll();
    }
    @Override
    @Transactional
    public void delete(UUID id) {
        if (!leaseRepository.findById(id).isPresent()) {
            throw new LeaseNotFoundException("Lease not found with ID: " + id);
        }
        leaseRepository.delete(id);
    }
    @Override
    public List<Lease> getByPropertyId(UUID propertyId) {
        return leaseRepository.findByPropertyId(propertyId);
    }
    @Override
    public List<Lease> getByStatus(LeaseStatus status) {
        return leaseRepository.findByStatus(status);
    }
    @Override
    public List<Lease> getActiveLeases() {
        return leaseRepository.findActiveLeases();
    }
    @Override
    @Transactional
    public Lease start(UUID id) {
        Lease lease = getById(id);
        lease.start();
        return leaseRepository.save(lease);
    }
    @Override
    @Transactional
    public Lease complete(UUID id) {
        Lease lease = getById(id);
        lease.complete();
        return leaseRepository.save(lease);
    }
    @Override
    @Transactional
    public Lease cancel(UUID id) {
        Lease lease = getById(id);
        lease.cancel();
        return leaseRepository.save(lease);
    }
    @Override
    @Transactional
    public Lease addTenant(UUID leaseId, UUID tenantId) {
        Lease lease = getById(leaseId);
        if (!tenantRepository.findById(tenantId).isPresent()) {
            throw new TenantNotFoundException("Tenant not found with ID: " + tenantId);
        }
        lease.addTenant(tenantId);
        return leaseRepository.save(lease);
    }
    @Override
    @Transactional
    public Lease removeTenant(UUID leaseId, UUID tenantId) {
        Lease lease = getById(leaseId);
        lease.removeTenant(tenantId);
        return leaseRepository.save(lease);
    }
}
