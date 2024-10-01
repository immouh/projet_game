package fr.ubx.poo.ugarden.view;

import fr.ubx.poo.ugarden.engine.GameEngine;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.launcher.GameLauncher;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class GameLauncherView extends BorderPane {
    private final FileChooser fileChooser = new FileChooser();

    public GameLauncherView(Stage stage)  {
        // Create menu
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem loadItem = new MenuItem("Load from file ...");
        MenuItem defaultItem = new MenuItem("Load default configuration");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        menuFile.getItems().addAll(
                loadItem, defaultItem, new SeparatorMenuItem(),
                exitItem);

        menuBar.getMenus().addAll(menuFile);
        this.setTop(menuBar);

        Text text = new Text("UBGarden 2023");
        text.getStyleClass().add("message");
        VBox scene = new VBox();
        scene.getChildren().add(text);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        scene.getStyleClass().add("message");
        this.setCenter(scene);

        // Load from file
        loadItem.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {

                try {
                    FileInputStream inputStream = new FileInputStream(file);
                    Game game = GameLauncher.getInstance().loadFromFile(inputStream);

                    if(game == null){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Erreur");
                        alert.setHeaderText("Une erreur s'est produite");
                        alert.setContentText("Le fichier de configuration n'est pas valide");
                        alert.showAndWait();
                        System.exit(0);
                    }

                    GameEngine engine = new GameEngine(game, stage);
                    engine.start();

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });

        defaultItem.setOnAction(e -> {
            Game game = GameLauncher.getInstance().load();
            GameEngine engine = null;

            engine = new GameEngine(game, stage);
            engine.start();
        });

        // Exit
        exitItem.setOnAction(e -> System.exit(0));

    }


}
