# Guide d'utilisation de l'endpoint `createPropertyWithPictures` dans Swagger UI

## Problème résolu

L'endpoint `/api/properties/with-pictures` fonctionne avec `curl` mais peut poser problème dans Swagger UI en raison de la complexité des requêtes multipart/form-data avec JSON + fichiers.

## Solution implémentée

### 1. Annotations Swagger améliorées

L'endpoint a été configuré avec :
- `@Encoding` pour spécifier le type de contenu de chaque partie
- Documentation claire dans la description
- Exemple JSON intégré dans `CreatePropertyRequest`

### 2. Comment utiliser dans Swagger UI

#### Étape 1 : Authentification
1. Connectez-vous via `/api/auth/login` ou `/api/auth/register`
2. Cliquez sur **Authorize** en haut de Swagger UI
3. Entrez : `Bearer {votre-token}`

#### Étape 2 : Accéder à l'endpoint
1. Naviguez vers `POST /api/properties/with-pictures`
2. Cliquez sur **Try it out**

#### Étape 3 : Remplir le formulaire

**Pour le champ `property` (JSON):**

Collez directement ce JSON (sans guillemets supplémentaires) :

```json
{
  "name": "Appartement Centre Ville",
  "description": "Magnifique appartement avec vue panoramique",
  "propertyType": "APARTMENT",
  "street": "123 Rue de la Paix",
  "city": "Paris",
  "postalCode": "75001",
  "country": "France",
  "area": 85.5,
  "status": "AVAILABLE",
  "photos": []
}
```

**Pour le champ `files`:**
1. Cliquez sur **Choose Files**
2. Sélectionnez une ou plusieurs images

#### Étape 4 : Exécuter
Cliquez sur **Execute**

## Types de propriétés disponibles

```
HOUSE          - Maison
APARTMENT      - Appartement
STUDIO         - Studio
OFFICE         - Bureau
COMMERCIAL_SPACE - Espace commercial
LAND           - Terrain
PARKING        - Parking
OTHER          - Autre
```

## Statuts disponibles

```
AVAILABLE - Disponible
RENTED    - Loué
INACTIVE  - Inactif
```

## Exemple avec curl (pour référence)

```bash
curl -X POST "http://localhost:8080/api/properties/with-pictures" \
  -H "Authorization: Bearer {votre-token}" \
  -F 'property={
    "name": "Appartement Centre Ville",
    "description": "Magnifique appartement avec vue panoramique",
    "propertyType": "APARTMENT",
    "street": "123 Rue de la Paix",
    "city": "Paris",
    "postalCode": "75001",
    "country": "France",
    "area": 85.5,
    "status": "AVAILABLE",
    "photos": []
  };type=application/json' \
  -F "files=@image1.jpg" \
  -F "files=@image2.jpg"
```

## Dépannage

### Erreur 400 - Bad Request
- Vérifiez que le JSON est valide (utilisez jsonlint.com)
- Assurez-vous que tous les champs obligatoires sont présents
- Vérifiez le format des valeurs (area doit être un nombre, propertyType doit correspondre à une des valeurs listées)

### Erreur 401 - Unauthorized
- Vérifiez que vous êtes bien authentifié
- Vérifiez que le token n'a pas expiré
- Cliquez sur **Authorize** et entrez : `Bearer {votre-token}`

### Erreur 415 - Unsupported Media Type
- Vérifiez que vous utilisez bien l'endpoint multipart
- Dans Swagger, assurez-vous de ne pas modifier le Content-Type dans les headers

## Modifications techniques apportées

### PropertyController.java
```java
@PostMapping(value = "/with-pictures", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@Operation(
    summary = "Créer un nouveau bien immobilier avec des fichiers",
    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            encoding = {
                @Encoding(name = "property", contentType = MediaType.APPLICATION_JSON_VALUE),
                @Encoding(name = "files", contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            }
        )
    )
)
```

### CreatePropertyRequest.java
- Ajout de l'annotation `@Schema` avec un exemple JSON
- Cet exemple s'affiche automatiquement dans Swagger UI

## Architecture respectée

✅ Architecture hexagonale maintenue
✅ Séparation des responsabilités
✅ FileUseCase utilisé pour la gestion des fichiers
✅ Pas d'accès direct aux repositories JPA depuis le contrôleur

## Bucket System

Les fichiers sont maintenant organisés par bucket type :
- `PROPERTY` : Fichiers liés aux propriétés
- Extensible pour d'autres types (USER, DOCUMENT, etc.)
