package fr.ubx.poo.ugarden.go.bonus;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.personage.Player;

public class Apple extends Bonus {

    public Apple(Position position, Decor decor){
        super(position, decor);
    }
    @Override
    public void takenBy(Player player) {
        remove();
        player.take(this);
    }
}
