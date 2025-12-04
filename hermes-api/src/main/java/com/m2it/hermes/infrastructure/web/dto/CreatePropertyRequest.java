package com.m2it.hermes.infrastructure.web.dto;

import com.m2it.hermes.domain.model.PropertyStatus;
import com.m2it.hermes.domain.model.PropertyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Requête pour créer une propriété", example = """
    {
      "name": "Appartement Centre Ville",
      "description": "Magnifique appartement avec vue panoramique",
      "propertyType": "APARTMENT",
      "street": "123 Rue de la Paix",
      "city": "Paris",
      "postalCode": "75001",
      "country": "France",
      "area": 85.5,
      "status": "AVAILABLE",
      "photos": []
    }
    """)
public class CreatePropertyRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    @NotBlank(message = "Street is required")
    private String street;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;

    @NotNull(message = "Area is required")
    @Positive(message = "Area must be positive")
    private BigDecimal area;

    private PropertyStatus status;

    private List<String> photos;
}
