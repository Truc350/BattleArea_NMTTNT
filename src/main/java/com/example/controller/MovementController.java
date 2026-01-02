package com.example.controller;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class MovementController {

    // ===== GIỚI HẠN MÀN HÌNH =====
    private static final double MIN_X = 100;    // Mép trái
    private static final double MAX_X_ENEMY = 600;   // AI không vượt quá giữa màn
    private static final double MIN_X_PLAYER = 650;  // Player không vượt quá giữa màn
    private static final double MAX_X_PLAYER = 1050; // Mép phải

    /**
     * Di chuyển với giới hạn màn hình (dành cho cả AI và Player)
     * @param actor ImageView cần di chuyển
     * @param targetX Vị trí đích mong muốn
     * @param onDone Callback khi hoàn thành
     * @param isPlayer true nếu là Player, false nếu là AI
     */
    public static void moveTo(ImageView actor, double targetX, Runnable onDone, boolean isPlayer) {
        double startX = actor.getLayoutX();
        double clampedX;

        if (isPlayer) {
            // Player: giới hạn từ MIN_X_PLAYER đến MAX_X_PLAYER
            clampedX = Math.max(MIN_X_PLAYER, Math.min(targetX, MAX_X_PLAYER));
        } else {
            // AI: giới hạn từ MIN_X đến MAX_X_ENEMY
            clampedX = Math.max(MIN_X, Math.min(targetX, MAX_X_ENEMY));
        }

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

    /**
     * Di chuyển đơn giản không kiểm tra giới hạn (dùng cho setup ban đầu)
     */
    public static void moveTo(ImageView actor, double targetX, Runnable onDone) {
        double startX = actor.getLayoutX();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(actor.layoutXProperty(), startX)
                ),
                new KeyFrame(Duration.seconds(0.6),
                        new KeyValue(
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
