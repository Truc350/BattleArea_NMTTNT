package com.example.view;

import com.example.controller.BattleController;
import javafx.animation.PauseTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

public class PlayerSkillBar extends Pane {

    private final ArenaView arena;
    private final BattleController battleController; // Tham chiếu đến logic thật

    private SkillButton A1, A2, A3, DEF, HEAL, ATK;

    public PlayerSkillBar(ArenaView arena, BattleController battleController) {
        this.arena = arena;
        this.battleController = battleController;

        // ===== TẠO BUTTON =====
        A1 = new SkillButton(createCircleButton("A1", "#3498db"));
        A2 = new SkillButton(createCircleButton("A2", "#f1c40f"));
        A3 = new SkillButton(createCircleButton("A3", "#e67e22"));
        DEF = new SkillButton(createRectButton("DEF", "#9b59b6"));
        HEAL = new SkillButton(createRectButton("HEAL", "#1abc9c"));

        Button atkBtn = new Button("ATTACK");
        atkBtn.setPrefSize(120, 55);
        atkBtn.setStyle("""
                -fx-background-radius: 25;
                -fx-background-color: #2ecc71;
                -fx-text-fill: white;
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                """);

        ATK = new SkillButton(atkBtn);

        // ===== LAYOUT =====
        VBox col = new VBox(10, A1, A2, A3);
        col.setLayoutX(0);
        col.setLayoutY(0);

        HBox row = new HBox(20, DEF, HEAL);
        row.setLayoutX(-200);
        row.setLayoutY(45);

        ATK.setLayoutX(90);
        ATK.setLayoutY(35);

        getChildren().addAll(col, row, ATK);

        // ===== KẾT NỐI BUTTON VỚI BATTLECONTROLLER =====
        ATK.getButton().setOnAction(e -> battleController.onAttack());

        A1.getButton().setOnAction(e -> battleController.onSkillA1());

        A2.getButton().setOnAction(e -> battleController.onSkillA2());

        A3.getButton().setOnAction(e -> battleController.onSkillA3());

        HEAL.getButton().setOnAction(e -> battleController.onHeal());

        DEF.getButton().setOnAction(e -> battleController.onDefend());
    }

    // =====================================================
    // UI HELPER (giữ overlay cooldown đẹp)
    // =====================================================

    public void disableSkill(SkillButton button) {
        button.getButton().setDisable(true);
        drawOverlay(button.getOverlay(), true);
    }

    public void enableSkill(SkillButton button) {
        button.getButton().setDisable(false);
        drawOverlay(button.getOverlay(), false);
    }

    private void drawOverlay(Canvas c, boolean on) {
        GraphicsContext g = c.getGraphicsContext2D();
        g.clearRect(0, 0, c.getWidth(), c.getHeight());
        if (on) {
            g.setFill(Color.rgb(0, 0, 0, 0.6));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
    }

    // =====================================================
    // GAME OVER (gọi từ BattleController)
    // =====================================================

    public void showGameOver(String txt) {
        Label lb = new Label(txt);
        lb.setStyle("-fx-font-size:48px;-fx-text-fill:red;-fx-font-weight:bold;");
        lb.setLayoutX(450);
        lb.setLayoutY(300);
        arena.getChildren().add(lb);

        // Delay 3 giây rồi quay về chọn sàn đấu
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> {
            MainApp.showAreaSelect();
        });
        delay.play();
    }

    // =====================================================
    // BUTTON FACTORY (giữ nguyên đẹp)
    // =====================================================

    private Button createRectButton(String text, String color) {
        Button b = new Button(text);
        b.setPrefSize(75, 45);
        b.setStyle("""
                -fx-background-radius: 14;
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                """.formatted(color));
        return b;
    }

    private Button createCircleButton(String text, String color) {
        Button b = new Button(text);
        b.setPrefSize(40, 40);
        b.setStyle("""
                -fx-background-radius: 30;
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                """.formatted(color));
        return b;
    }
}