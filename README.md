# 📦 Inventaire API — Gestion de Produits & Stocks

API REST complète pour la gestion d'un inventaire de produits avec suivi des stocks et alertes automatiques, développée avec Spring Boot 3, Spring Data JPA et Swagger/OpenAPI.

---

## 🚀 Fonctionnalités

- **CRUD Produits** : Créer, lire, modifier et supprimer des produits
- **Alertes de stock** : Détection automatique des produits en stock faible (< 5 unités)
- **3 statuts de stock** : `NORMAL`, `FAIBLE`, `RUPTURE`
- **Recherche** par nom (insensible à la casse)
- **Documentation interactive** Swagger UI
- **H2 in-memory** pour le développement (zéro configuration)
- **MySQL/PostgreSQL** pour la production

---

## 📋 Prérequis

| Outil | Version |
|-------|---------|
| Java  | 17+     |
| Maven | 3.8+    |

> Aucune base de données à installer en mode développement.

---

## 🛠️ Installation

```bash
# 1. Cloner le dépôt
git clone https://github.com/votre-username/inventaire-api.git
cd inventaire-api

# 2. Compiler
mvn clean install
```

---

## ⚙️ Configuration de la base de données

### Développement — H2 In-Memory (défaut)

Aucune configuration requise. 10 produits de test sont insérés automatiquement au démarrage.

```properties
# application-dev.properties (déjà configuré)
spring.datasource.url=jdbc:h2:mem:inventairedb
spring.datasource.username=sa
spring.datasource.password=
```

**Console H2** : [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL : `jdbc:h2:mem:inventairedb`
- Username : `sa` | Password : *(vide)*

---

### Production — MySQL

```sql
-- Créer la base de données
CREATE DATABASE inventairedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'invuser'@'localhost' IDENTIFIED BY 'motdepasse';
GRANT ALL PRIVILEGES ON inventairedb.* TO 'invuser'@'localhost';
```

```bash
# Variables d'environnement
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=inventairedb
export DB_USERNAME=invuser
export DB_PASSWORD=motdepasse
```

---

## ▶️ Lancement

```bash
# Développement (H2)
mvn spring-boot:run

# Production (MySQL)
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Via JAR
mvn clean package
java -jar target/inventaire-api-1.0.0.jar
```

L'application démarre sur **http://localhost:8080**

---

## 📚 Accès à Swagger

| Interface | URL |
|-----------|-----|
| **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| OpenAPI JSON | [http://localhost:8080/api-docs](http://localhost:8080/api-docs) |

---

## 🔗 Endpoints disponibles

### Produits

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/produits` | Créer un produit |
| `GET` | `/api/produits` | Lister tous les produits |
| `GET` | `/api/produits/{id}` | Obtenir un produit par ID |
| `PUT` | `/api/produits/{id}` | Mettre à jour un produit |
| `DELETE` | `/api/produits/{id}` | Supprimer un produit |

### Stocks

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/produits/stock/alertes` | Rapport des alertes stock faible |
| `GET` | `/api/produits/recherche?nom=xxx` | Rechercher par nom |

---

## 📡 Exemples de requêtes

### Créer un produit

```http
POST http://localhost:8080/api/produits
Content-Type: application/json

{
  "nom": "Ordinateur portable Dell XPS 15",
  "prix": 1299.99,
  "quantiteStock": 25
}
```

**Réponse (201 Created) :**
```json
{
  "success": true,
  "message": "Produit créé avec succès",
  "data": {
    "id": 1,
    "nom": "Ordinateur portable Dell XPS 15",
    "prix": 1299.99,
    "quantiteStock": 25,
    "stockFaible": false,
    "statutStock": "NORMAL",
    "dateCreation": "2024-06-15T10:00:00",
    "dateModification": "2024-06-15T10:00:00"
  },
  "timestamp": "2024-06-15T10:00:01"
}
```

---

### Lister tous les produits

```http
GET http://localhost:8080/api/produits
```

---

### Obtenir un produit

```http
GET http://localhost:8080/api/produits/1
```

---

### Mettre à jour un produit

```http
PUT http://localhost:8080/api/produits/1
Content-Type: application/json

{
  "nom": "Ordinateur portable Dell XPS 15 - Édition 2024",
  "prix": 1399.99,
  "quantiteStock": 3
}
```

**Réponse (200 OK) — stock passé en FAIBLE :**
```json
{
  "success": true,
  "message": "Produit mis à jour avec succès",
  "data": {
    "id": 1,
    "nom": "Ordinateur portable Dell XPS 15 - Édition 2024",
    "prix": 1399.99,
    "quantiteStock": 3,
    "stockFaible": true,
    "statutStock": "FAIBLE",
    "dateCreation": "2024-06-15T10:00:00",
    "dateModification": "2024-06-20T14:30:00"
  }
}
```

---

### Supprimer un produit

```http
DELETE http://localhost:8080/api/produits/1
```

---

### 🔔 Alertes de stock faible

```http
GET http://localhost:8080/api/produits/stock/alertes
```

**Réponse (200 OK) :**
```json
{
  "success": true,
  "message": "⚠️  3 produit(s) nécessitent un réapprovisionnement. 🚨 1 en rupture totale !",
  "data": {
    "seuilAlerte": 5,
    "nombreProduitsCritiques": 3,
    "nombreProduitsEnRupture": 1,
    "dateRapport": "2024-06-20T15:00:00",
    "produitsEnAlerte": [
      {
        "id": 8,
        "nom": "Hub USB-C 7-en-1",
        "prix": 45.00,
        "quantiteStock": 0,
        "stockFaible": true,
        "statutStock": "RUPTURE"
      },
      {
        "id": 3,
        "nom": "Souris sans fil Microsoft",
        "prix": 49.99,
        "quantiteStock": 3,
        "stockFaible": true,
        "statutStock": "FAIBLE"
      }
    ]
  }
}
```

---

### Rechercher par nom

```http
GET http://localhost:8080/api/produits/recherche?nom=logitech
```

---

## 📊 Statuts de stock

| Statut | Condition | Description |
|--------|-----------|-------------|
| `NORMAL` | quantité ≥ 5 | Stock suffisant |
| `FAIBLE` | 0 < quantité < 5 | Réapprovisionnement recommandé |
| `RUPTURE` | quantité = 0 | Rupture totale — action urgente |

> Le seuil (5 par défaut) est configurable via `inventaire.stock.seuil-alerte` dans `application.properties`.

---

## 🏗️ Structure du projet

```
inventaire-api/
├── src/
│   ├── main/
│   │   ├── java/com/inventaire/api/
│   │   │   ├── InventaireApiApplication.java
│   │   │   ├── config/
│   │   │   │   ├── OpenApiConfig.java        # Swagger / OpenAPI
│   │   │   │   └── WebConfig.java            # CORS
│   │   │   ├── controller/
│   │   │   │   └── ProduitController.java    # 7 endpoints REST
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java          # Enveloppe JSON générique
│   │   │   │   ├── ProduitRequest.java       # DTO entrée (création/MAJ)
│   │   │   │   ├── ProduitResponse.java      # DTO sortie produit
│   │   │   │   ├── StockAlerteResponse.java  # DTO rapport d'alertes
│   │   │   │   └── StatutStock.java          # Enum NORMAL / FAIBLE / RUPTURE
│   │   │   ├── entity/
│   │   │   │   └── Produit.java              # Entité JPA
│   │   │   ├── exception/
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── repository/
│   │   │   │   └── ProduitRepository.java    # Requêtes JPA
│   │   │   └── service/
│   │   │       ├── ProduitService.java       # Interface
│   │   │       └── ProduitServiceImpl.java   # Implémentation
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties    # H2
│   │       ├── application-prod.properties   # MySQL
│   │       └── data.sql                      # 10 produits de test
│   └── test/
│       └── java/com/inventaire/api/
│           └── ProduitServiceTest.java       # 11 tests unitaires
└── pom.xml
```

---

## 🧪 Procédures de test

### Tests unitaires

```bash
# Lancer les tests
mvn test

# Avec rapport de couverture
mvn test jacoco:report
# → target/site/jacoco/index.html
```

Les tests couvrent :
- Création de produit (stock normal + stock faible initial)
- Consultation (liste, par ID, ID introuvable)
- Mise à jour
- Suppression (succès + introuvable)
- Alertes de stock (avec/sans alertes, statuts NORMAL/FAIBLE/RUPTURE)

### Tests manuels via Swagger UI

1. Démarrer l'application : `mvn spring-boot:run`
2. Ouvrir : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
3. Utiliser le bouton **"Try it out"** sur chaque endpoint

### Tests avec curl

```bash
# Créer un produit
curl -X POST http://localhost:8080/api/produits \
  -H "Content-Type: application/json" \
  -d '{"nom":"Test produit","prix":9.99,"quantiteStock":2}'

# Lister tous les produits
curl http://localhost:8080/api/produits

# Alertes de stock
curl http://localhost:8080/api/produits/stock/alertes

# Recherche
curl "http://localhost:8080/api/produits/recherche?nom=dell"
```

---

## ⚠️ Gestion des erreurs

Toutes les erreurs retournent une réponse JSON standardisée :

```json
{
  "success": false,
  "message": "Produit non trouvé(e) avec id : '99'",
  "timestamp": "2024-06-20T15:00:00"
}
```

| Code | Situation |
|------|-----------|
| `200` | Succès |
| `201` | Ressource créée |
| `400` | Données invalides / validation |
| `404` | Ressource introuvable |
| `409` | Conflit règle métier |
| `500` | Erreur interne |

---

## 🔧 Stack Technologique

| Technologie | Version | Rôle |
|-------------|---------|------|
| Java | 17 | Langage |
| Spring Boot | 3.2.5 | Framework |
| Spring Data JPA | 3.2.5 | Persistance |
| Hibernate | 6.x | ORM |
| H2 Database | 2.x | Dev |
| MySQL | 8.x | Production |
| Lombok | 1.18.x | Boilerplate |
| Springdoc OpenAPI | 2.5.0 | Swagger |
| JUnit 5 + Mockito | 5.x | Tests |

---

## 📄 Licence

MIT License — Voir [LICENSE](LICENSE)
