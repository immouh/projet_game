/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.engine;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.decor.OpenedDoorNext;
import fr.ubx.poo.ugarden.go.decor.Tree;
import fr.ubx.poo.ugarden.go.personage.Bee;
import fr.ubx.poo.ugarden.go.personage.Player;
import fr.ubx.poo.ugarden.view.*;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final Game game;
    private  Player player;
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    private final Stage stage;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;

    private SpritePlayer playerSprite;
    private ColorAdjust playerSpriteCA;
    private Timeline beesLoop;

    private List<Bee> bees = new ArrayList<>();

    private List<Boolean> beesCollisions = new ArrayList<>();

    private Timeline recoveryTimer;

    private Position nextDoorOpened;

    private boolean switchPrev = false;


    public GameEngine(Game game, final Stage stage) {
        this.stage = stage;
        this.game = game;
        this.player = game.getPlayer();
        game.gameEngine = this;
        initialize();
        buildAndSetGameLoop();
    }

    private void initialize() {
        Group root = new Group();
        layer = new Pane();

        int height = game.world().getGrid().height();
        int width = game.world().getGrid().width();
        int sceneWidth = width * ImageResource.size;
        int sceneHeight = height * ImageResource.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.hide();
        stage.show();


        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(game, root, sceneWidth, sceneHeight);
        sprites.clear();

        for (var decor : game.world().getGrid().values()) {
            sprites.add(SpriteFactory.create(layer, decor));
            decor.setModified(true);
            var bonus = decor.getBonus();
            var bee = decor.getBee();
            if (bonus != null) {
                sprites.add(SpriteFactory.create(layer, bonus));
                bonus.setModified(true);
            }

            if(bee != null){
                sprites.add(new SpriteBee(layer, bee));
                bee.setModified(true);
                bees.add(bee);
                beesCollisions.add(false);
            }
        }

        Position initPos = game.world().getGrid().initialPlayerPosition();
        if(initPos.level() != game.world().currentLevel())
            initPos = new Position(game.world().currentLevel(), initPos.x(), initPos.y());

        if(player != null) {

            if(player.getNextDoorOpened() != null){
                nextDoorOpened = player.getNextDoorOpened();
            }

            if(nextDoorOpened != null && switchPrev){
                initPos = nextDoorOpened;
                switchPrev = false;
            }

            player = new Player(game, initPos, player);
        }

        game.setPlayer(player);

        playerSpriteCA = new ColorAdjust();
        playerSprite = new SpritePlayer(layer, player, playerSpriteCA);
        sprites.add(playerSprite);
    }

    public void doorOpen(Position pos){
        OpenedDoorNext openedDoor = new OpenedDoorNext(pos);
        game.world().getGrid().set(pos, openedDoor);
        sprites.add(SpriteFactory.create(layer, openedDoor));
    }

    public void Win(){
        gameLoop.stop();
        beesLoop.stop();
        recoveryTimer.stop();
        showMessage("Vous gagnez!", Color.GREEN);
    }

    void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
            checkLevel();

            // Check keyboard actions
            processInput(now);

            // Do actions
            update(now);

            checkCollision(now);

            // Graphic update
            cleanupSprites();
            render();
            statusBar.update(game);
            }
        };

        beesLoop = new Timeline(new KeyFrame(Duration.seconds(game.configuration().beeMoveFrequency()), event -> {
            for(var bee: bees) {
                bee.requestMove(Direction.random());
                bee.update();
            }
        } ));

        beesLoop.setCycleCount(Timeline.INDEFINITE);

       // AtomicInteger recSec = new AtomicInteger(0);
        int recDur = game.configuration().energyRecoverDuration();
        recoveryTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if(recDur == player.getRecSec()) {
                player.resetRecSec();
                player.incEnergy();
            }

            player.incRecSec();

        }));

        recoveryTimer.setCycleCount(Timeline.INDEFINITE);

    }


    private void checkLevel() {
        if (game.isSwitchLevelRequested()) {
            cleanupSprites();
            int nextLevel = game.getSwitchLevel();

            if(nextLevel < game.world().currentLevel()){
                switchPrev = true;
            }

            game.world().setCurrentLevel(nextLevel);
            game.clearSwitchLevel();
            bees.clear();
            beesCollisions.clear();
            beesLoop.stop();
            beesLoop.play();
            stage.close();
            initialize();
        }
    }

    private void checkCollision(long now)  {

        int index = 0;
        for(Bee bee : bees){
            boolean stung = beesCollisions.get(index);

            if(bee.getPosition().x() == player.getPosition().x() && bee.getPosition().y() == player.getPosition().y() && !player.isInvincible()) {
                if (!stung) {
                    bee.remove();
                    player.ouch();
                    beesCollisions.set(index, true);

                    player.setInvincible(true);
                    int invDuration = game.configuration().playerInvincibilityDuration();
                    AtomicInteger sec = new AtomicInteger(0);
                    Timeline invDurTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                        sec.getAndIncrement();

                        if(sec.get() == invDuration)
                            player.setInvincible(false);

                    })) ;

                    Timeline invEffect = new Timeline(new KeyFrame(Duration.millis(100), event -> {
                        playerSprite.br();
                    }));

                    invDurTimer.setCycleCount(invDuration);
                    invDurTimer.play();
                    invEffect.setCycleCount(invDuration * 10);
                    invEffect.play();
                }
            }

            index++;
        }
    }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            beesLoop.stop();
            recoveryTimer.stop();
            Platform.exit();
            System.exit(0);
        } else if (input.isMoveDown()) {
            player.requestMove(Direction.DOWN);
        } else if (input.isMoveLeft()) {
            player.requestMove(Direction.LEFT);
        } else if (input.isMoveRight()) {
            player.requestMove(Direction.RIGHT);
        } else if (input.isMoveUp()) {
            player.requestMove(Direction.UP);
        }
        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setScene(scene);
        input = new Input(scene);
        stage.hide();
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        player.update(now);

        if (player.getLives() == 0) {
            gameLoop.stop();
            beesLoop.stop();
            recoveryTimer.stop();
            showMessage("Perdu!", Color.RED);
        }

    }

    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                cleanUpSprites.add(sprite);
            }
        });
        cleanUpSprites.forEach(Sprite::remove);
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }

    private void render() {
        sprites.forEach(Sprite::render);
    }

    public void start() {
        gameLoop.start();
        beesLoop.play();
        recoveryTimer.play();
    }
}