package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.game.Position;

import static fr.ubx.poo.ugarden.launcher.MapEntity.*;

public class MapLevel {

    private Position doorPosition = null;

    private Position nextDoorPosition = null;
    private final int width;
    private final int height;
    private final MapEntity[][] grid;

    private int level = 1;

    private Position playerPosition = null;

    public MapLevel(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new MapEntity[height][width];
    }

    public void setNextDoorPosition(Position pos){
        nextDoorPosition = pos;
    }

    public Position getNextDoorPosition(){
        return nextDoorPosition;
    }

    public void setPlayerPosition(Position pos){
        playerPosition = pos;
    }
    public Position getDoorPosition(){
        return doorPosition;
    }

    public void setDoorPosition(Position pos){
        doorPosition = pos;
    }

    public int width() {
        return width;    }

    public int height() {
        return height;
    }

    public MapEntity get(int i, int j) {
        return grid[j][i];
    }

    public void set(int i, int j, MapEntity mapEntity) {
        grid[j][i] = mapEntity;
    }

    public void setLevel(int level){ this.level = level; }
    public Position getPlayerPosition() {
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (grid[j][i] == Player) {
                    if (playerPosition != null)
                        throw new RuntimeException("Multiple definition of player");
                    set(i, j, Grass);
                    playerPosition = new Position(level, i, j);
                }

        return playerPosition;
    }

}
