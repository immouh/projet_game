package fr.ubx.poo.ugarden.game;

import fr.ubx.poo.ugarden.go.bonus.*;
import fr.ubx.poo.ugarden.go.decor.ground.Grass;
import fr.ubx.poo.ugarden.go.decor.*;
import fr.ubx.poo.ugarden.go.personage.Bee;
import fr.ubx.poo.ugarden.go.personage.Princess;
import fr.ubx.poo.ugarden.launcher.MapEntity;
import fr.ubx.poo.ugarden.launcher.MapLevel;

import java.util.*;

public class Level implements Map {

    private final int level;
    private final int width;

    private final int height;
    private Game game;

    private final MapLevel entities;

    private final java.util.Map<Position, Decor> decors = new HashMap<>();

    public Level(Game game, int level, MapLevel entities) {
        this.level = level;
        this.entities = entities;
        this.width = entities.width();
        this.height = entities.height();
        this.game = game;

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Position position = new Position(level, i, j);
                MapEntity mapEntity = entities.get(i, j);
                switch (mapEntity) {
                    case Grass:
                        decors.put(position, new Grass(position));
                        break;
                    case Tree:
                        decors.put(position, new Tree(position));
                        break;
                    case Heart: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new Heart(position, grass));
                        decors.put(position, grass);
                        break;
                    }

                    case DoorNextOpened:
                        decors.put(position, new OpenedDoorNext(position));
                        break;

                    case DoorPrevOpened:
                        decors.put(position, new OpenedDoorPrev(position));
                        break;

                    case DoorNextClosed:
                        decors.put(position, new ClosedDoor(position));
                        break;

                    case Key:
                        Decor grass = new Grass(position);
                        grass.setBonus(new Key(position, grass));
                        decors.put(position, grass);
                        break;

                    case PoisonedApple:
                        Decor PAGrass = new Grass(position);
                        PAGrass.setBonus(new PoisonedApple(position, PAGrass));
                        decors.put(position, PAGrass);
                        break;

                    case Land:
                        decors.put(position, new Land(position));
                        break;

                    case Flowers:
                        decors.put(position, new Flowers(position));
                        break;

                    case Carrots:
                        decors.put(position, new Carrots(position));
                        break;

                    case Apple:
                        Grass AppleGrass = new Grass(position);
                        AppleGrass.setBonus(new Apple(position, AppleGrass));
                        decors.put(position, AppleGrass);
                        break;

                    case Bee:
                       Decor beeGrass = new Grass(position);
                       beeGrass.setBee( new Bee(game, position));
                       decors.put(position, beeGrass);
                        break;

                    case Princess:
                        decors.put(position, new Princess(position));
                        break;

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

    @Override
    public void remove(Position position) {
        decors.remove(position);
    }

    public Collection<Decor> values() {
        return decors.values();
    }

    @Override
    public Position initialPlayerPosition() {
        return game.getPlayer().getPosition();
    }


    @Override
    public boolean inside(Position position) {
        return position.level() == level && position.x() >= 0 && position.x() < width && position.y() >= 0 && position.y() < height;
    }

    @Override
    public void set(Position position, Decor decor) {
        if (!inside(position))
            throw new IllegalArgumentException("Illegal Position");
        if (decor != null)
            decors.put(position, decor);
    }


}
