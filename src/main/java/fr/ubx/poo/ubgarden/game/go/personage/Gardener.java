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

import java.util.ArrayList;
import java.util.List;

public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    private int diseaseLevel = 1;

    private int energy;
    private Direction direction;
    private boolean moveRequested = false;
    private boolean won = false;

    private Timer energyRecoverTimer;

    private int carrots =0;
    private int bombs =0;
    private List<Timer> diseaseTimers = new ArrayList<>(); //gère plusieurs trognons en même temps

    public int getCarrots() {
        return carrots;
    }

    public int getBombs() {
        return bombs;
    }

    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();

        this.energyRecoverTimer = new Timer(game.configuration().energyRecoverDuration());
        this.energyRecoverTimer.start();
    }

    @Override
    public void pickUp(EnergyBoost energyBoost) {
        int maxEnergy=game.configuration().gardenerEnergy();
        int boostval=game.configuration().energyBoost();
        this.energy=Math.min(this.energy+boostval, maxEnergy); //pour pas dépasser l'énergie maximum
        this.diseaseLevel = 1;
        this.diseaseTimers.clear();
        energyBoost.remove();

    }

    public void pickUp(PoisonedApple poisonedApple){
        this.diseaseLevel++;
        Timer diseaseTimer= new Timer(game.configuration().diseaseDuration());
        diseaseTimer.start();
        this.diseaseTimers.add(diseaseTimer);
        poisonedApple.remove();
    }

    public void pickUp(Carrots carrots){
        this.carrots++;
        carrots.remove();
    }

    public void pickUp(Insecticide insecticide) {
        this.bombs++;
        insecticide.remove();
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
        //GESTION DU DEPLACEMENT ET DE LA RECUPERATION D'ENERGIE
        if (moveRequested) {
            if (canMove(direction)) {
                move(direction);
                this.energy = getEnergy() - next.energyConsumptionWalk()*this.diseaseLevel;
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
        //GESTION DE LA FIN DES MALADIES (les trognons de pomme)
        for (int i = diseaseTimers.size() - 1; i >= 0; i--) {
            Timer t = diseaseTimers.get(i);
            t.update(now);
            if (!t.isRunning()) {
                diseaseTimers.remove(i);
                diseaseLevel--;
            }
        }
        //GESTION DE LA DEFAITE
        if (this.energy <= 0) {
            this.energy = 0;
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
