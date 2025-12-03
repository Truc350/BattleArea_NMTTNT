package com.example.view;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class PlayerSkillBar extends Pane {
    Button attackBtn, a1Btn, a2Btn, a3Btn, defendBtn, healBtn;

    public PlayerSkillBar() {

        a1Btn = createCircleButton("A1", "#3498db");
        a2Btn = createCircleButton("A2", "#f1c40f");
        a3Btn = createCircleButton("A3", "#e67e22");

        VBox skillColumn = new VBox(10, a1Btn, a2Btn, a3Btn);
        skillColumn.setAlignment(Pos.CENTER_LEFT);

        skillColumn.setLayoutX(0);
        skillColumn.setLayoutY(10);

        defendBtn = createRectButton("Defend", "#9b59b6");
        healBtn   = createRectButton("Heal", "#1abc9c");

        HBox supportRow = new HBox(35, defendBtn, healBtn);
        supportRow.setLayoutX(-200);
        supportRow.setLayoutY(50);

        attackBtn = new Button("A");
        attackBtn.setPrefSize(100, 50);
        setButtonClickEffect(attackBtn, "#2ecc71");
        attackBtn.setStyle("-fx-background-color: #2ecc71;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: white;"
                + "-fx-background-radius:18;");
        attackBtn.setLayoutX(95);
        attackBtn.setLayoutY(40);

        getChildren().addAll(supportRow, skillColumn, attackBtn);
        setPrefSize(260, 150);
    }

    private Button createRectButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefSize(65, 35);
        btn.setStyle(
                "-fx-background-radius: 10;" +
                        "-fx-background-color: " + color + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: white;"
        );
        setButtonClickEffect(btn, color);
        return btn;
    }

    private Button createCircleButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefSize(35, 35);
        btn.setStyle(
                "-fx-background-radius: 25;" +
                        "-fx-background-color: " + color + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: white;"
        );
        setButtonClickEffect(btn, color);
        return btn;
    }

    // --- Hiệu ứng click: làm sáng màu khi nhấn ---
    private void setButtonClickEffect(Button btn, String baseColor) {

        String lighter = lightenColor(baseColor, 0.25);

        btn.setOnMousePressed(e ->
                btn.setStyle(btn.getStyle().replace(baseColor, lighter)));

        btn.setOnMouseReleased(e ->
                btn.setStyle(btn.getStyle().replace(lighter, baseColor)));
    }

    // --- Hàm làm tối màu HEX ---
    private String lightenColor(String hex, double amount) {
        hex = hex.replace("#", "");
        int r = (int) Math.min(255, Integer.parseInt(hex.substring(0, 2), 16) + 255 * amount);
        int g = (int) Math.min(255, Integer.parseInt(hex.substring(2, 4), 16) + 255 * amount);
        int b = (int) Math.min(255, Integer.parseInt(hex.substring(4, 6), 16) + 255 * amount);

        return String.format("#%02x%02x%02x", r, g, b);
    }
}
