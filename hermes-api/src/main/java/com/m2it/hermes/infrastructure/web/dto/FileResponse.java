package com.m2it.hermes.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Réponse contenant les informations d'un fichier")
public class FileResponse {

    @Schema(description = "Identifiant unique du fichier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Nom du bucket", example = "PROPERTY")
    private String bucket;

    @Schema(description = "Id de l'objet associé", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID refId;

    @Schema(description = "Nom du fichier", example = "photo-maison.jpg")
    private String name;

    @Schema(description = "URL d'accès au fichier", example = "/api/files/123e4567-e89b-12d3-a456-426614174000")
    private String url;

    @Schema(description = "Type MIME du fichier", example = "image/jpeg")
    private String contentType;

    @Schema(description = "Taille du fichier en octets", example = "204800")
    private Long size;

    @Schema(description = "Date de création du fichier")
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière modification du fichier")
    private LocalDateTime updatedAt;
}
