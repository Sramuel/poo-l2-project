package fr.ubx.poo.ubgarden.game.view;

import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.decor.DoorNext;
import javafx.scene.layout.Pane;

public class SpriteDoor extends Sprite {
    public SpriteDoor(Pane layer, GameObject gameObject) {
        super(layer, null, gameObject);
        updateImage();
    }

    @Override
    public void updateImage() {
        DoorNext door = (DoorNext) getGameObject();
        ImageResourceFactory factory = ImageResourceFactory.getInstance();
        setImage(factory.getDoor(door.getIsOpen()));
    }
}
