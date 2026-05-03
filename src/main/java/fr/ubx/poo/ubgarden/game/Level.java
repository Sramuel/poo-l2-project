package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import fr.ubx.poo.ubgarden.game.go.decor.*;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Land;
import fr.ubx.poo.ubgarden.game.go.personage.Hornet;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import fr.ubx.poo.ubgarden.game.launcher.MapEntity;
import fr.ubx.poo.ubgarden.game.launcher.MapLevel;

import java.util.*;

public class Level implements Map {

    private final int level;
    private final int width;
    private final int height;

    private final java.util.Map<Position, Decor> decors = new HashMap<>();

    public List<Wasp> getWasps() {
        return wasps;
    }

    private final List<Wasp> wasps = new LinkedList<>();

    public List<Hornet> getHornets() {
        return hornets;
    }

    private final List<Hornet> hornets = new LinkedList<>();

    public Level(Game game, int level, MapLevel entities) {
        this.level = level;
        this.width = entities.width();
        this.height = entities.height();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Position position = new Position(level, i, j);
                MapEntity mapEntity = entities.get(i, j);
                switch (mapEntity) {

                    case Tree:{
                        decors.put(position, new Tree(position));
                        break;}

                    //decor
                    case Grass:{
                        decors.put(position, new Grass(position));
                        break;}
                    case Land:{
                        decors.put(position, new Land(position));
                        break;}
                    case Flowers:{
                        decors.put(position, new Flowers(position));
                        break;}
                    case NestWasp:{
                        decors.put(position, new NestWasp(position,game));
                        break;}
                    case NestHornet:{
                        decors.put(position, new NestHornet(position,game));
                        break;}
                    case Hedgehog:{
                        decors.put(position, new Hedgehog(position));
                        break;}

                    //portes
                    case DoorNextClosed:{
                        decors.put(position, new DoorNext(position,false));
                        break;}
                    case DoorNextOpened:{
                        decors.put(position, new DoorNext(position,true));
                        break;}
                    case DoorPrevOpened:{
                        decors.put(position, new DoorPrevOpened(position));
                        break;}

                    //bonus
                    case Carrots:{
                        Decor land = new Land(position);
                        land.setBonus(new Carrots(position, land));
                        decors.put(position,land);
                        break;}
                    case PoisonedApple:{
                        Decor grass = new Grass(position);
                        grass.setBonus(new PoisonedApple(position, grass));
                        decors.put(position,grass);
                        break;}
                    case Apple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new EnergyBoost(position, grass));
                        decors.put(position, grass);
                        break;}
                    case Insecticide:{
                        Decor grass = new Grass(position);
                        grass.setBonus(new Insecticide(game, position, grass));
                        decors.put(position,grass);
                        break;}

                    default:
                        throw new RuntimeException("EntityCode " + mapEntity.name() + " not processed");
                }
            }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    public Decor get(Position position) {
        return decors.get(position);
    }

    public Collection<Decor> values() {
        return decors.values();
    }


    @Override
    public boolean inside(Position position) {
        return true;
    }

    public boolean hasCarrots() {
        for (Decor dec : values()) {
            if (dec.getBonus() != null && dec.getBonus() instanceof Carrots) {
                return true;
            }
        }
        return false;
    }

    public void updateDoors() {
        if (!hasCarrots()) {
            for (Decor dec : decors.values()) {
                if (dec instanceof DoorNext) {
                    ((DoorNext) dec).setIs_open();
                }
            }
        }
    }

}
