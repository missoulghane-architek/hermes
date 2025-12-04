package com.m2it.hermes.application.port.in;

import com.m2it.hermes.domain.model.LeaseStatus;
import com.m2it.hermes.domain.model.LeaseType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UpdateLeaseCommand {
    private final UUID id;
    private final UUID propertyId;
    private final LeaseStatus status;
    private final BigDecimal monthlyRent;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LeaseType leaseType;
    private final List<UUID> tenantIds;
}
