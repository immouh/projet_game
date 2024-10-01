package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.game.Position;

import static fr.ubx.poo.ugarden.launcher.MapEntity.DoorNextClosed;
import static fr.ubx.poo.ugarden.launcher.MapEntity.DoorPrevOpened;

public class MapLevelGenerator extends MapLevel {


    public MapLevelGenerator(String levelData, int w, int h, int level) {
        super(w, h);
        setLevel(level);

        String[] split = levelData.split("x");
        for (int i = 0; i < w; i++) {
            char[] chars = split[i].toCharArray();
            for (int j = 0; j < h; j++) {
                set(i, j, MapEntity.fromCode(chars[j]));
                if(MapEntity.fromCode(chars[j]) == DoorPrevOpened)
                    setDoorPosition(new Position(level, i, j));

                if(MapEntity.fromCode(chars[j]) == DoorNextClosed)
                    setNextDoorPosition(new Position(level, i, j));

            }
        }
    }


}
