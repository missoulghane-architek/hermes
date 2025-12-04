package com.m2it.hermes.infrastructure.web.dto;

import com.m2it.hermes.domain.model.LeaseStatus;
import com.m2it.hermes.domain.model.LeaseType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class LeaseResponse {
    private UUID id;
    private UUID propertyId;
    private LeaseStatus status;
    private BigDecimal monthlyRent;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaseType leaseType;
    private List<UUID> tenantIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
