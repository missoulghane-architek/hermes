package com.m2it.hermes.infrastructure.web.controller;

import com.m2it.hermes.application.port.in.CreateTenantCommand;
import com.m2it.hermes.application.port.in.TenantUseCase;
import com.m2it.hermes.application.port.in.UpdateTenantCommand;
import com.m2it.hermes.domain.model.Tenant;
import com.m2it.hermes.infrastructure.web.dto.CreateTenantRequest;
import com.m2it.hermes.infrastructure.web.dto.TenantResponse;
import com.m2it.hermes.infrastructure.web.dto.UpdateTenantRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Locataires (Tenants)", description = "Endpoints pour la gestion des locataires")
@SecurityRequirement(name = "bearerAuth")
public class TenantController {

    private final TenantUseCase tenantUseCase;

    @PostMapping
    @Operation(
            summary = "Créer un nouveau locataire",
            description = "Crée un nouveau locataire avec ses informations personnelles et son adresse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Locataire créé avec succès",
                    content = @Content(schema = @Schema(implementation = TenantResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email déjà existant", content = @Content)
    })
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        CreateTenantCommand command = CreateTenantCommand.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .street(request.getStreet())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .build();

        Tenant tenant = tenantUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toTenantResponse(tenant));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un locataire",
            description = "Modifie les informations d'un locataire existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locataire mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = TenantResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Locataire non trouvé", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email déjà existant", content = @Content)
    })
    public ResponseEntity<TenantResponse> updateTenant(
            @Parameter(description = "ID du locataire", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantRequest request) {
        UpdateTenantCommand command = UpdateTenantCommand.builder()
                .id(id)
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .street(request.getStreet())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .build();

        Tenant tenant = tenantUseCase.update(command);
        return ResponseEntity.ok(toTenantResponse(tenant));
    }

    @GetMapping
    @Operation(
            summary = "Lister tous les locataires",
            description = "Récupère la liste complète de tous les locataires"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = TenantResponse.class)))
    })
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        List<TenantResponse> tenants = tenantUseCase.getAll().stream()
                .map(this::toTenantResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consulter un locataire",
            description = "Récupère les détails d'un locataire spécifique par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locataire trouvé",
                    content = @Content(schema = @Schema(implementation = TenantResponse.class))),
            @ApiResponse(responseCode = "404", description = "Locataire non trouvé", content = @Content)
    })
    public ResponseEntity<TenantResponse> getTenantById(
            @Parameter(description = "ID du locataire", required = true)
            @PathVariable UUID id) {
        Tenant tenant = tenantUseCase.getById(id);
        return ResponseEntity.ok(toTenantResponse(tenant));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un locataire",
            description = "Supprime définitivement un locataire du système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Locataire supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Locataire non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deleteTenant(
            @Parameter(description = "ID du locataire", required = true)
            @PathVariable UUID id) {
        tenantUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    @Operation(
            summary = "Rechercher un locataire par email",
            description = "Récupère un locataire par son adresse email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locataire trouvé",
                    content = @Content(schema = @Schema(implementation = TenantResponse.class))),
            @ApiResponse(responseCode = "404", description = "Locataire non trouvé", content = @Content)
    })
    public ResponseEntity<TenantResponse> getTenantByEmail(
            @Parameter(description = "Adresse email du locataire", required = true)
            @PathVariable String email) {
        Tenant tenant = tenantUseCase.getByEmail(email);
        return ResponseEntity.ok(toTenantResponse(tenant));
    }

    private TenantResponse toTenantResponse(Tenant tenant) {
        TenantResponse.AddressDto addressDto = null;
        if (tenant.getAddress() != null) {
            addressDto = TenantResponse.AddressDto.builder()
                    .street(tenant.getAddress().getStreet())
                    .city(tenant.getAddress().getCity())
                    .postalCode(tenant.getAddress().getPostalCode())
                    .country(tenant.getAddress().getCountry())
                    .build();
        }

        return TenantResponse.builder()
                .id(tenant.getId())
                .lastName(tenant.getLastName())
                .firstName(tenant.getFirstName())
                .email(tenant.getEmail() != null ? tenant.getEmail().getValue() : null)
                .phone(tenant.getPhone())
                .birthDate(tenant.getBirthDate())
                .address(addressDto)
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }
}
