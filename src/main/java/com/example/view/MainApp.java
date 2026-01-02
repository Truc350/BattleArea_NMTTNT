package com.example.view;

import com.example.controller.GameController;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        GameController controller = new GameController(stage);
        controller.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
