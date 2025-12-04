package com.m2it.hermes.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête de changement de mot de passe")
public class ResetPasswordRequest {
    @NotBlank(message = "Token is required")
    @Schema(description = "Token de réinitialisation reçu par email")
    private String token;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Schema(description = "Nouveau mot de passe (minimum 8 caractères)", example = "newPassword123")
    private String newPassword;
}
