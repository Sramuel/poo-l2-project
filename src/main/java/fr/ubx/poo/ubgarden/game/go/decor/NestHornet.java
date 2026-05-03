package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Hornet;

import java.util.Random;

public class NestHornet extends Decor {

    private final Timer spawnTimer;
    private final Game game;
    private static final Random randomGenerator = new Random();

    public NestHornet(Position position, Game game) {
        super(position);
        this.game = game;
        this.spawnTimer = new Timer(10000);//10 secondes
        this.spawnTimer.start();
    }

    @Override
    public boolean walkableBy(Gardener gardener) {
        return gardener.canWalkOn(this);
    }

    @Override
    public void update(long now) {
        super.update(now);
        spawnTimer.update(now);
        if (!spawnTimer.isRunning()) {
            Hornet newHornet = new Hornet(game, getPosition());
            if (game.world().getGrid() instanceof Level currentLevel) {
                currentLevel.getHornets().add(newHornet);

                int spawned = 0;
                int attempts = 0;
                while (spawned<2 && attempts < 10) { //on essaie de poser les deux bombes jusqu'à 10 fois (pour essayer de garder 2 bombes/frelon)
                    Position randomPos = random_pos(); //génération d'une position aléatoire
                    Decor decor = currentLevel.get(randomPos);
                    if (decor instanceof Grass && decor.getBonus() == null) { //poser la bombe où c'est libre
                        decor.setBonus(new Insecticide(game, randomPos, decor));
                        spawned++;
                    }
                    attempts++;
                }
            }
            spawnTimer.start();
        }
    }

    public Position random_pos() {
        int level = game.world().currentLevel();
        int width = game.world().getGrid().width();
        int height = game.world().getGrid().height();
        int x = randomGenerator.nextInt(width);
        int y = randomGenerator.nextInt(height);
        return new Position(level,x,y);
    }
}