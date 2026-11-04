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
