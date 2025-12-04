package com.m2it.hermes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lease {
    private UUID id;
    private UUID propertyId;
    private LeaseStatus status;
    private BigDecimal monthlyRent;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaseType leaseType;

    @Builder.Default
    private List<UUID> tenantIds = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void start() {
        if (this.status == LeaseStatus.RESERVED) {
            this.status = LeaseStatus.ONGOING;
        } else {
            throw new IllegalStateException("Cannot start a lease that is not reserved");
        }
    }

    public void complete() {
        if (this.status == LeaseStatus.ONGOING) {
            this.status = LeaseStatus.COMPLETED;
        } else {
            throw new IllegalStateException("Cannot complete a lease that is not ongoing");
        }
    }

    public void cancel() {
        if (this.status == LeaseStatus.RESERVED || this.status == LeaseStatus.ONGOING) {
            this.status = LeaseStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Cannot cancel a completed or already cancelled lease");
        }
    }

    public void addTenant(UUID tenantId) {
        if (tenantId != null && !this.tenantIds.contains(tenantId)) {
            this.tenantIds.add(tenantId);
        }
    }

    public void removeTenant(UUID tenantId) {
        this.tenantIds.remove(tenantId);
    }

    public boolean isOngoing() {
        return this.status == LeaseStatus.ONGOING;
    }

    public boolean isCompleted() {
        return this.status == LeaseStatus.COMPLETED;
    }

    public boolean isActive() {
        return this.status == LeaseStatus.ONGOING || this.status == LeaseStatus.RESERVED;
    }

    public boolean isRenewable() {
        return this.endDate == null;
    }
}
