package com.m2it.hermes.domain.model;

import com.m2it.hermes.domain.valueobject.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private PropertyType propertyType;
    private Address address;
    private BigDecimal area;
    private PropertyStatus status;

    @Builder.Default
    private List<String> photos = new ArrayList<>();

    @Builder.Default
    private List<UUID> picturesIds = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void changeStatus(PropertyStatus newStatus) {
        this.status = newStatus;
    }

    public void addPhoto(String photoUrl) {
        if (photoUrl != null && !photoUrl.isBlank()) {
            this.photos.add(photoUrl);
        }
    }

    public void removePhoto(String photoUrl) {
        this.photos.remove(photoUrl);
    }

    public boolean isAvailable() {
        return this.status == PropertyStatus.AVAILABLE;
    }

    public boolean isRented() {
        return this.status == PropertyStatus.RENTED;
    }
}
