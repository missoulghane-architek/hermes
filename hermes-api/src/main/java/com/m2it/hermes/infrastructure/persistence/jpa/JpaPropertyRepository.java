package com.m2it.hermes.infrastructure.persistence.jpa;

import com.m2it.hermes.domain.model.PropertyStatus;
import com.m2it.hermes.domain.model.PropertyType;
import com.m2it.hermes.infrastructure.persistence.entity.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaPropertyRepository extends JpaRepository<PropertyEntity, UUID> {
    List<PropertyEntity> findByStatus(PropertyStatus status);
    List<PropertyEntity> findByPropertyType(PropertyType propertyType);

    @Query("SELECT p FROM PropertyEntity p WHERE p.address.city = :city")
    List<PropertyEntity> findByCity(@Param("city") String city);
}
