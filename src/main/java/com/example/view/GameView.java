package com.example.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameView extends Application {

    @Override
    public void start(Stage primaryStage) {

        Pane root = new Pane();

        // ====== LOAD BACKGROUND ======
        Image bg = new Image(getClass().getResourceAsStream("/img/sanDau/sandau1.jpg"));
        ImageView bgView = new ImageView(bg);
        bgView.setFitWidth(1400);
        bgView.setFitHeight(900);

        // ====== PLAYER BÊN TRÁI ======
        Image playerImg = new Image(getClass().getResourceAsStream("/img/character/dausi.png"));
        ImageView player = new ImageView(playerImg);
        player.setFitWidth(250);
        player.setFitHeight(300);
        player.setLayoutX(150);   // bên trái
        player.setLayoutY(450);   // thấp xuống sàn

        // ====== ENEMY BÊN PHẢI ======
        Image enemyImg = new Image(getClass().getResourceAsStream("/img/character/img.png"));
        ImageView enemy = new ImageView(enemyImg);
        enemy.setFitWidth(250);
        enemy.setFitHeight(300);
        enemy.setLayoutX(1400 - 250 - 150); // bên phải
        enemy.setLayoutY(450);

        root.getChildren().addAll(bgView, player, enemy);

        Scene scene = new Scene(root, 1400, 900);

        primaryStage.setTitle("Battle Arena 2D");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
