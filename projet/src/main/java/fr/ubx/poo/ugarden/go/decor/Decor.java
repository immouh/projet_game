package fr.ubx.poo.ugarden.go.decor;

import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Takeable;
import fr.ubx.poo.ugarden.go.Walkable;
import fr.ubx.poo.ugarden.go.bonus.Bonus;
import fr.ubx.poo.ugarden.go.personage.Bee;
import javafx.geometry.Pos;

public abstract class Decor extends GameObject implements Walkable, Takeable  {

    private Bonus bonus;
    private Bee bee;

    public Decor(Position position) {
        super(position);
    }

    public Decor(Position position, Bonus bonus) {
        super(position);
        this.bonus = bonus;
    }

    public Decor(Position position, Bee bee){
        super(position);
        this.bee = bee;
    }

    public Bonus getBonus() {
        return bonus;
    }

    public void setBonus(Bonus bonus) {
        this.bonus = bonus;
    }

    public Bee getBee() { return bee;}
    public void setBee(Bee bee){
        this.bee = bee;
    }


}