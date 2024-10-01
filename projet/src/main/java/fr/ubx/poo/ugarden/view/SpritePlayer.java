/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.view;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.go.decor.Carrots;
import fr.ubx.poo.ugarden.go.personage.Player;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpritePlayer extends Sprite {


    private Player player;

    private boolean toggleEff = false;

    private ColorAdjust colorAdjust;

    public SpritePlayer(Pane layer, Player player, ColorAdjust colorAdjust) {
        super(layer, null, player, colorAdjust);
        this.player = player;
        this.colorAdjust = colorAdjust;
        updateImage();
    }

    public void br(){

        if(!player.isInvincible()){
            colorAdjust.setBrightness(0.0);
            return;
        }

        if(!toggleEff) {
            colorAdjust.setBrightness(0.5);
            toggleEff = true;
        } else{
            colorAdjust.setBrightness(0.0);
            toggleEff = false;
        }
        getGameObject().setModified(true);
    }

    @Override
    public void updateImage() {
        Player player = (Player) getGameObject();
        Image image = getImage(player.getDirection());
        setImage(image);
    }

    private Image getImage(Direction direction) {
        return ImageResourceFactory.getInstance().getPlayer(direction);
    }
}
