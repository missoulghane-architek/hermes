package com.m2it.hermes.infrastructure.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.m2it.hermes.application.port.in.CreatePropertyCommand;
import com.m2it.hermes.application.port.in.FileUseCase;
import com.m2it.hermes.application.port.in.PropertyUseCase;
import com.m2it.hermes.application.port.in.UpdatePropertyCommand;
import com.m2it.hermes.domain.exception.UserNotFoundException;
import com.m2it.hermes.domain.model.Property;
import com.m2it.hermes.domain.model.PropertyStatus;
import com.m2it.hermes.domain.model.PropertyType;
import com.m2it.hermes.domain.model.User;
import com.m2it.hermes.domain.port.out.UserRepository;
import com.m2it.hermes.infrastructure.web.dto.CreatePropertyRequest;
import com.m2it.hermes.infrastructure.web.dto.FileResponse;
import com.m2it.hermes.infrastructure.web.dto.PropertyResponse;
import com.m2it.hermes.infrastructure.web.dto.UpdatePropertyRequest;

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
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@Tag(name = "Biens immobiliers  (Properties)", description = "Endpoints pour la gestion des biens immobiliers")
@SecurityRequirement(name = "bearerAuth")
public class PropertyController {

    private final PropertyUseCase propertyUseCase;
    private final UserRepository userRepository;
    private final FileUseCase fileUseCase;

    @PostMapping
    @Operation(summary = "Créer un nouveau bien immobilier", description = "Crée un nouveau bien immobilier avec ses détails et son adresse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bien créé avec succès",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<PropertyResponse> createProperty(
            @Valid @RequestBody CreatePropertyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userDetails.getUsername()));

        CreatePropertyCommand command = CreatePropertyCommand.builder()
                .ownerId(user.getId())
                .name(request.getName())
                .description(request.getDescription())
                .propertyType(request.getPropertyType())
                .street(request.getStreet())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .area(request.getArea())
                .status(request.getStatus())
                .photos(request.getPhotos())
                .build();

        Property property = propertyUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toPropertyResponse(property));
    }

    @PostMapping(value = "/with-pictures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Créer un nouveau bien immobilier avec des fichiers",
            description = "Crée un nouveau bien immobilier avec ses détails, son adresse et télécharge des fichiers (images) associés à la propriété"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bien créé avec succès et fichiers téléchargés",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content)
    })
    public ResponseEntity<PropertyResponse> createPropertyWithPictures(
            @Parameter(description = "Données de la propriété au format JSON", required = true)
            @RequestPart("property") @Valid CreatePropertyRequest request,
            @Parameter(description = "Fichiers à télécharger", required = true)
            @RequestPart("files") MultipartFile[] files,
            @AuthenticationPrincipal UserDetails userDetails) {

        // Récupérer l'utilisateur
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userDetails.getUsername()));

        // Créer la propriété
        CreatePropertyCommand command = CreatePropertyCommand.builder()
                .ownerId(user.getId())
                .name(request.getName())
                .description(request.getDescription())
                .propertyType(request.getPropertyType())
                .street(request.getStreet())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .area(request.getArea())
                .status(request.getStatus())
                .photos(request.getPhotos())
                .build();

        Property property = propertyUseCase.create(command);

        // Upload files using FileUseCase
        fileUseCase.uploadFilesForProperty(property.getId(), files);

        // Récupérer la propriété mise à jour avec les fichiers
        Property updatedProperty = propertyUseCase.getById(property.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toPropertyResponse(updatedProperty));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour une propriété",
            description = "Modifie les informations d'une propriété existante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Propriété mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Propriété non trouvée", content = @Content)
    })
    public ResponseEntity<PropertyResponse> updateProperty(
            @Parameter(description = "ID de la propriété", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePropertyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userDetails.getUsername()));

        UpdatePropertyCommand command = UpdatePropertyCommand.builder()
                .id(id)
                .ownerId(user.getId())
                .name(request.getName())
                .description(request.getDescription())
                .propertyType(request.getPropertyType())
                .street(request.getStreet())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .area(request.getArea())
                .status(request.getStatus())
                .photos(request.getPhotos())
                .build();

        Property property = propertyUseCase.update(command);
        return ResponseEntity.ok(toPropertyResponse(property));
    }

    @GetMapping
    @Operation(
            summary = "Lister toutes les propriétés",
            description = "Récupère la liste complète de toutes les propriétés"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class)))
    })
    public ResponseEntity<List<PropertyResponse>> getAllProperties(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PropertyResponse> properties = propertyUseCase.getAll().stream()
                .map(this::toPropertyResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Consulter une propriété",
            description = "Récupère les détails d'une propriété spécifique par son ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Propriété trouvée",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Propriété non trouvée", content = @Content)
    })
    public ResponseEntity<PropertyResponse> getPropertyById(
            @Parameter(description = "ID de la propriété", required = true)
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Property property = propertyUseCase.getById(id);
        return ResponseEntity.ok(toPropertyResponse(property));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer une propriété",
            description = "Supprime définitivement une propriété du système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Propriété supprimée avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Propriété non trouvée", content = @Content)
    })
    public ResponseEntity<Void> deleteProperty(
            @Parameter(description = "ID de la propriété", required = true)
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        propertyUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Lister les propriétés par statut",
            description = "Récupère toutes les propriétés ayant un statut spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class)))
    })
    public ResponseEntity<List<PropertyResponse>> getPropertiesByStatus(
            @Parameter(description = "Statut de la propriété (AVAILABLE, RENTED, INACTIVE)", required = true)
            @PathVariable PropertyStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PropertyResponse> properties = propertyUseCase.getByStatus(status).stream()
                .map(this::toPropertyResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/type/{type}")
    @Operation(
            summary = "Lister les propriétés par type",
            description = "Récupère toutes les propriétés ayant un type spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class)))
    })
    public ResponseEntity<List<PropertyResponse>> getPropertiesByType(
            @Parameter(description = "Type de propriété (HOUSE, APARTMENT, STUDIO, OFFICE, COMMERCIAL_SPACE, LAND, PARKING, OTHER)", required = true)
            @PathVariable PropertyType type,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PropertyResponse> properties = propertyUseCase.getByPropertyType(type).stream()
                .map(this::toPropertyResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(properties);
    }

    @GetMapping("/city/{city}")
    @Operation(
            summary = "Lister les propriétés par ville",
            description = "Récupère toutes les propriétés situées dans une ville spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class)))
    })
    public ResponseEntity<List<PropertyResponse>> getPropertiesByCity(
            @Parameter(description = "Nom de la ville", required = true)
            @PathVariable String city,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<PropertyResponse> properties = propertyUseCase.getByCity(city).stream()
                .map(this::toPropertyResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(properties);
    }

    @PostMapping("/{id}/photos")
    @Operation(
            summary = "Ajouter une photo à une propriété",
            description = "Ajoute une URL de photo à la liste des photos d'une propriété"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo ajoutée avec succès",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Propriété non trouvée", content = @Content)
    })
    public ResponseEntity<PropertyResponse> addPhoto(
            @Parameter(description = "ID de la propriété", required = true)
            @PathVariable UUID id,
            @Parameter(description = "URL de la photo", required = true)
            @RequestParam String photoUrl,
            @AuthenticationPrincipal UserDetails userDetails) {
        Property property = propertyUseCase.addPhoto(id, photoUrl);
        return ResponseEntity.ok(toPropertyResponse(property));
    }

    @DeleteMapping("/{id}/photos")
    @Operation(
            summary = "Retirer une photo d'une propriété",
            description = "Retire une URL de photo de la liste des photos d'une propriété"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo retirée avec succès",
                    content = @Content(schema = @Schema(implementation = PropertyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Propriété non trouvée", content = @Content)
    })
    public ResponseEntity<PropertyResponse> removePhoto(
            @Parameter(description = "ID de la propriété", required = true)
            @PathVariable UUID id,
            @Parameter(description = "URL de la photo", required = true)
            @RequestParam String photoUrl,
            @AuthenticationPrincipal UserDetails userDetails) {
        Property property = propertyUseCase.removePhoto(id, photoUrl);
        return ResponseEntity.ok(toPropertyResponse(property));
    }

    private PropertyResponse toPropertyResponse(Property property) {
        PropertyResponse.AddressDto addressDto = null;
        if (property.getAddress() != null) {
            addressDto = PropertyResponse.AddressDto.builder()
                    .street(property.getAddress().getStreet())
                    .city(property.getAddress().getCity())
                    .postalCode(property.getAddress().getPostalCode())
                    .country(property.getAddress().getCountry())
                    .build();
        }

        // Load pictures from FileUseCase using property ID
        List<FileResponse> picturesDto = fileUseCase.getFilesByProperty(property.getId()).stream()
                .map(file -> FileResponse.builder()
                        .id(file.getId())
                        .name(file.getName())
                        .url(file.getUrl())
                        .contentType(file.getContentType())
                        .size(file.getSize())
                        .createdAt(file.getCreatedAt())
                        .updatedAt(file.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return PropertyResponse.builder()
                .id(property.getId())
                .ownerId(property.getOwnerId())
                .name(property.getName())
                .description(property.getDescription())
                .propertyType(property.getPropertyType())
                .address(addressDto)
                .area(property.getArea())
                .status(property.getStatus())
                .photos(property.getPhotos())
                .pictures(picturesDto)
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();
    }
}
