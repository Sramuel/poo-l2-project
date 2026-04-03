/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import fr.ubx.poo.ubgarden.game.go.decor.Tree;
import fr.ubx.poo.ubgarden.game.launcher.MapEntity;

public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private final int energy;
    private Direction direction;
    private boolean moveRequested = false;

    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
    }

    @Override
    public void pickUp(EnergyBoost energyBoost) {
// TODO
        System.out.println("I am taking the boost, I should do something ...");

    }

    public void pickUp(PoisonedApple poisonedApple){
        System.out.println("I am PoisonedApple");
    }

    public void pickUp(Carrots carrots){
        System.out.println("I am Carrots");
    }

    public void pickUp(Insecticide insecticide) {
        System.out.println("I am Insecticide");
    }

    public void pickUp(Hedgehog hedgehog) {
        System.out.println("Winner");
    }


    public int getEnergy() {
        return this.energy;
    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        if (next instanceof Tree ){
            return false;
        }
        return true;
    }

    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        setPosition(nextPos);
        if (next != null)
            next.pickUpBy(this);
        return nextPos;
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                move(direction);
            }
        }
        moveRequested = false;
    }

    public void hurt(int damage) {
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }


}
