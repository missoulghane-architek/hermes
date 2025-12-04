# Guide d'utilisation de Swagger UI

## Introduction

L'API Hermes est documentée avec **Swagger UI** (OpenAPI 3.0), une interface interactive permettant d'explorer et de tester tous les endpoints de l'API directement depuis votre navigateur.

## Accès à Swagger UI

Une fois l'application lancée, accédez à Swagger UI via :

```
http://localhost:8080/swagger-ui.html
```

Ou directement :

```
http://localhost:8080/swagger-ui/index.html
```

## Vue d'ensemble de l'interface

L'interface Swagger UI est divisée en plusieurs sections :

### 1. Header
- **Titre** : Hermes API - Gestion des Utilisateurs
- **Version** : 1.0.0
- **Description** : Vue d'ensemble des fonctionnalités
- **Bouton Authorize** 🔓 : Pour configurer l'authentification JWT

### 2. Groupes d'endpoints (Tags)

Les endpoints sont organisés par catégories :

#### 📋 Authentification
Endpoints publics pour la gestion des sessions :
- `POST /api/auth/register` - Inscription
- `POST /api/auth/login` - Connexion
- `POST /api/auth/refresh` - Rafraîchir le token
- `POST /api/auth/verify-email` - Vérifier l'email
- `POST /api/auth/logout` - Déconnexion

#### 👥 Administration des Utilisateurs
Endpoints protégés (ROLE_ADMIN requis) :
- `GET /api/admin/users` - Lister tous les utilisateurs
- `GET /api/admin/users/{userId}` - Consulter un utilisateur
- `PUT /api/admin/users/{userId}` - Mettre à jour un utilisateur
- `DELETE /api/admin/users/{userId}` - Supprimer un utilisateur
- `POST /api/admin/users/{userId}/enable` - Activer un utilisateur
- `POST /api/admin/users/{userId}/disable` - Désactiver un utilisateur
- `POST /api/admin/users/{userId}/roles/{roleName}` - Attribuer un rôle
- `DELETE /api/admin/users/{userId}/roles/{roleName}` - Retirer un rôle

## Guide pas à pas

### Étape 1 : Connexion avec le compte admin

1. Cliquez sur **POST /api/auth/login**
2. Cliquez sur **Try it out**
3. Modifiez le body JSON :
   ```json
   {
     "usernameOrEmail": "admin",
     "password": "admin123"
   }
   ```
4. Cliquez sur **Execute**
5. **Copiez** l'`accessToken` de la réponse (sans les guillemets)

Exemple de réponse :
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "aBcDeFg1234567890",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### Étape 2 : Configurer l'authentification JWT

1. Cliquez sur le bouton **🔓 Authorize** en haut à droite
2. Dans le champ **Value**, entrez :
   ```
   Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
   (Remplacez par votre token complet)
3. Cliquez sur **Authorize**
4. Cliquez sur **Close**

**Note** : N'oubliez pas d'inclure `Bearer ` (avec un espace) avant le token !

Le cadenas 🔓 devient 🔒, indiquant que vous êtes authentifié.

### Étape 3 : Tester les endpoints protégés

Maintenant vous pouvez tester les endpoints d'administration :

#### Lister tous les utilisateurs

1. Cliquez sur **GET /api/admin/users**
2. Cliquez sur **Try it out**
3. Cliquez sur **Execute**
4. Vous verrez la liste de tous les utilisateurs

#### Créer un nouvel utilisateur

1. Allez sur **POST /api/auth/register** (pas besoin d'authentification)
2. Cliquez sur **Try it out**
3. Modifiez le body :
   ```json
   {
     "username": "testuser",
     "email": "test@example.com",
     "password": "password123"
   }
   ```
4. Cliquez sur **Execute**

#### Attribuer le rôle ADMIN à un utilisateur

1. Copiez l'`id` d'un utilisateur (depuis GET /api/admin/users)
2. Allez sur **POST /api/admin/users/{userId}/roles/{roleName}**
3. Cliquez sur **Try it out**
4. Entrez :
   - `userId` : L'UUID de l'utilisateur
   - `roleName` : `ROLE_ADMIN`
5. Cliquez sur **Execute**

### Étape 4 : Tester les codes d'erreur

#### Erreur 403 - Forbidden

Essayez d'accéder à un endpoint admin sans token :

1. Cliquez sur **🔒 Authorize**
2. Cliquez sur **Logout** pour vous déconnecter
3. Essayez **GET /api/admin/users**
4. Vous recevrez une erreur 403

#### Erreur 404 - Not Found

1. Allez sur **GET /api/admin/users/{userId}**
2. Entrez un UUID inexistant : `00000000-0000-0000-0000-000000000000`
3. Cliquez sur **Execute**
4. Vous recevrez une erreur 404

#### Erreur 409 - Conflict

1. Allez sur **POST /api/auth/register**
2. Essayez de créer un utilisateur avec le username `admin`
3. Vous recevrez une erreur 409 (déjà existant)

## Comprendre les réponses

### Codes de statut HTTP

- **200 OK** : Requête réussie
- **201 Created** : Ressource créée avec succès
- **204 No Content** : Succès sans contenu de réponse
- **400 Bad Request** : Données invalides
- **401 Unauthorized** : Token invalide ou expiré
- **403 Forbidden** : Pas les permissions nécessaires
- **404 Not Found** : Ressource non trouvée
- **409 Conflict** : Conflit (ex: email déjà existant)

### Format des réponses

#### Succès (AuthenticationResponse)
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

#### Succès (UserResponse)
```json
{
  "id": "uuid",
  "username": "string",
  "email": "string",
  "enabled": true,
  "emailVerified": false,
  "roles": ["ROLE_USER"],
  "createdAt": "2025-11-24T19:00:00",
  "updatedAt": "2025-11-24T19:00:00"
}
```

#### Erreur (ErrorResponse)
```json
{
  "timestamp": "2025-11-24T19:15:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: ..."
}
```

#### Erreur de validation (ValidationErrorResponse)
```json
{
  "timestamp": "2025-11-24T19:15:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "validationErrors": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email must be valid"
  }
}
```

## Fonctionnalités avancées

### Schemas

En bas de la page, vous trouverez la section **Schemas** qui détaille tous les modèles de données :

- **RegisterRequest**
- **LoginRequest**
- **RefreshTokenRequest**
- **UpdateUserRequest**
- **UserResponse**
- **AuthenticationResponse**
- **ErrorResponse**
- **ValidationErrorResponse**

Cliquez sur un schema pour voir sa structure complète.

### Try it out vs Execute

- **Try it out** : Active le mode édition des paramètres
- **Execute** : Envoie la requête HTTP réelle
- **Cancel** : Annule le mode édition

### Curl

Pour chaque requête exécutée, Swagger génère automatiquement :

1. La commande **curl** équivalente
2. L'**URL** de la requête
3. Les **headers** envoyés

Vous pouvez copier la commande curl pour l'utiliser dans votre terminal :

```bash
curl -X 'POST' \
  'http://localhost:8080/api/auth/login' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "usernameOrEmail": "admin",
  "password": "admin123"
}'
```

## Expiration du token

Les access tokens expirent après **1 heure** (3600 secondes).

Quand votre token expire :

1. Vous recevrez des erreurs 401 sur les requêtes authentifiées
2. Utilisez **POST /api/auth/refresh** avec votre refresh token
3. Mettez à jour votre autorisation avec le nouveau access token

Ou reconnectez-vous avec **POST /api/auth/login**.

## Astuces

### Raccourcis clavier

- `Ctrl/Cmd + K` : Ouvrir la recherche
- Cliquer sur le nom d'un endpoint : Développer/Réduire

### Filtrer les endpoints

Utilisez la barre de recherche en haut pour filtrer les endpoints par nom ou tag.

### Copier les exemples

Cliquez sur l'icône de copie à côté des exemples de JSON pour les copier rapidement.

### Voir les schémas

Dans chaque requête/réponse, cliquez sur **Model** pour voir la structure JSON, ou **Example Value** pour voir un exemple.

## Troubleshooting

### Erreur "Failed to fetch"

- Vérifiez que l'application est bien lancée sur le port 8080
- Vérifiez que vous utilisez `http://` et non `https://`
- Vérifiez votre firewall

### Erreur 401 sur tous les endpoints

- Vérifiez que vous avez bien cliqué sur **Authorize**
- Vérifiez que vous avez ajouté `Bearer ` avant le token
- Vérifiez que votre token n'est pas expiré (1h de validité)

### Erreur 403 sur les endpoints admin

- Vérifiez que l'utilisateur connecté a le rôle ADMIN
- Le compte par défaut `admin/admin123` possède ce rôle

### Le token ne fonctionne pas

- Assurez-vous de copier le token SANS les guillemets
- Format correct : `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI...`
- Format incorrect : `Bearer "eyJhbGciOiJIUzI1NiIsInR5cCI..."`

## Documentation OpenAPI JSON

L'API expose également la spécification OpenAPI au format JSON :

```
http://localhost:8080/v3/api-docs
```

Vous pouvez importer ce fichier dans des outils comme :
- **Postman** : Import > Link > Coller l'URL
- **Insomnia** : Import/Export > Import Data > From URL
- **OpenAPI Generator** : Pour générer des clients dans différents langages

## Ressources

- [Documentation OpenAPI](https://swagger.io/specification/)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

## Conclusion

Swagger UI est un outil puissant pour :
- 📚 Explorer l'API
- 🧪 Tester les endpoints
- 🐛 Débugger les problèmes
- 📖 Documenter l'API
- 🤝 Partager avec l'équipe

Profitez-en pour explorer toutes les fonctionnalités de l'API Hermes !
