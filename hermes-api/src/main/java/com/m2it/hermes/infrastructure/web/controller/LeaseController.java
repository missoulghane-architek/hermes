package com.m2it.hermes.infrastructure.web.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.m2it.hermes.application.port.in.CreateLeaseCommand;
import com.m2it.hermes.application.port.in.LeaseUseCase;
import com.m2it.hermes.application.port.in.UpdateLeaseCommand;
import com.m2it.hermes.application.service.FileStorageService;
import com.m2it.hermes.domain.exception.PropertyNotFoundException;
import com.m2it.hermes.domain.model.Lease;
import com.m2it.hermes.domain.model.LeaseStatus;
import com.m2it.hermes.infrastructure.persistence.entity.FileEntity;
import com.m2it.hermes.infrastructure.persistence.entity.PropertyEntity;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaFileRepository;
import com.m2it.hermes.infrastructure.persistence.jpa.JpaPropertyRepository;
import com.m2it.hermes.infrastructure.web.dto.CreateLeaseRequest;
import com.m2it.hermes.infrastructure.web.dto.FileResponse;
import com.m2it.hermes.infrastructure.web.dto.LeaseResponse;
import com.m2it.hermes.infrastructure.web.dto.UpdateLeaseRequest;

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

@RestController
@RequestMapping("/api/leases")
@RequiredArgsConstructor
@Tag(name = "Locations (Leases)", description = "Endpoints pour la gestion des baux")
@SecurityRequirement(name = "bearerAuth")
public class LeaseController {

    private final LeaseUseCase leaseUseCase;
    private final FileStorageService fileStorageService;
    private final JpaFileRepository fileRepository;
    private final JpaPropertyRepository propertyRepository;

    @PostMapping
    @Operation(
            summary = "Créer un nouveau bail",
            description = "Crée un nouveau bail pour une propriété avec les locataires associés"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bail créé avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Propriété ou locataire non trouvé", content = @Content)
    })
    public ResponseEntity<LeaseResponse> createLease(@Valid @RequestBody CreateLeaseRequest request) {
        CreateLeaseCommand command = CreateLeaseCommand.builder()
                .propertyId(request.getPropertyId())
                .status(request.getStatus())
                .monthlyRent(request.getMonthlyRent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .leaseType(request.getLeaseType())
                .tenantIds(request.getTenantIds())
                .build();

        Lease lease = leaseUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toLeaseResponse(lease));
    }

    @PostMapping(value = "/with-pictures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Créer un nouveau bail avec des fichiers",
            description = "Crée un nouveau bail pour une propriété avec les locataires associés et télécharge des fichiers (images) associés à la propriété"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bail créé avec succès et fichiers téléchargés",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Propriété ou locataire non trouvé", content = @Content)
    })
    public ResponseEntity<LeaseResponse> createLeaseWithPictures(
            @Parameter(description = "Données du bail au format JSON", required = true)
            @RequestPart("lease") @Valid CreateLeaseRequest request,
            @Parameter(description = "Fichiers à télécharger", required = true)
            @RequestPart("files") MultipartFile[] files) {

        // Créer le bail
        CreateLeaseCommand command = CreateLeaseCommand.builder()
                .propertyId(request.getPropertyId())
                .status(request.getStatus())
                .monthlyRent(request.getMonthlyRent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .leaseType(request.getLeaseType())
                .tenantIds(request.getTenantIds())
                .build();

        Lease lease = leaseUseCase.create(command);

        // Récupérer la propriété associée au bail
        PropertyEntity property = propertyRepository.findById(lease.getPropertyId())
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + lease.getPropertyId()));

        // Télécharger et associer les fichiers à la propriété
        List<FileResponse> uploadedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            String storedFileName = fileStorageService.storeFile(file);

            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/download/")
                    .path(storedFileName)
                    .toUriString();

            FileEntity fileEntity = FileEntity.builder()
                    .id(UUID.randomUUID())
                    .name(file.getOriginalFilename())
                    .url(fileUrl)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .property(property)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            fileEntity = fileRepository.save(fileEntity);

            FileResponse response = FileResponse.builder()
                    .id(fileEntity.getId())
                    .name(fileEntity.getName())
                    .url(fileEntity.getUrl())
                    .contentType(fileEntity.getContentType())
                    .size(fileEntity.getSize())
                    .createdAt(fileEntity.getCreatedAt())
                    .updatedAt(fileEntity.getUpdatedAt())
                    .build();
            uploadedFiles.add(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(toLeaseResponse(lease));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un bail",
            description = "Modifie les informations d'un bail existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bail mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bail, propriété ou locataire non trouvé", content = @Content)
    })
    public ResponseEntity<LeaseResponse> updateLease(
            @Parameter(description = "ID du bail", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLeaseRequest request) {
        UpdateLeaseCommand command = UpdateLeaseCommand.builder()
                .id(id)
                .propertyId(request.getPropertyId())
                .status(request.getStatus())
                .monthlyRent(request.getMonthlyRent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .leaseType(request.getLeaseType())
                .tenantIds(request.getTenantIds())
                .build();

        Lease lease = leaseUseCase.update(command);
        return ResponseEntity.ok(toLeaseResponse(lease));
    }

    @GetMapping
    @Operation(
            summary = "Lister tous les baux",
            description = "Récupère la liste complète de tous les baux"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class)))
    })
    public ResponseEntity<List<LeaseResponse>> getAllLeases() {
        List<LeaseResponse> leases = leaseUseCase.getAll().stream()
                .map(this::toLeaseResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leases);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consulter un bail",
            description = "Récupère les détails d'un bail spécifique par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bail trouvé",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bail non trouvé", content = @Content)
    })
    public ResponseEntity<LeaseResponse> getLeaseById(
            @Parameter(description = "ID du bail", required = true)
            @PathVariable UUID id) {
        Lease lease = leaseUseCase.getById(id);
        return ResponseEntity.ok(toLeaseResponse(lease));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un bail",
            description = "Supprime définitivement un bail du système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bail supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bail non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deleteLease(
            @Parameter(description = "ID du bail", required = true)
            @PathVariable UUID id) {
        leaseUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/property/{propertyId}")
    @Operation(
            summary = "Lister les baux d'une propriété",
            description = "Récupère tous les baux associés à une propriété spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class)))
    })
    public ResponseEntity<List<LeaseResponse>> getLeasesByPropertyId(
            @Parameter(description = "ID de la propriété", required = true)
            @PathVariable UUID propertyId) {
        List<LeaseResponse> leases = leaseUseCase.getByPropertyId(propertyId).stream()
                .map(this::toLeaseResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leases);
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Lister les baux par statut",
            description = "Récupère tous les baux ayant un statut spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class)))
    })
    public ResponseEntity<List<LeaseResponse>> getLeasesByStatus(
            @Parameter(description = "Statut du bail (ONGOING, COMPLETED, RESERVED, CANCELLED)", required = true)
            @PathVariable LeaseStatus status) {
        List<LeaseResponse> leases = leaseUseCase.getByStatus(status).stream()
                .map(this::toLeaseResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leases);
    }

    @GetMapping("/active")
    @Operation(
            summary = "Lister les baux actifs",
            description = "Récupère tous les baux actuellement actifs (ONGOING ou RESERVED)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class)))
    })
    public ResponseEntity<List<LeaseResponse>> getActiveLeases() {
        List<LeaseResponse> leases = leaseUseCase.getActiveLeases().stream()
                .map(this::toLeaseResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(leases);
    }

    @PostMapping("/{id}/start")
    @Operation(
            summary = "Démarrer un bail",
            description = "Change le statut d'un bail RESERVED en ONGOING"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bail démarré avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Le bail ne peut pas être démarré", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bail non trouvé", content = @Content)
    })
    public ResponseEntity<LeaseResponse> startLease(
            @Parameter(description = "ID du bail", required = true)
            @PathVariable UUID id) {
        Lease lease = leaseUseCase.start(id);
        return ResponseEntity.ok(toLeaseResponse(lease));
    }

    @PostMapping("/{id}/complete")
    @Operation(
            summary = "Terminer un bail",
            description = "Change le statut d'un bail ONGOING en COMPLETED"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bail terminé avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Le bail ne peut pas être terminé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bail non trouvé", content = @Content)
    })
    public ResponseEntity<LeaseResponse> completeLease(
            @Parameter(description = "ID du bail", required = true)
            @PathVariable UUID id) {
        Lease lease = leaseUseCase.complete(id);
        return ResponseEntity.ok(toLeaseResponse(lease));
    }

    @PostMapping("/{id}/cancel")
    @Operation(
            summary = "Annuler un bail",
            description = "Change le statut d'un bail RESERVED ou ONGOING en CANCELLED"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bail annulé avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Le bail ne peut pas être annulé", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bail non trouvé", content = @Content)
    })
    public ResponseEntity<LeaseResponse> cancelLease(
            @Parameter(description = "ID du bail", required = true)
            @PathVariable UUID id) {
        Lease lease = leaseUseCase.cancel(id);
        return ResponseEntity.ok(toLeaseResponse(lease));
    }

    @PostMapping("/{leaseId}/tenants/{tenantId}")
    @Operation(
            summary = "Ajouter un locataire à un bail",
            description = "Associe un locataire supplémentaire à un bail existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locataire ajouté avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bail ou locataire non trouvé", content = @Content)
    })
    public ResponseEntity<LeaseResponse> addTenant(
            @Parameter(description = "ID du bail", required = true)
            @PathVariable UUID leaseId,
            @Parameter(description = "ID du locataire", required = true)
            @PathVariable UUID tenantId) {
        Lease lease = leaseUseCase.addTenant(leaseId, tenantId);
        return ResponseEntity.ok(toLeaseResponse(lease));
    }

    @DeleteMapping("/{leaseId}/tenants/{tenantId}")
    @Operation(
            summary = "Retirer un locataire d'un bail",
            description = "Retire un locataire d'un bail existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Locataire retiré avec succès",
                    content = @Content(schema = @Schema(implementation = LeaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bail non trouvé", content = @Content)
    })
    public ResponseEntity<LeaseResponse> removeTenant(
            @Parameter(description = "ID du bail", required = true)
            @PathVariable UUID leaseId,
            @Parameter(description = "ID du locataire", required = true)
            @PathVariable UUID tenantId) {
        Lease lease = leaseUseCase.removeTenant(leaseId, tenantId);
        return ResponseEntity.ok(toLeaseResponse(lease));
    }

    private LeaseResponse toLeaseResponse(Lease lease) {
        return LeaseResponse.builder()
                .id(lease.getId())
                .propertyId(lease.getPropertyId())
                .status(lease.getStatus())
                .monthlyRent(lease.getMonthlyRent())
                .startDate(lease.getStartDate())
                .endDate(lease.getEndDate())
                .leaseType(lease.getLeaseType())
                .tenantIds(lease.getTenantIds())
                .createdAt(lease.getCreatedAt())
                .updatedAt(lease.getUpdatedAt())
                .build();
    }
}
