package com.m2it.hermes.infrastructure.web.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.m2it.hermes.application.port.in.FileUseCase;
import com.m2it.hermes.domain.model.File;
import com.m2it.hermes.infrastructure.web.dto.FileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Fichiers (Files)", description = "Endpoints pour la gestion des fichiers")
@SecurityRequirement(name = "bearerAuth")
public class FileController {

    private final FileUseCase fileUseCase;

    @PostMapping(value = "/upload/{propertyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Télécharger des fichiers pour une propriété",
            description = "Télécharge un ou plusieurs fichiers (images) et les associe à une propriété"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fichiers téléchargés avec succès",
                    content = @Content(schema = @Schema(implementation = FileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
            @ApiResponse(responseCode = "404", description = "Propriété non trouvée", content = @Content)
    })
    public ResponseEntity<List<FileResponse>> uploadFiles(
            @Parameter(description = "ID de la propriété", required = true)
            @PathVariable UUID propertyId,
            @RequestPart("files") MultipartFile[] files,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<File> uploadedFiles = fileUseCase.uploadFilesForProperty(propertyId, files);

        List<FileResponse> responses = uploadedFiles.stream()
                .map(this::toFileResponse)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/download/{fileName:.+}")
    @Operation(
            summary = "Télécharger un fichier",
            description = "Télécharge un fichier par son nom"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fichier téléchargé avec succès"),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé", content = @Content)
    })
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "Nom du fichier", required = true)
            @PathVariable String fileName) {

        try {
            Path filePath = fileUseCase.getFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/property/{propertyId}")
    @Operation(
            summary = "Lister les fichiers d'une propriété",
            description = "Récupère tous les fichiers associés à une propriété"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = FileResponse.class)))
    })
    public ResponseEntity<List<FileResponse>> getFilesByProperty(
            @Parameter(description = "ID de la propriété", required = true)
            @PathVariable UUID propertyId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<File> files = fileUseCase.getFilesByProperty(propertyId);
        List<FileResponse> responses = files.stream()
                .map(this::toFileResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{fileId}")
    @Operation(
            summary = "Supprimer un fichier",
            description = "Supprime un fichier du système et du stockage"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fichier supprimé avec succès", content = @Content),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé", content = @Content)
    })
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "ID du fichier", required = true)
            @PathVariable UUID fileId,
            @AuthenticationPrincipal UserDetails userDetails) {

        fileUseCase.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    private FileResponse toFileResponse(File file) {
        return FileResponse.builder()
                .id(file.getId())
                .name(file.getName())
                .url(file.getUrl())
                .contentType(file.getContentType())
                .size(file.getSize())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}
