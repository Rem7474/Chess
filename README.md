# Chess JSON JavaFX

Jeu d’échecs Java piloté par JSON, avec interface graphique JavaFX et mode console interactif.

## Fonctionnalités principales

- **Interface graphique JavaFX complète** : affichage du plateau, déplacement des pièces, chrono, démo automatique, boutons de contrôle, surlignage des coups, gestion de la fin de partie.
- **Mode console interactif** : affichage ASCII, saisie des coups, alternance des joueurs, gestion des règles de base.
- **Configuration 100% JSON** : toutes les pièces (classiques et personnalisées) sont définies dans `src/pieces.json` (nom, unicode/emoji, image, type, règles de déplacement, position initiale, etc.).
- **Activation/désactivation dynamique** des pièces personnalisées via l’UI (interrupteurs), avec placement symétrique automatique côté noir.
- **Démo automatique** : bouton pour lancer/mettre en pause une séquence de coups aléatoires, surlignage dynamique, gestion du chrono.
- **Séparation stricte** de la logique métier (modèle) et de l’affichage (console/graphique).
- **Robustesse** : restauration exacte des pièces classiques à la désactivation, gestion des cas limites, aucune superposition imprévue.

## Structure du projet

```
main/
├── src/
│   ├── controller/         # Contrôleur principal (Jeu.java)
│   ├── main/               # Point d’entrée (Main.java)
│   ├── model/              # Modèle métier (Echiquier, PiecePersonnalisee, PieceLoader, ...)
│   ├── view/               # Affichages (console, JavaFX, démo)
│   ├── images/             # Images des pièces
│   └── pieces.json         # Configuration des pièces (classiques et personnalisées)
├── bin/                    # Fichiers compilés
├── lib/                    # Librairies éventuelles
└── README.md
```

## Lancement

### Interface graphique JavaFX

1. Compiler et lancer :
   ```sh
   javac -cp src -d bin src/model/*.java src/controller/*.java src/view/*.java && java -cp bin main.Main
   ```
2. Utiliser les boutons et interrupteurs pour jouer, activer/désactiver les pièces personnalisées, lancer la démo, etc.

### Mode console

1. Compiler et lancer :
   ```sh
   javac -cp src -d bin src/model/*.java src/controller/*.java src/view/*.java && java -cp bin controller.Jeu
   ```
2. Suivre les instructions dans le terminal.

## Choisir le mode d’interface (graphique ou console)

Le choix du mode d’interface se fait dans le fichier `src/main/Main.java` :

```java
boolean useJavaFX = false; // false = console, true = interface graphique
```

- Si `useJavaFX` est à `true`, l’interface JavaFX s’affiche.
- Si `useJavaFX` est à `false`, le mode console s’affiche.

Modifiez cette variable puis compilez et lancez :
```sh
javac -cp src -d bin src/model/*.java src/controller/*.java src/view/*.java && java -cp bin main.Main
```

Il n’y a pas de bouton ou d’option dans l’interface pour basculer dynamiquement.

## Personnalisation des pièces

- Modifier `src/pieces.json` pour ajouter, retirer ou modifier des pièces classiques ou personnalisées.
- Chaque pièce personnalisée doit avoir :
  - `name`, `unicode`, `imagePath`, `isWhite`, `startRow`, `startCol`, `type`, `isKing`, `movePattern`.
- Les pièces personnalisées sont activables/désactivables dynamiquement dans l’UI.

## Dépendances

- Java 11+
- JavaFX (modules requis : javafx.base, javafx.controls, javafx.graphics)

## Auteurs

- Projet pédagogique IN361 – Esisar
- Développement : @Rem7474

## Licence

Projet académique – Usage pédagogique uniquement.
