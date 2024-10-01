package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.game.*;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameLauncher {

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }
    private GameLauncher() {}

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int playerLives = integerProperty(properties, "playerLives", 5);
        int playerInvincibilityDuration = integerProperty(properties, "playerInvincibilityDuration", 4);
        int beeMoveFrequency = integerProperty(properties, "beeMoveFrequency", 1);
        int playerEnergy = integerProperty(properties, "playerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        int energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 5);
        int diseaseDuration = integerProperty(properties, "diseaseDuration", 5);

        return new Configuration(playerLives, playerEnergy, energyBoost, playerInvincibilityDuration, beeMoveFrequency, energyRecoverDuration, diseaseDuration);
    }

    public Game load() {
        Properties emptyConfig = new Properties();

        MapLevel mapLevel = new MapLevelDefault();
        Position playerPosition = mapLevel.getPlayerPosition();

        if (playerPosition == null)
            throw new RuntimeException("Player not found");

        Configuration configuration = getConfiguration(emptyConfig);
        WorldLevels world = new WorldLevels(1);

        Game game = new Game(world, configuration, playerPosition);
        Map level1 = new Level(game, 1, mapLevel);
        world.put(1, level1);

        return game;
    }

    public  String levelLreDecode(String input) {
        StringBuilder output = new StringBuilder();

        for(String part: input.split("x")){
            char[] partChars = part.toCharArray();

            for(int i = 0; i < partChars.length; i++){
                char chr = partChars[i];

                if(Character.isDigit(chr)){
                    char prev = partChars[i - 1];

                    for(int j = 1; j < Integer.parseInt(String.valueOf(chr)); j++)
                        output.append(prev);
                    continue;
                }
                output.append(chr);
            }

            output.append("x");
        }

        return output.toString();
    }

    public Game loadFromFile(FileInputStream inputStream) throws IOException {
        Properties conf = new Properties();
        conf.load(inputStream);

        String[] required = {
                "playerLives",
                "playerInvincibilityDuration", "beeMoveFrequency", "playerEnergy",
                "energyBoost", "energyRecoverDuration", "diseaseDuration", "levels","compression"};

        for(String key: required){
            if(!conf.containsKey(key)){
                return null;
            } else {
                if(!key.equals("compression")){
                    try{
                        Integer.parseInt(conf.getProperty(key));
                    } catch (NumberFormatException e){
                        return null;
                    }
                }
            }
        }

        int levels = Integer.parseInt(conf.getProperty("levels"));
        WorldLevels world = new WorldLevels(levels);

        Configuration configuration = new Configuration(Integer.parseInt(conf.getProperty("playerLives")),
                Integer.parseInt(conf.getProperty("playerEnergy")),
                Integer.parseInt(conf.getProperty("energyBoost")),
                Integer.parseInt(conf.getProperty("playerInvincibilityDuration")),
                Integer.parseInt(conf.getProperty("beeMoveFrequency")),
                Integer.parseInt(conf.getProperty("energyRecoverDuration")),
                Integer.parseInt(conf.getProperty("diseaseDuration")));

        Position playerPosition = null;
        Game game = null;

        for(int i = 1; i <= levels; i++){
            if (!conf.containsKey(String.format("level%d", i)))
                return null;

            String currentLevelData = conf.getProperty(String.format("level%d", i));
            if(booleanProperty(conf, "compression", false))
                currentLevelData = levelLreDecode(currentLevelData);

            String[] objects = currentLevelData.split("x");
            int h = objects[0].length();
            int w = objects.length;

            MapLevel mapLevel = new MapLevelGenerator(currentLevelData ,w, h, i);

            playerPosition = mapLevel.getPlayerPosition();

            if(playerPosition == null) {
                if(i == 1)
                    return null;

                playerPosition = new Position(i, 0, 0);
            }

            if(mapLevel.getDoorPosition() != null) {
                playerPosition = mapLevel.getDoorPosition();
            }


            Map level = null;

            if(i == 1) {
                 game = new Game(world, configuration, playerPosition);
                 game.getPlayer().setNextDoorOpened(mapLevel.getNextDoorPosition());
                 level = new Level(game, i, mapLevel);
            }
            else {
                 Game tmpGame = new Game(world, configuration, playerPosition);
                 tmpGame.getPlayer().setNextDoorOpened(mapLevel.getNextDoorPosition());
                 level = new Level(tmpGame, i, mapLevel);
            }

            world.put(i, level);
        }

        return game;
    }

}
