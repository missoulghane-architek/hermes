package com.m2it.hermes.infrastructure.web.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
  
    @GetMapping
    @Operation(
            summary = "Lister tous les fichiers d'un bucket",
            description = "Récupère la liste des tous les fichiers d'un bucket"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = FileResponse.class)))
    })
    public ResponseEntity<List<FileResponse>> getFilesByBucket(
            @Parameter(description = "bucketId", required = true)
            @RequestParam String bucketId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<File> files = fileUseCase.getFiles(bucketId);
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
