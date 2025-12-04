package com.m2it.hermes.infrastructure.web.dto;

import com.m2it.hermes.domain.model.LeaseStatus;
import com.m2it.hermes.domain.model.LeaseType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CreateLeaseRequest {

    @NotNull(message = "Property ID is required")
    private UUID propertyId;

    private LeaseStatus status;

    @NotNull(message = "Monthly rent is required")
    @Positive(message = "Monthly rent must be positive")
    private BigDecimal monthlyRent;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Lease type is required")
    private LeaseType leaseType;

    private List<UUID> tenantIds;
}
