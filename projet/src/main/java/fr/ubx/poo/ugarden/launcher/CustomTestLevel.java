package fr.ubx.poo.ugarden.launcher;


import static fr.ubx.poo.ugarden.launcher.MapEntity.*;

public class CustomTestLevel extends MapLevel {

    private final MapEntity[][] level1 = {
            {Tree, Tree, Tree, Tree, Tree, Tree, Tree, Tree, Tree, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
            {Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Key, Grass, Key, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
            {Grass, Grass, Grass, Carrots, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
            {Player, Grass, Grass, Grass, Grass, Grass, Grass, Tree, Grass, Carrots, Grass, Bee, Grass, Heart, DoorNextClosed, Grass, Grass, Grass},
            {Grass, Grass, Grass, Grass, Grass, Grass, Grass, Tree, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
            {Grass, Bee, Grass, Grass, Apple, Grass, Grass, Tree, Grass, Grass, Grass, Grass, Grass, Apple, Grass, Grass, Grass, Grass},
            {Grass, Grass, Carrots, DoorPrevOpened, Grass, Grass, Grass, Tree, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Tree},
            {Grass, Grass, Grass, Grass, Grass, Grass, Grass, Tree, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass, Grass},
    };

    private final static int width = 18;
    private final static int height = 8;

    public CustomTestLevel(int level) {
        super(width, height);
        setLevel(level);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                set(i, j, level1[j][i]);
    }
}
