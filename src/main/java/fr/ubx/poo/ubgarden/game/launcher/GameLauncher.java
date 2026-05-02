package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class GameLauncher {

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int waspMoveFrequency = integerProperty(properties, "waspMoveFrequency", 2);
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);

        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        long energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1_000);
        long diseaseDuration = integerProperty(properties, "diseaseDuration", 5_000);

        return new Configuration(gardenerEnergy, energyBoost, energyRecoverDuration, diseaseDuration, waspMoveFrequency, hornetMoveFrequency);
    }

    public Game load(File file) {
        Properties emptyConfig = new Properties();
        try (FileReader reader = new FileReader(file)) {
            emptyConfig.load(reader); //on récupère les données du fichier .properties passé en paramètre
            Configuration configuration = getConfiguration(emptyConfig);

            int numLevels = integerProperty(emptyConfig, "levels", 1); //on récupère le nombre de niveaux
            World world = new World(numLevels); //initialisation d'un monde vide avec le nombre de niveaux
            MapRepoString mapRepo = new MapRepoString();

            //chargement du premier niveau
            String level1Data = emptyConfig.getProperty("level1"); //on récupère le niveau 1 depuis le fichier
            if (level1Data == null)
                throw new RuntimeException("Level 1 data missing");
            MapLevel mapLevel1 = mapRepo.load(level1Data);
            Position gardenerPosition = mapLevel1.getGardenerPosition();
            if (gardenerPosition == null)
                throw new RuntimeException("Gardener not found");

            //création du jeu avec le premier niveau
            Game game = new Game(world, configuration, gardenerPosition);
            Map level1 = new Level(game, 1, mapLevel1);
            world.put(1, level1);

            //gestion de plusieurs niveaux
            if (numLevels > 1){
                for (int i = 2; i <= numLevels; i++) {
                    String levelData = emptyConfig.getProperty("level" + i);
                    if (levelData != null) {
                        MapLevel mapLevel = mapRepo.load(levelData); //création d'un espace pour le nouveau niveau

                        //on crée le niveau et on l'ajoute au monde
                        Map level = new Level(game, i, mapLevel);
                        world.put(i, level);
                    }
                }
            }
            return game;
        } catch (IOException e) {
            throw new RuntimeException("Error loading file", e);
        }
    }

    public Game load() {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefaultStart();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        Configuration configuration = getConfiguration(emptyConfig);
        World world = new World(1);
        Game game = new Game(world, configuration, gardenerPosition);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }

}
