package com.m2it.hermes.application.port.in;

import com.m2it.hermes.domain.model.PropertyStatus;
import com.m2it.hermes.domain.model.PropertyType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CreatePropertyCommand {
    private final UUID ownerId;
    private final String name;
    private final String description;
    private final PropertyType propertyType;
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;
    private final BigDecimal area;
    private final PropertyStatus status;
    private final List<String> photos;
}
