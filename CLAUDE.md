# CLAUDE.md — Chapeau Blanc Manager (App Android Kotlin pour Odoo 18 Community)

## Architecture

- **Nom**: Chapeau Blanc Manager
- **Language**: Kotlin 2.0 + Jetpack Compose + Material 3
- **Package**: `com.chapeaublanc.manager`
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Réseau**: Retrofit + Moshi + JSON-RPC (pas REST)
- **Stockage**: Room + DataStore
- **Push**: Firebase Cloud Messaging
- **CI/CD**: GitHub Actions (à venir)

## Communication Odoo

L'app utilise le protocole **JSON-RPC** natif d'Odoo (pas REST).

Trois services:
- `common` → authenticate, version
- `db` → list databases
- `object` → execute_kw (search_read, read, create, write, fields_get)

La couche réseau est dans `core/network/`:
- `JsonRPCProvider.kt` — état connexion (URL, DB, UID, session_id)
- `OdooApiService.kt` — API calls typés

## Structure des dossiers

```
com.chapeaublanc.manager/
├── OdooApplication.kt
├── MainActivity.kt
├── core/
│   ├── jsonrpc/        # Client JSON-RPC (ne pas modifier sauf bug)
│   ├── network/        # Provider + API service (ne pas modifier sauf bug)
│   ├── auth/           # SessionManager (DataStore)
│   ├── push/           # FCM service
│   └── di/             # Hilt modules
├── data/
│   └── repository/     # OdooRepository (data access)
├── domain/
│   └── model/          # Data classes Kotlin
└── ui/
    ├── navigation/     # NavGraph central
    ├── theme/          # Material 3 theme Odoo
    ├── auth/           # LoginScreen + CompanyPicker
    ├── home/           # Dashboard + menu apps
    └── generic/        # GenericModelList + GenericForm (renderer auto)
```

## Système générique auto-adaptatif

Le coeur du projet : tout module Odoo détecté apparaît automatiquement.

### Flux
1. Login → OdooRepository.fetchMenus() → `ir.ui.menu` → liste apps
2. Clic sur un menu → NavGraph.handleMenuClick() → résout le modèle Odoo
3. Navigation vers `GenericListScreen?model=crm.lead&label=Pistes`
4. GenericListScreen appelle `search_read` → affiche en LazyColumn
5. Clic sur un record → `GenericFormScreen?model=crm.lead&id=42&label=Piste`

### Quand un nouveau module est installé
L'utilisateur rafraîchit le menu (pull-to-refresh ou bouton).
→ Les nouveaux menus apparaissent automatiquement.
→ GenericListScreen + GenericFormScreen savent gérer n'importe quel modèle.

### Quand un module a besoin d'UI custom
Certains modules (photo upload, dashboard IA, SumUp POS) ont besoin d'UI native spécifique.
Dans ce cas, ajouter un dossier `ui/<module>/` avec ses propres écrans.

**Prompt Claude Code pour ajouter un module custom:**

```
Ajoute le module [NOM_MODULE] à l'app Odoo Native.
Modèle Odoo: [nom.technique]
URL: https://e-mage.photo
Fonctionnalités mobiles: [liste des features]
Écrans: [List, Form, Kanban, ou Custom]
Actions spécifiques: [upload, scan, etc.]

Ajoute les fichiers dans ui/[module]/ et mets à jour NavGraph.kt.
Puis ./gradlew assembleDebug et push.
```

## Build

```bash
# Debug APK
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk

# Release APK
./gradlew assembleRelease
```

## URLs importantes
- Production: `https://e-mage.photo`
- DB: `chapeau_blanc_group`
- Société holding: Chapeau Blanc Group (id=4)
- Filiales: e-Mâge(2), Ayashee(6), Sugar Daddy(7), SylverPrint(8)
