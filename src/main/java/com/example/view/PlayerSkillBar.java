package com.example.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.util.Duration;

public class PlayerSkillBar extends Pane {

    private ArenaView arena;

    SkillButton A1, A2, A3, DEF, HEAL, ATK;

    public PlayerSkillBar(ArenaView arena) {
        this.arena = arena;

        // ==============================
        // TẠO NÚT & WRAP BẰNG SkillButton
        // ==============================
        A1 = new SkillButton(createCircleButton("A1", "#3498db"));
        A2 = new SkillButton(createCircleButton("A2", "#f1c40f"));
        A3 = new SkillButton(createCircleButton("A3", "#e67e22"));

        DEF = new SkillButton(createRectButton("Defend", "#9b59b6"));
        HEAL = new SkillButton(createRectButton("Heal", "#1abc9c"));

        Button atkBtn = new Button("A");
        atkBtn.setPrefSize(100, 50);
        atkBtn.setStyle("-fx-background-color:#2ecc71; -fx-text-fill:white; -fx-font-size:14px; -fx-background-radius:18;");
        ATK = new SkillButton(atkBtn);

        // ==============================
        // LAYOUT
        // ==============================
        VBox skillColumn = new VBox(10, A1, A2, A3);
        skillColumn.setLayoutX(0);
        skillColumn.setLayoutY(10);

        HBox supportRow = new HBox(35, DEF, HEAL);
        supportRow.setLayoutX(-200);
        supportRow.setLayoutY(50);

        ATK.setLayoutX(95);
        ATK.setLayoutY(40);

        getChildren().addAll(skillColumn, supportRow, ATK);

        // ==============================
        // GÁN SKILL ACTION + COOLDOWN
        // ==============================
        A1.getButton().setOnAction(e -> {
            castA1();
            startCooldown(A1.getOverlay(), 5);  // cooldown 5s
        });

        A2.getButton().setOnAction(e -> {
            castA2();
            startCooldown(A2.getOverlay(), 8);  // cooldown 8s
        });

        A3.getButton().setOnAction(e -> {
            castA3();
            startCooldown(A3.getOverlay(), 10); // cooldown 10s
        });

        ATK.getButton().setOnAction(e -> {
            castAttack();
        });
    }

    // ============================================================
    // CAST SKILL FUNCTIONS
    // ============================================================
    private void castAttack() {
        SkillEffect.castSkill(
                arena,
                arena.getPlayerView().getLayoutX() - 20,
                arena.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu2.png", 10, 5,
                "/img/explosion/explosion_thuong.png", 120
        );
    }

    private void castA1() {
        SkillEffect.castSkill(
                arena,
                arena.getPlayerView().getLayoutX() - 20,
                arena.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu4.png",
                20, 10,
                "/img/explosion/explosion_1.png", 150
        );
    }

    private void castA2() {
        SkillEffect.castSkill(
                arena,
                arena.getPlayerView().getLayoutX() - 20,
                arena.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu4.png",
                30, 15,
                "/img/explosion/explosion_2.png", 180
        );
    }

    private void castA3() {
        SkillEffect.castSkill(
                arena,
                arena.getPlayerView().getLayoutX() - 20,
                arena.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu4.png",
                35, 25,
                "/img/explosion/explosion_3.png", 220
        );
    }

    // ============================================================
    // COOLDOWN OVERLAY
    // ============================================================
    private void startCooldown(Canvas overlay, double seconds) {
        StackPane wrapper = (StackPane) overlay.getParent();
        wrapper.setDisable(true);   // KHÓA nút khi cooldown bắt đầu

        long start = System.currentTimeMillis();

        Timeline tl = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            double elapsed = (System.currentTimeMillis() - start) / 1000.0;
            double p = Math.max(0, 1 - elapsed / seconds);
            drawCooldown(overlay, p);
        }));

        tl.setCycleCount(Animation.INDEFINITE);
        tl.play();

        Timeline stopTl = new Timeline(new KeyFrame(Duration.seconds(seconds), e -> {
            tl.stop();
            drawCooldown(overlay, 0);

            wrapper.setDisable(false); // MỞ KHÓA khi cooldown xong
        }));
        stopTl.play();
    }

    private void drawCooldown(Canvas c, double percent) {
        GraphicsContext g = c.getGraphicsContext2D();
        g.clearRect(0, 0, c.getWidth(), c.getHeight());

        if (percent <= 0) return;

        g.setFill(Color.rgb(0, 0, 0, 0.5));
        g.fillArc(0, 0, c.getWidth(), c.getHeight(),
                90, -360 * percent, ArcType.ROUND);
    }

    // ============================================================
    // BUTTON CREATOR
    // ============================================================
    private Button createRectButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefSize(65, 35);
        btn.setStyle("-fx-background-radius: 10; -fx-background-color:" + color + "; -fx-font-size:13px; -fx-text-fill:white;");
        return btn;
    }

    private Button createCircleButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefSize(35, 35);
        btn.setStyle("-fx-background-radius: 25; -fx-background-color:" + color + "; -fx-font-size:13px; -fx-text-fill:white;");
        return btn;
    }
}
