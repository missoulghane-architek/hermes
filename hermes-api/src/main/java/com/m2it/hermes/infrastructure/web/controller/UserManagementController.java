// package com.m2it.hermes.infrastructure.web.controller;

// import java.util.List;
// import java.util.UUID;
// import java.util.stream.Collectors;

// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.m2it.hermes.application.port.in.UpdateUserCommand;
// import com.m2it.hermes.application.port.in.UserManagementUseCase;
// import com.m2it.hermes.domain.model.User;
// import com.m2it.hermes.infrastructure.web.dto.UpdateUserRequest;
// import com.m2it.hermes.infrastructure.web.dto.UserResponse;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/admin/users")
// @RequiredArgsConstructor
// @PreAuthorize("hasRole('ADMIN')")
// @Tag(name = "Administration des Utilisateurs", description = "Endpoints d'administration réservés aux utilisateurs avec le rôle ADMIN")
// @SecurityRequirement(name = "bearerAuth")
// public class UserManagementController {

//     private final UserManagementUseCase userManagementUseCase;

//     @GetMapping
//     @Operation(
//             summary = "Lister tous les utilisateurs",
//             description = "Récupère la liste complète des utilisateurs du système. Requiert le rôle ADMIN."
//     )
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès",
//                     content = @Content(schema = @Schema(implementation = UserResponse.class))),
//             @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis", content = @Content)
//     })
//     public ResponseEntity<List<UserResponse>> getAllUsers() {
//         List<UserResponse> users = userManagementUseCase.getAllUsers().stream()
//                 .map(this::toUserResponse)
//                 .collect(Collectors.toList());
//         return ResponseEntity.ok(users);
//     }

//     @GetMapping("/{userId}")
//     @Operation(
//             summary = "Consulter un utilisateur",
//             description = "Récupère les détails d'un utilisateur spécifique par son ID. Requiert le rôle ADMIN."
//     )
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Utilisateur trouvé",
//                     content = @Content(schema = @Schema(implementation = UserResponse.class))),
//             @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis", content = @Content),
//             @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
//     })
//     public ResponseEntity<UserResponse> getUserById(
//             @Parameter(description = "ID de l'utilisateur", required = true)
//             @PathVariable UUID userId) {
//         User user = userManagementUseCase.getUserById(userId);
//         return ResponseEntity.ok(toUserResponse(user));
//     }

//     @PutMapping("/{userId}")
//     @Operation(
//             summary = "Mettre à jour un utilisateur",
//             description = "Modifie les informations d'un utilisateur (username et/ou email). Requiert le rôle ADMIN."
//     )
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès",
//                     content = @Content(schema = @Schema(implementation = UserResponse.class))),
//             @ApiResponse(responseCode = "400", description = "Données invalides", content = @Content),
//             @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis", content = @Content),
//             @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content),
//             @ApiResponse(responseCode = "409", description = "Username ou email déjà existant", content = @Content)
//     })
//     public ResponseEntity<UserResponse> updateUser(
//             @Parameter(description = "ID de l'utilisateur", required = true)
//             @PathVariable UUID userId,
//             @Valid @RequestBody UpdateUserRequest request) {
//         UpdateUserCommand command = UpdateUserCommand.builder()
//                 .id(userId)
//                 .username(request.getUsername())
//                 .email(request.getEmail())
//                 .enabled(request.getEnabled())
//                 .build();

//         User user = userManagementUseCase.updateUser(command);
//         return ResponseEntity.ok(toUserResponse(user));
//     }

//     @DeleteMapping("/{userId}")
//     @Operation(
//             summary = "Supprimer un utilisateur",
//             description = "Supprime définitivement un utilisateur du système. Requiert le rôle ADMIN."
//     )
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès", content = @Content),
//             @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis", content = @Content),
//             @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
//     })
//     public ResponseEntity<Void> deleteUser(
//             @Parameter(description = "ID de l'utilisateur", required = true)
//             @PathVariable UUID userId) {
//         userManagementUseCase.deleteUser(userId);
//         return ResponseEntity.noContent().build();
//     }

//     @PostMapping("/{userId}/enable")
//     @Operation(
//             summary = "Activer un utilisateur",
//             description = "Active un compte utilisateur désactivé. Requiert le rôle ADMIN."
//     )
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Utilisateur activé avec succès", content = @Content),
//             @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis", content = @Content),
//             @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
//     })
//     public ResponseEntity<Void> enableUser(
//             @Parameter(description = "ID de l'utilisateur", required = true)
//             @PathVariable UUID userId) {
//         // TODO
//         //userManagementUseCase.enableUser(userId);
//         return ResponseEntity.ok().build();
//     }

//     @PostMapping("/{userId}/disable")
//     @Operation(
//             summary = "Désactiver un utilisateur",
//             description = "Désactive un compte utilisateur, empêchant toute connexion. Requiert le rôle ADMIN."
//     )
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Utilisateur désactivé avec succès", content = @Content),
//             @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis", content = @Content),
//             @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé", content = @Content)
//     })
//     public ResponseEntity<Void> disableUser(
//             @Parameter(description = "ID de l'utilisateur", required = true)
//             @PathVariable UUID userId) {
//         // TODO
//         //userManagementUseCase.disableUser(userId);
//         return ResponseEntity.ok().build();
//     }

//     @PostMapping("/{userId}/roles/{roleName}")
//     @Operation(
//             summary = "Attribuer un rôle",
//             description = "Ajoute un rôle à un utilisateur. Requiert le rôle ADMIN."
//     )
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Rôle attribué avec succès", content = @Content),
//             @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis", content = @Content),
//             @ApiResponse(responseCode = "404", description = "Utilisateur ou rôle non trouvé", content = @Content)
//     })
//     public ResponseEntity<Void> assignRole(
//             @Parameter(description = "ID de l'utilisateur", required = true)
//             @PathVariable UUID userId,
//             @Parameter(description = "Nom du rôle (ex: ROLE_ADMIN)", required = true)
//             @PathVariable String roleName) {
//                 // TODO
//         userManagementUseCase.assignRoleToUser(userId, UUID.randomUUID());
//         return ResponseEntity.ok().build();
//     }

//     @DeleteMapping("/{userId}/roles/{roleName}")
//     @Operation(
//             summary = "Retirer un rôle",
//             description = "Retire un rôle d'un utilisateur. Requiert le rôle ADMIN."
//     )
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Rôle retiré avec succès", content = @Content),
//             @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis", content = @Content),
//             @ApiResponse(responseCode = "404", description = "Utilisateur ou rôle non trouvé", content = @Content)
//     })
//     public ResponseEntity<Void> removeRole(
//             @Parameter(description = "ID de l'utilisateur", required = true)
//             @PathVariable UUID userId,
//             @Parameter(description = "Nom du rôle (ex: ROLE_ADMIN)", required = true)
//             @PathVariable String roleName) {
//                 // TODO
//         userManagementUseCase.removeRoleFromUser(userId, UUID.randomUUID());
//         return ResponseEntity.ok().build();
//     }

//     private UserResponse toUserResponse(User user) {
//         return UserResponse.builder()
//                 .id(user.getId())
//                 .username(user.getUsername())
//                 .firstName(user.getFirstName())
//                 .lastName(user.getLastName())
//                 .email(user.getEmail().getValue())
//                 .enabled(user.isEnabled())
//                 .emailVerified(user.isEmailVerified())
//                 .roles(user.getRoles().stream()
//                         .map(role -> role.getName())
//                         .collect(Collectors.toSet()))
//                 .createdAt(user.getCreatedAt())
//                 .updatedAt(user.getUpdatedAt())
//                 .build();
//     }
// }
