/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.TakeVisitor;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.decor.*;
import fr.ubx.poo.ugarden.go.bonus.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

public class Player extends GameObject implements Movable, TakeVisitor, WalkVisitor {

    private Position nextDoorOpened = null;
    private Direction direction;
    private boolean moveRequested = false;
    private int lives;

    private int keys;

    private int PApples;

    private int energy;

    private int recSec;

    private boolean invincible = false;


    public int getRecSec() { return recSec;}

    public void incRecSec() { recSec++;}

    public void resetRecSec() { recSec = 0;}

    public void setInvincible(boolean invincible) { this.invincible = invincible; }

    public boolean isInvincible() { return this.invincible; }

    public int getEnergy() { return energy; }

    public int getLives() {
        return lives;
    }

    public int getPApples() { return PApples; }

    public int getKeys() { return  keys; }

    public void setEnergy(int energy) { this.energy = energy;}

    public Position getNextDoorOpened(){
        return nextDoorOpened;
    }

    public void setNextDoorOpened(Position pos){
        nextDoorOpened = pos;
    }

    public void incEnergy() {

        if(energy + 1 <= game.configuration().playerEnergy() )
            energy += 1;
    }

    public Direction getDirection() {
        return direction;
    }

    public void ouch(){
        this.lives -= 1;
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

        int w = game.world().getGrid().width();
        int h = game.world().getGrid().height();

        Position nextPos = direction.nextPosition(getPosition());

        Decor next = game.world().getGrid().get(nextPos);

         if (next instanceof Tree || next instanceof Flowers || nextPos.x() == w || nextPos.y() == h || energy == 0)
             return false;

        return nextPos.x() >= 0 && nextPos.y() >= 0;
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;
    }

    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = game.configuration().playerLives();
        this.energy =  game.configuration().playerEnergy();
        this.keys = 0;
        this.PApples = 1;
        this.recSec = 0;
    }

    public Player(Game game, Position position, Player old){
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = old.getLives();
        //this.energy =  old.getEnergy();
        this.energy = game.configuration().playerEnergy();
        this.keys = old.getKeys();
        this.PApples = old.getPApples();
        this.recSec = old.getRecSec();
    }

    @Override
    public void take(Bonus bonus) {
        if(bonus instanceof Apple){
            PApples = 1;
            energy += game.configuration().energyBoost();
        }

        if(bonus instanceof Heart)
            lives += 1;

        if(bonus instanceof PoisonedApple) {
            PApples += 1;

            int disDurSec =  game.configuration().diseaseDuration();
            AtomicInteger sec = new AtomicInteger(0);
            Timeline disDur = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                sec.getAndIncrement();
                if(sec.get() == disDurSec){
                    if(PApples - 1 >= 1)
                        PApples -= 1;
                }

            }));

            disDur.setCycleCount(disDurSec);
            disDur.play();
        }

        if(bonus instanceof Key)
            keys += 1;

    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);

        if (next != null)
            next.takenBy(this);

        int consume = 1;

        if(next instanceof Land)
            consume = 2;

        if(next instanceof Carrots)
            consume = 3;

        if(energy - consume * PApples <= 0)
            energy = 0;
        else
            energy -= consume * PApples;

        //System.out.println(String.format("Level: %d, X: %d Y: %d", getPosition().level(), getPosition().x(), getPosition().y()));

        if(next instanceof Princess)
            game.gameEngine.Win();

        if(next instanceof ClosedDoor){
            if(keys > 0){
                keys -= 1;
                game.world().getGrid().get(nextPos).remove();
                game.gameEngine.doorOpen(nextPos);
                nextPos = getPosition();
            }
        }

        if(next instanceof OpenedDoorNext) {
            if(game.world().currentLevel() + 1 <= game.world().levels())
                game.requestSwitchLevel(game.world().currentLevel() + 1);
        }

        if(next instanceof OpenedDoorPrev) {
            if(!(game.world().currentLevel() - 1 <= 0) ) {
                int toSwitch = game.world().currentLevel() - 1;
                game.requestSwitchLevel(toSwitch);
            }
        }
        resetRecSec();
        setPosition(nextPos);
    }

    @Override
    public String toString() {
        return "Player";
    }


}
