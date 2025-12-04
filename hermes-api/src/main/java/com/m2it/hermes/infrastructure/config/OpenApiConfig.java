package com.m2it.hermes.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Hermes API - Gestion des Utilisateurs")
                        .description("""
                                API REST pour la gestion des utilisateurs, rôles et permissions avec architecture hexagonale.

                                ## Fonctionnalités
                                - **Authentification JWT** avec access et refresh tokens
                                - **Validation par email** des inscriptions
                                - **Gestion des utilisateurs** (CRUD)
                                - **Système de rôles et permissions**
                                - **Administration** réservée aux utilisateurs avec le rôle ADMIN

                                ## Authentification
                                1. Utilisez `/api/auth/register` pour créer un compte ou `/api/auth/login` pour vous connecter
                                2. Récupérez l'`accessToken` de la réponse
                                3. Cliquez sur le bouton **Authorize** en haut de cette page
                                4. Entrez : `Bearer {votre-token}`
                                5. Vous pouvez maintenant accéder aux endpoints protégés

                                ## Compte Admin par défaut
                                - **Username:** admin
                                - **Password:** admin123
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("M2IT - Hermes Team")
                                .email("admin@hermes.com")
                                .url("https://github.com/m2it/hermes"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Serveur de développement")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Entrez votre JWT token précédé de 'Bearer '")));
    }
}
