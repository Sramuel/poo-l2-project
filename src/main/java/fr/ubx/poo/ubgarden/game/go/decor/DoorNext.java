package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class DoorNext extends Decor {
    boolean is_open;


    public DoorNext(Position position, boolean open) {
        super(position);
        this.is_open = open;
    }
    public boolean getIsOpen(){
        return this.is_open;
    }

    public void setIs_open(){
        this.is_open = true;
    }

    @Override
    public boolean walkableBy(Gardener gardener) {
        return super.walkableBy(gardener);
    }
}