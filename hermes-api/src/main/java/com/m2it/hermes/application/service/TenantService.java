package com.m2it.hermes.application.service;
import com.m2it.hermes.application.port.in.*;
import com.m2it.hermes.domain.exception.*;
import com.m2it.hermes.domain.model.Tenant;
import com.m2it.hermes.domain.port.out.TenantRepository;
import com.m2it.hermes.domain.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
@Service
@RequiredArgsConstructor
public class TenantService implements TenantUseCase {
    private final TenantRepository tenantRepository;
    @Override
    @Transactional
    public Tenant create(CreateTenantCommand command) {
        if (tenantRepository.existsByEmail(command.getEmail())) {
            throw new TenantAlreadyExistsException("A tenant with this email already exists");
        }
        Address address = new Address(command.getStreet(), command.getCity(), command.getPostalCode(), command.getCountry());
        Tenant tenant = Tenant.builder()
            .id(UUID.randomUUID())
            .lastName(command.getLastName())
            .firstName(command.getFirstName())
            .email(new Email(command.getEmail()))
            .phone(command.getPhone())
            .birthDate(command.getBirthDate())
            .address(address)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        return tenantRepository.save(tenant);
    }
    @Override
    @Transactional
    public Tenant update(UpdateTenantCommand command) {
        Tenant existing = tenantRepository.findById(command.getId())
            .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + command.getId()));
        if (!existing.getEmail().getValue().equals(command.getEmail())) {
            if (tenantRepository.existsByEmail(command.getEmail())) {
                throw new TenantAlreadyExistsException("A tenant with this email already exists");
            }
        }
        Address address = new Address(command.getStreet(), command.getCity(), command.getPostalCode(), command.getCountry());
        Tenant updated = Tenant.builder()
            .id(command.getId())
            .lastName(command.getLastName())
            .firstName(command.getFirstName())
            .email(new Email(command.getEmail()))
            .phone(command.getPhone())
            .birthDate(command.getBirthDate())
            .address(address)
            .createdAt(existing.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();
        return tenantRepository.save(updated);
    }
    @Override
    public Tenant getById(UUID id) {
        return tenantRepository.findById(id)
            .orElseThrow(() -> new TenantNotFoundException("Tenant not found with ID: " + id));
    }
    @Override
    public List<Tenant> getAll() {
        return tenantRepository.findAll();
    }
    @Override
    @Transactional
    public void delete(UUID id) {
        if (!tenantRepository.findById(id).isPresent()) {
            throw new TenantNotFoundException("Tenant not found with ID: " + id);
        }
        tenantRepository.delete(id);
    }
    @Override
    public Tenant getByEmail(String email) {
        return tenantRepository.findByEmail(email)
            .orElseThrow(() -> new TenantNotFoundException("Tenant not found with email: " + email));
    }
}
