# BoraStock - Système de Gestion de Stock

## 🎯 Vue d'ensemble

BoraStock est un système de gestion de stock moderne et modulaire développé en Kotlin. Il suit les principes de l'architecture Clean Architecture et offre une structure claire, réutilisable et facilement testable.

## 🏗️ Architecture

Le projet suit une architecture en couches bien définie :

```
src/main/kotlin/org/babetech/borastock/
├── domain/                 # Couche métier (Domain Layer)
│   ├── model/             # Entités métier
│   ├── repository/        # Interfaces des repositories
│   └── service/           # Services métier
├── application/           # Couche application
│   └── cli/              # Interface en ligne de commande
├── infrastructure/        # Couche infrastructure
│   ├── config/           # Configuration
│   ├── di/               # Injection de dépendances
│   └── repository/       # Implémentations des repositories
└── Main.kt               # Point d'entrée
```

### Couches

1. **Domain** : Contient la logique métier pure, indépendante de toute technologie
2. **Application** : Orchestration des cas d'usage et interfaces utilisateur
3. **Infrastructure** : Implémentations techniques (base de données, configuration, etc.)

## 🚀 Fonctionnalités

### Gestion des Produits
- ✅ Création, modification, suppression de produits
- ✅ Gestion des catégories
- ✅ Suivi des stocks avec seuils d'alerte
- ✅ Recherche et filtrage

### Gestion des Fournisseurs
- ✅ Gestion complète des fournisseurs
- ✅ Informations de contact et adresses
- ✅ Statut actif/inactif

### Mouvements de Stock
- ✅ Enregistrement des entrées et sorties
- ✅ Historique complet des mouvements
- ✅ Traçabilité par utilisateur

### Tableau de Bord
- ✅ Vue d'ensemble des stocks
- ✅ Alertes stock faible
- ✅ Statistiques et métriques
- ✅ Mouvements récents

## 🛠️ Technologies

- **Kotlin** : Langage principal
- **Kotlinx Coroutines** : Programmation asynchrone
- **Kotlinx Serialization** : Sérialisation JSON
- **Kotlinx DateTime** : Gestion des dates
- **Kotlin Logging** : Journalisation
- **JUnit 5** : Tests unitaires
- **MockK** : Mocking pour les tests

## 📦 Installation et Utilisation

### Prérequis
- JDK 17 ou supérieur
- Gradle 7.0 ou supérieur

### Compilation
```bash
./gradlew build
```

### Exécution
```bash
./gradlew run
```

### Tests
```bash
./gradlew test
```

## 🎮 Interface CLI

L'application propose une interface en ligne de commande intuitive :

```
borastock> help
📚 Commandes disponibles:

  help                    - Affiche cette aide
  dashboard               - Affiche le tableau de bord
  product list            - Liste tous les produits
  product create          - Crée un nouveau produit
  product show <id>       - Affiche les détails d'un produit
  supplier list           - Liste tous les fournisseurs
  supplier create         - Crée un nouveau fournisseur
  stock summary           - Affiche le résumé des stocks
  movement entry          - Enregistre une entrée de stock
  movement exit           - Enregistre une sortie de stock
  exit                    - Quitte l'application
```

## 🧪 Tests

Le projet inclut une suite de tests complète :

- **Tests unitaires** : Validation de la logique métier
- **Tests d'intégration** : Validation des services
- **Mocking** : Isolation des dépendances

Exécuter les tests :
```bash
./gradlew test --info
```

## 🔧 Configuration

La configuration se fait via la classe `AppConfig` :

```kotlin
data class AppConfig(
    val database: DatabaseConfig = DatabaseConfig(),
    val logging: LoggingConfig = LoggingConfig(),
    val application: ApplicationConfig = ApplicationConfig()
)
```

## 📊 Modèle de Données

### Entités Principales

- **Product** : Produit avec stock, prix, catégorie
- **Supplier** : Fournisseur avec informations de contact
- **StockMovement** : Mouvement d'entrée/sortie de stock
- **User** : Utilisateur avec rôles et permissions
- **Category** : Catégorie de produits

### Relations
- Un produit appartient à une catégorie
- Un produit a un fournisseur
- Les mouvements sont liés aux produits et utilisateurs

## 🎯 Principes de Conception

### Clean Architecture
- **Indépendance des frameworks** : La logique métier ne dépend d'aucun framework
- **Testabilité** : Chaque couche peut être testée indépendamment
- **Indépendance de l'UI** : L'interface peut être changée sans affecter la logique
- **Indépendance de la base de données** : Le système peut fonctionner avec différents types de stockage

### SOLID Principles
- **Single Responsibility** : Chaque classe a une seule responsabilité
- **Open/Closed** : Ouvert à l'extension, fermé à la modification
- **Liskov Substitution** : Les implémentations peuvent être substituées
- **Interface Segregation** : Interfaces spécifiques plutôt que générales
- **Dependency Inversion** : Dépendance aux abstractions, pas aux implémentations

## 🔄 Extensibilité

Le système est conçu pour être facilement extensible :

### Nouveaux Types de Stockage
Implémentez simplement les interfaces de repository :
```kotlin
class DatabaseProductRepository : ProductRepository {
    // Implémentation avec base de données
}
```

### Nouvelles Interfaces
Ajoutez de nouvelles interfaces (Web, Mobile, etc.) dans la couche application :
```kotlin
class WebController(private val productService: ProductService) {
    // Endpoints REST
}
```

### Nouveaux Services
Étendez la logique métier avec de nouveaux services :
```kotlin
class AnalyticsService(
    private val productRepository: ProductRepository,
    private val movementRepository: StockMovementRepository
) {
    // Logique d'analyse
}
```

## 🤝 Contribution

1. Fork le projet
2. Créez une branche pour votre fonctionnalité
3. Committez vos changements
4. Poussez vers la branche
5. Ouvrez une Pull Request

## 📄 Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## 🎉 Remerciements

Merci à tous les contributeurs qui ont participé à ce projet !

---

**BoraStock** - Gestion de stock moderne et modulaire 🚀