package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.personage.Gardener;


public class Game {

    private final Configuration configuration;
    private final World world;
    private final Gardener gardener;
    private boolean switchLevelRequested = false;
    private int switchLevel;
    public Game(World world, Configuration configuration, Position gardenerPosition) {
        this.configuration = configuration;
        this.world = world;
        gardener = new Gardener(this, gardenerPosition);
    }

    public Configuration configuration() {
        return configuration;
    }

    public Gardener getGardener() {
        return this.gardener;
    }

    public World world() {
        return world;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public int getSwitchLevel() {
        return switchLevel;
    }

    public void switchLevel() {
        if (switchLevelRequested) {
            //on change le numéro du niveau dans le "world"
            world.setCurrentLevel(switchLevel);
            //on nettoie la requête
            clearSwitchLevel();
        }
    }

    public void requestSwitchLevel(int level) {
        int currentIndex = world.currentLevel();
        Map currentMap = world.getGrid(currentIndex);

        //si on veut passer au niveau suivant : le gardener envoie currentLevel+1
        if (level > currentIndex) {
            if (currentMap instanceof Level currentLevel) {
                //donc on vérifie ici si la condition des carottes est remplie
                if (currentLevel.hasCarrots()) {
                    return;
                }
            }
        }
        //si on descend ou si on a le droit de monter (tous les autres cas), on pourra changer de niveau
        this.switchLevel = level;
        this.switchLevelRequested = true;
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }

}
