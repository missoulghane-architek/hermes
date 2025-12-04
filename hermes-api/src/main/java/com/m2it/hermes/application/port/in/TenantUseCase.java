package com.m2it.hermes.application.port.in;

import com.m2it.hermes.domain.model.Tenant;

import java.util.List;
import java.util.UUID;

public interface TenantUseCase {
    Tenant create(CreateTenantCommand command);
    Tenant update(UpdateTenantCommand command);
    Tenant getById(UUID id);
    List<Tenant> getAll();
    void delete(UUID id);
    Tenant getByEmail(String email);
}
