package com.example.controller;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Controller xử lý animation di chuyển nhân vật
 */
public class MovementController {

    /**
     * Di chuyển node đến vị trí X mới
     * @param node Node cần di chuyển
     * @param targetX Vị trí X đích
     * @param onFinished Callback sau khi animation xong
     */
    public static void moveTo(Node node, double targetX, Runnable onFinished) {
        double currentX = node.getLayoutX();
        double distance = targetX - currentX;

        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), node);
        transition.setByX(distance);
        transition.setOnFinished(e -> {
            // Cập nhật layoutX thực sự (không chỉ translate)
            node.setLayoutX(targetX);
            node.setTranslateX(0);

            if (onFinished != null) {
                onFinished.run();
            }
        });

        transition.play();
    }

    /**
     * Di chuyển node theo hướng (dùng cho combat movement)
     * @param node Node cần di chuyển
     * @param deltaX Khoảng cách di chuyển (+ là phải, - là trái)
     * @param onFinished Callback sau khi animation xong
     */
    public static void moveBy(Node node, double deltaX, Runnable onFinished) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), node);
        transition.setByX(deltaX);
        transition.setOnFinished(e -> {
            // Cập nhật layoutX
            node.setLayoutX(node.getLayoutX() + deltaX);
            node.setTranslateX(0);

            if (onFinished != null) {
                onFinished.run();
            }
        });

        transition.play();
    }
}