package com.example.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

import static java.awt.SystemColor.text;

public class PlayerSkillBar extends Pane {

    private ArenaView arena;

    SkillButton A1, A2, A3, DEF, HEAL, ATK;
    private int turnCount = 0;
    private Map<SkillButton, Integer> skillCooldownEnd = new HashMap<>();

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
            if (!arena.isPlayerTurn()) return;

            if (isSkillReady(A1)) {
                castA1();
                startSkillCooldown(A1, 2); // A1 cần 2 lượt
            }
        });

        A2.getButton().setOnAction(e -> {
            if (!arena.isPlayerTurn()) return;

            if (isSkillReady(A2)) {
                castA2();
                startSkillCooldown(A2, 3); // 3 lượt
            }
        });

        A3.getButton().setOnAction(e -> {
            if (!arena.isPlayerTurn()) return;

            if (isSkillReady(A3)) {
                castA3();
                startSkillCooldown(A3, 5); // 5 lượt
            }
        });

        HEAL.getButton().setOnAction(e -> {
            if (isSkillReady(HEAL)) {
                castHeal();
                startSkillCooldown(HEAL, 4); // 4 lượt
            }
        });

        DEF.getButton().setOnAction(e -> {
            if (arena.getPlayerBar().getCurrentHp() <= 50) {
                castDefend();
                DEF.setDisable(true);
            }
        });

        ATK.getButton().setOnAction(e -> {
            if (!arena.isPlayerTurn()) return;
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
                "/img/explosion/explosion_thuong.png", 120,
                () -> afterPlayerAttack()
        );
    }

    private void castA1() {
        SkillEffect.castSkill(
                arena,
                arena.getPlayerView().getLayoutX() - 20,
                arena.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu4.png",
                20, 10,
                "/img/explosion/explosion_1.png", 150,
                () -> afterPlayerAttack()
        );
    }

    private void castA2() {
        SkillEffect.castSkill(
                arena,
                arena.getPlayerView().getLayoutX() - 20,
                arena.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu4.png",
                30, 15,
                "/img/explosion/explosion_2.png", 180,
                () -> afterPlayerAttack()
        );
    }

    private void castA3() {
        SkillEffect.castSkill(
                arena,
                arena.getPlayerView().getLayoutX() - 20,
                arena.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu4.png",
                35, 25,
                "/img/explosion/explosion_3.png", 220,
                () -> afterPlayerAttack()
        );
    }

    private void castHeal() {
        arena.getPlayerBar().heal(30);
    }

    private void castDefend() {
        // tuỳ bạn implement
    }

    // ============================================================
    // COOLDOWN THEO LƯỢT
    // ============================================================
    private boolean isSkillReady(SkillButton skill) {
        return !skillCooldownEnd.containsKey(skill)
                || turnCount >= skillCooldownEnd.get(skill);
    }

    private void startSkillCooldown(SkillButton skill, int waitTurns) {
        skillCooldownEnd.put(skill, turnCount + waitTurns);
        skill.setDisable(true);

        // overlay mờ
        drawOverlay(skill.getOverlay(), true);
    }

    // ============================================================
    // GỌI SAU MỖI LƯỢT
    // ============================================================
    public void nextTurn() {
        turnCount++;

        for (var entry : skillCooldownEnd.entrySet()) {
            SkillButton btn = entry.getKey();
            int readyTurn = entry.getValue();

            if (turnCount >= readyTurn) {
                btn.setDisable(false);
                drawOverlay(btn.getOverlay(), false);
            }
        }

        // DEF chỉ dùng được khi máu thấp
        if (arena.getPlayerBar().getCurrentHp() <= 50) {
            DEF.setDisable(false);
        } else {
            DEF.setDisable(true);
        }
    }

    // ============================================================
    // VẼ / TẮT OVERLAY
    // ============================================================
    private void drawOverlay(Canvas c, boolean active) {
        GraphicsContext g = c.getGraphicsContext2D();
        g.clearRect(0, 0, c.getWidth(), c.getHeight());

        if (active) {
            g.setFill(Color.rgb(0, 0, 0, 0.6));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
    }

    // ============================================================
    // BUTTON CREATOR
    // ============================================================
    private Button createRectButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefSize(65, 35);
        btn.setStyle("-fx-background-radius: 10; -fx-background-color:" + color
                + "; -fx-font-size:13px; -fx-text-fill:white;");
        return btn;
    }

    private Button createCircleButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefSize(35, 35);
        btn.setStyle("-fx-background-radius: 25; -fx-background-color:" + color
                + "; -fx-font-size:13px; -fx-text-fill:white;");
        return btn;
    }

    private void aiAttack() {
        if (arena.isGameOver()) return;

        PauseTransition delay = new PauseTransition(Duration.seconds(0.8));
        delay.setOnFinished(e -> {
            if (arena.isGameOver()) return;

            HealthBar aiBar = arena.getEnemyBar();

            // AI hết MP → đánh thường
            int mp = aiBar.getCurrentMp();

            int damage;
            int mpCost;
            String effect;
            String explosion;
            int explosionSize;

            // RANDOM SKILL
            if (mp >= 25 && Math.random() < 0.2) {
                // A3
                damage = 35;
                mpCost = 25;
                effect = "/img/attackEffect/chieu4.png";
                explosion = "/img/explosion/explosion_3.png";
                explosionSize = 220;
            } else if (mp >= 15 && Math.random() < 0.4) {
                // A2
                damage = 25;
                mpCost = 15;
                effect = "/img/attackEffect/chieu4.png";
                explosion = "/img/explosion/explosion_2.png";
                explosionSize = 180;
            } else if (mp >= 10 && Math.random() < 0.6) {
                // A1
                damage = 18;
                mpCost = 10;
                effect = "/img/attackEffect/chieu4.png";
                explosion = "/img/explosion/explosion_1.png";
                explosionSize = 150;
            } else {
                // Attack thường
                damage = 12;
                mpCost = 5;
                effect = "/img/attackEffect/chieu2.png";
                explosion = "/img/explosion/explosion_thuong.png";
                explosionSize = 120;
            }

            // TRỪ MP AI
            aiBar.takeDamage(0, mpCost);

            SkillEffect.castSkillAI(
                    arena,
                    arena.getEnemyView().getLayoutX() + 200,
                    arena.getEnemyView().getLayoutY() + 60,
                    effect,
                    damage,
                    explosion,
                    explosionSize,
                    () -> {

                        if (arena.getPlayerBar().getCurrentHp() <= 0) {
                            arena.setGameOver(true);
                            showGameOver("GAME OVER");
                            return;
                        }

                        // player còn sống → trả lượt
                        arena.startPlayerTurn();
                        nextTurn();
                    }
            );
        });
        delay.play();
    }

    private void showGameOver(String text) {
        Label label = new Label(text);
        label.setStyle("""
        -fx-font-size: 48px;
        -fx-text-fill: red;
        -fx-font-weight: bold;
    """);

        label.setLayoutX(500);
        label.setLayoutY(300);

        arena.getChildren().add(label);
    }

    private void afterPlayerAttack() {
        // AI chết ngay sau đòn đánh của player
        if (arena.getEnemyBar().getCurrentHp() <= 0) {
            arena.setGameOver(true);
            showGameOver("YOU WIN!");
            return;
        }

        // AI còn sống → mới được đánh
        arena.endPlayerTurn();
        aiAttack();
    }

}
