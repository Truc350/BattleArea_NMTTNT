package com.example.view;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class MovementController {
    public static void moveTo(ImageView actor, double targetX, Runnable onDone) {
        double startX = actor.getLayoutX();
        double distance = targetX - startX;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new javafx.animation.KeyValue(actor.layoutXProperty(), startX)
                ),
                new KeyFrame(Duration.seconds(0.6),
                        new javafx.animation.KeyValue(
                                actor.layoutXProperty(),
                                targetX,
                                Interpolator.EASE_BOTH
                        )
                )
        );

        timeline.setOnFinished(e -> {
            if (onDone != null) onDone.run();
        });

        timeline.play();
    }

    public static void dodge(ImageView actor) {
        TranslateTransition dodge = new TranslateTransition(Duration.seconds(0.25), actor);
        dodge.setByY(-60); // nhảy né
        dodge.setAutoReverse(true);
        dodge.setCycleCount(2);
        dodge.play();
    }

    private static boolean isJumping(Node node) {
        return node.getProperties().containsKey("jumping");
    }

    public static void jump(ImageView character) {
        if (isJumping(character)) return;

        character.getProperties().put("jumping", true);

        double startY = character.getLayoutY();

        TranslateTransition up = new TranslateTransition(Duration.millis(220), character);
        up.setByY(-120);
        up.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition down = new TranslateTransition(Duration.millis(260), character);
        down.setByY(120);
        down.setInterpolator(Interpolator.EASE_IN);

        SequentialTransition jump = new SequentialTransition(up, down);
        jump.setOnFinished(e -> {
            character.setTranslateY(0);
            character.setLayoutY(startY);
            character.getProperties().remove("jumping");
        });

        jump.play();
    }

}
