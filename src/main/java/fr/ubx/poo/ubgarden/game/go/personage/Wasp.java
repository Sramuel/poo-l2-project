package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.DoorNext;
import fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;
import fr.ubx.poo.ubgarden.game.go.decor.Tree;

public class Wasp extends GameObject implements Movable {

    private Direction direction;
    private final Timer moveTimer;

    public Wasp(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        long duration = 1000 / game.configuration().waspMoveFrequency(); //vitesse de la guêpe
        this.moveTimer = new Timer(duration);
        this.moveTimer.start();
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        int width = game.world().getGrid().width();
        int height = game.world().getGrid().height();

        //bloquée par les bords
        if (nextPos.x() < 0 || nextPos.y() < 0 || nextPos.x() >= width || nextPos.y() >= height) {
            return false;
        }

        Decor next = game.world().getGrid().get(nextPos);

        //bloquée par les arbres et les portes
        if (next instanceof Tree || next instanceof DoorNext || next instanceof DoorPrevOpened) {
            return false;
        }

        //elle passe sur le reste
        return true;
    }

    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
        return nextPos;
    }

    public void update(long now) {
        moveTimer.update(now);
        //si le temps est écoulé, on bouge
        if (!moveTimer.isRunning()) {
            //on choisit une direction au hasard
            this.direction = Direction.random();//on se tourne
            setModified(true); //pour prévenir le moteur graphique
            if (canMove(this.direction)) {
                move(this.direction);
            }
            moveTimer.start(); //on relance le chrono
        }
    }
}