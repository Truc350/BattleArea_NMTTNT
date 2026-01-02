package com.example.view;

import javafx.animation.PauseTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class PlayerSkillBar extends Pane {

    private final ArenaView arena;

    private SkillButton A1, A2, A3, DEF, HEAL, ATK;

    private int turnCount = 0;
    private final Map<SkillButton, Integer> cooldownEnd = new HashMap<>();

    public PlayerSkillBar(ArenaView arena) {
        this.arena = arena;

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

        // ===== ACTION =====
        ATK.getButton().setOnAction(e -> {
            if (!canAct()) return;
            castNormalAttack();
        });

        A1.getButton().setOnAction(e -> {
            if (!canAct() || !ready(A1)) return;
            castSkill(20);
            startCooldown(A1, 2);
        });

        A2.getButton().setOnAction(e -> {
            if (!canAct() || !ready(A2)) return;
            castSkill(30);
            startCooldown(A2, 3);
        });

        A3.getButton().setOnAction(e -> {
            if (!canAct() || !ready(A3)) return;
            castSkill(40);
            startCooldown(A3, 5);
        });

        HEAL.getButton().setOnAction(e -> {
            if (!canAct() || !ready(HEAL)) return;
            arena.getPlayerBar().heal(30);
            startCooldown(HEAL, 4);
            endPlayerTurn();
        });

        DEF.getButton().setOnAction(e -> {
            if (!canAct()) return;
            // placeholder
            endPlayerTurn();
        });
    }

    // =====================================================
    // CORE LOGIC
    // =====================================================

    private boolean canAct() {
        return arena.isPlayerTurn() && !arena.isGameOver();
    }

    private void castNormalAttack() {
        SkillEffect.castSkill(
                arena,
                arena.getPlayerView().getLayoutX() - 20,
                arena.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu2.png",
                12, 5,
                "/img/explosion/explosion_thuong.png",
                120,
                this::endPlayerTurn
        );
    }

    private void castSkill(int dmg) {
        SkillEffect.castSkill(
                arena,
                arena.getPlayerView().getLayoutX() - 20,
                arena.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu4.png",
                dmg, 10,
                "/img/explosion/explosion_1.png",
                160,
                this::endPlayerTurn
        );
    }

    private void endPlayerTurn() {
        if (arena.getEnemyBar().getCurrentHp() <= 0) {
            arena.setGameOver(true);
            showGameOver("YOU WIN!");
            return;
        }

        arena.endPlayerTurn();
        aiTurn();
    }

    // =====================================================
    // AI
    // =====================================================

    private void aiTurn() {
        PauseTransition delay = new PauseTransition(Duration.seconds(0.8));
        delay.setOnFinished(e -> {
            // Tính vị trí đích: dừng trước player một khoảng
            double targetX = arena.getPlayerView().getLayoutX() - 50;

            SkillEffect.castSkillAI(
                    arena,
                    arena.getEnemyView().getLayoutX() + 200,  // vị trí bắt đầu (từ enemy)
                    arena.getEnemyView().getLayoutY() + 60,
                    "/img/attackEffect/chieu2.png",
                    12,
                    "/img/explosion/explosion_thuong.png",
                    120,
                    () -> {
                        if (arena.getPlayerBar().getCurrentHp() <= 0) {
                            arena.setGameOver(true);
                            showGameOver("GAME OVER");
                            return;
                        }
                        arena.startPlayerTurn();
                        nextTurn();
                    }

            );
        });
        delay.play();
    }

    // =====================================================
    // COOLDOWN
    // =====================================================

    private boolean ready(SkillButton s) {
        return !cooldownEnd.containsKey(s) || turnCount >= cooldownEnd.get(s);
    }

    private void startCooldown(SkillButton s, int turns) {
        cooldownEnd.put(s, turnCount + turns);
        s.getButton().setDisable(true);
        drawOverlay(s.getOverlay(), true);
    }

    public void nextTurn() {
        turnCount++;

        for (var e : cooldownEnd.entrySet()) {
            if (turnCount >= e.getValue()) {
                e.getKey().getButton().setDisable(false);
                drawOverlay(e.getKey().getOverlay(), false);
            }
        }
    }

    // =====================================================
    // UI
    // =====================================================

    private void drawOverlay(Canvas c, boolean on) {
        GraphicsContext g = c.getGraphicsContext2D();
        g.clearRect(0, 0, c.getWidth(), c.getHeight());
        if (on) {
            g.setFill(Color.rgb(0, 0, 0, 0.6));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
    }

    public void showGameOver(String txt) {
        Label lb = new Label(txt);
        lb.setStyle("-fx-font-size:48px;-fx-text-fill:red;-fx-font-weight:bold;");
        lb.setLayoutX(450);
        lb.setLayoutY(300);
        arena.getChildren().add(lb);
    }

    // =====================================================
    // BUTTON FACTORY
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
