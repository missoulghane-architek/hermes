package com.m2it.hermes.infrastructure.web.dto;

import com.m2it.hermes.domain.model.PropertyStatus;
import com.m2it.hermes.domain.model.PropertyType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PropertyResponse {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private PropertyType propertyType;
    private AddressDto address;
    private BigDecimal area;
    private PropertyStatus status;
    private List<String> photos;
    private List<FileResponse> pictures;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class AddressDto {
        private String street;
        private String city;
        private String postalCode;
        private String country;
    }
}
