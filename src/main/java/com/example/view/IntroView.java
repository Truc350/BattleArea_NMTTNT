package com.example.view;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class IntroView {
    private PauseTransition delay;
    public Scene getScene() {
        ImageView bg = new ImageView(
                new Image(getClass().getResourceAsStream("/img/arena/intro.png"))
        );
        bg.setFitWidth(1300);
        bg.setFitHeight(700);

        StackPane root = new StackPane(bg);
        Scene scene = new Scene(root, 1300, 700);

        return scene;
    }
}
