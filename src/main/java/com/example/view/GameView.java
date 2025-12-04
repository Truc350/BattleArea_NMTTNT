package com.example.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameView extends Application {

    @Override
    public void start(Stage primaryStage) {
        ArenaView arena = new ArenaView();
        PlayerSkillBar skillBar = new PlayerSkillBar(arena);

        // đặt vị trí UI
        skillBar.setLayoutX(1000);
        skillBar.setLayoutY(550);

        Pane root = new Pane();
        root.getChildren().addAll(arena, skillBar);

        primaryStage.setScene(new Scene(root, 1300, 700));
        primaryStage.setResizable(false);
        primaryStage.setTitle("Battle Arena 2D");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
