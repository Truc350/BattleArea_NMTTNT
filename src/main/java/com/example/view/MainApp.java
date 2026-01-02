package com.example.view;

import com.example.controller.GameController;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {
    private static Stage primaryStage;
    @Override
    public void start(Stage stage) throws Exception {
//        GameController controller = new GameController(stage);
//        controller.start();
        primaryStage = stage;
        primaryStage.setTitle("Battle Arena");
        primaryStage.setResizable(false);
        // khoi dogn controller
        GameController controller = new GameController(primaryStage);
        controller.showIntro();
    }
    public static void showAreaSelect(){
        GameController.getInstance().showArenaSelect();
    }
    public static void showCharacterSelect(String arenaPath) {
        GameController.getInstance().showCharacterSelect(arenaPath);
    }
    public static void showGame(String arenaPath, String characterPath) {
        GameController.getInstance().showGame(arenaPath, characterPath);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
