/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.engine.GameEngine;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.Flowers;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import fr.ubx.poo.ubgarden.game.go.decor.Tree;
import javafx.scene.paint.Color;

import fr.ubx.poo.ubgarden.game.engine.Timer;

public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private int energy;
    private Direction direction;
    private boolean moveRequested = false;
    private boolean won = false;

    private Timer energyRecoverTimer;

    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();

        this.energyRecoverTimer = new Timer(game.configuration().energyRecoverDuration());
        this.energyRecoverTimer.start();
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
        this.won = true;
    }

    public int getEnergy() {
        return this.energy;
    }

    public boolean getWin() { return this.won; }

    public String toString_energy(){
        String s= "";
        return s+getEnergy();
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
        int x = nextPos.x();
        int y = nextPos.y();
        int width = game.world().getGrid().width();
        int height = game.world().getGrid().height();

        //collisions avec les bordures du plateau
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }
        Decor next = game.world().getGrid().get(nextPos);

        //collisions avec les arbres / les fleurs
        if (next instanceof Tree || next instanceof Flowers){
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
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        if (moveRequested) {
            if (canMove(direction)) {
                move(direction);
                this.energy = getEnergy() - next.energyConsumptionWalk();
            }
            energyRecoverTimer.start();
        }else {
            energyRecoverTimer.update(now);
            if (!energyRecoverTimer.isRunning()) {
                int maxEnergy = game.configuration().gardenerEnergy();
                if (this.energy<maxEnergy) {
                    this.energy++;
                }
                energyRecoverTimer.start();
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
