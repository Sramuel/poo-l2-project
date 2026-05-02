package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;

public class NestWasp extends Decor {

    private final Timer spawnTimer;
    private final Game game;

    public NestWasp(Position position,Game game) {
        super(position);
        this.game=game;
        this.spawnTimer = new Timer(5000); //5sec
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
            Wasp newWasp = new Wasp(game, getPosition());
            if (game.world().getGrid() instanceof Level) {
                Level currentLevel = (Level) game.world().getGrid();
                currentLevel.getWasps().add(newWasp);
            }
            spawnTimer.start();
        }
    }
}