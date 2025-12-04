package com.m2it.hermes.infrastructure.persistence.mapper;

import com.m2it.hermes.domain.valueobject.Address;
import com.m2it.hermes.infrastructure.persistence.entity.AddressEmbeddable;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address toDomain(AddressEmbeddable entity) {
        if (entity == null) {
            return null;
        }
        return new Address(
            entity.getStreet(),
            entity.getCity(),
            entity.getPostalCode(),
            entity.getCountry()
        );
    }

    public AddressEmbeddable toEntity(Address domain) {
        if (domain == null) {
            return null;
        }
        return AddressEmbeddable.builder()
            .street(domain.getStreet())
            .city(domain.getCity())
            .postalCode(domain.getPostalCode())
            .country(domain.getCountry())
            .build();
    }
}
