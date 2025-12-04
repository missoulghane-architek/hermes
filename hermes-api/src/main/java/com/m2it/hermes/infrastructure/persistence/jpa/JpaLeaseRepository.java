package com.m2it.hermes.infrastructure.persistence.jpa;

import com.m2it.hermes.domain.model.LeaseStatus;
import com.m2it.hermes.infrastructure.persistence.entity.LeaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaLeaseRepository extends JpaRepository<LeaseEntity, UUID> {
    List<LeaseEntity> findByPropertyId(UUID propertyId);
    List<LeaseEntity> findByStatus(LeaseStatus status);

    @Query("SELECT l FROM LeaseEntity l WHERE l.status IN ('ONGOING', 'RESERVED')")
    List<LeaseEntity> findActiveLeases();
}
