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

public class Hornet extends GameObject implements Movable {

    private Direction direction;
    private final Timer moveTimer;
    private int hp = 2;//le frelon a 2 hp

    public Hornet(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        long duration = 1000 / game.configuration().hornetMoveFrequency();
        this.moveTimer = new Timer(duration);
        this.moveTimer.start();
    }

    public Direction getDirection() { return direction; }
    public int getHp() { return hp; }

    //il perd une vie, si il arrive à 0 il meurt
    public void takeDamage() {
        this.hp--;
        if (this.hp <= 0) {
            this.remove();
        }
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        int width = game.world().getGrid().width();
        int height = game.world().getGrid().height();
        if (nextPos.x() < 0 || nextPos.y() < 0 || nextPos.x() >= width || nextPos.y() >= height) {
            return false;
        }
        Decor next = game.world().getGrid().get(nextPos);
        if (next instanceof Tree || next instanceof DoorNext || next instanceof DoorPrevOpened) {
            return false;
        }
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
        if (!moveTimer.isRunning()) {
            this.direction = Direction.random();
            setModified(true);
            if (canMove(this.direction)) {
                move(this.direction);
            }
            moveTimer.start();
        }
    }
}