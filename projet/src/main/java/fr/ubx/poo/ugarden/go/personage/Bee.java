package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.*;
import fr.ubx.poo.ugarden.go.bonus.Bonus;
import fr.ubx.poo.ugarden.go.decor.*;

public class Bee extends GameObject implements Movable, TakeVisitor, WalkVisitor {


    private Direction direction;

    private boolean moveRequested = false;

    public Bee(Game game, Position position) {
        super(game, position);
        this.direction = Direction.LEFT;
    }


    public Direction getDirection(){
        return direction;
    }


    @Override
    public boolean canMove(Direction direction) {

        int w = game.world().getGrid().width();
        int h = game.world().getGrid().height();

        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);

        if(nextPos.x() == w || nextPos.y() == h || next instanceof Tree || next instanceof OpenedDoorPrev || next instanceof ClosedDoor || next instanceof OpenedDoorNext)
            return false;

        return nextPos.x() >= 0 && nextPos.y() >= 0;
    }


    public void requestMove(Direction direction){
        if(direction != this.direction){
            this.direction = direction;
            setModified(true);
        }

        moveRequested = true;

    }

    public void update(){

        if(moveRequested){
            if(canMove(direction))
                doMove(direction);
        }
    }
    @Override
    public void doMove(Direction direction) {
        this.direction = direction;
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        setPosition(nextPos);

    }


}
