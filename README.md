# UBGarden

Jeu JavaFX dans lequel le jardinier explore des niveaux et interagit avec les objets du jardin.

## Pré-requis

- JDK 17
- Une connexion internet lors du premier build pour télécharger les dépendances Gradle

## Lancer le jeu

Linux/macOS :

```bash
chmod +x gradlew
./gradlew run
```

Windows :

```powershell
.\gradlew.bat run
```

## Tests et archive de soumission

```bash
./gradlew test
./gradlew submissionZip
```

L'application est configurée dans `build.gradle` avec JavaFX 17 et la classe principale `fr.ubx.poo.ubgarden.Main`.

"'## Avertissement

Ce dépôt contient un projet étudiant réalisé à des fins pédagogiques et académiques. Il peut contenir des limitations, des choix simplifiés ou des failles de sécurité ; il n est pas destiné à un usage professionnel, à la production ou au traitement de données réelles. Utilisez-le uniquement dans un environnement de test et adaptez-le avant toute réutilisation.
'"
