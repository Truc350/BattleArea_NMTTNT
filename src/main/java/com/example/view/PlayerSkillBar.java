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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PlayerSkillBar extends Pane {

    private final ArenaView arena;
    private final BattleController battleController;

    private SkillButton A1, A2, A3, DEF, HEAL, ATK;

    public PlayerSkillBar(ArenaView arena, BattleController battleController) {
        this.arena = arena;
        this.battleController = battleController;

        // ===== TẠO BUTTONS =====
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
        VBox col = new VBox(5, A1, A2, A3);
        col.setLayoutX(0);
        col.setLayoutY(0);

        HBox row = new HBox(20, DEF, HEAL);
        row.setLayoutX(-200);
        row.setLayoutY(45);

        ATK.setLayoutX(90);
        ATK.setLayoutY(35);

        getChildren().addAll(col, row, ATK);

        // ===== KẾT NỐI VỚI BATTLECONTROLLER =====
        ATK.getButton().setOnAction(e -> battleController.onAttack());
        A1.getButton().setOnAction(e -> battleController.onSkillA1());
        A2.getButton().setOnAction(e -> battleController.onSkillA2());
        A3.getButton().setOnAction(e -> battleController.onSkillA3());
        HEAL.getButton().setOnAction(e -> battleController.onHeal());
        DEF.getButton().setOnAction(e -> battleController.onDefend());
    }

    // =====================================================
    // CẬP NHẬT COOLDOWN TỪ MODEL
    // =====================================================
    /**
     * Cập nhật cooldown của các skill
     * @param cd1 Cooldown skill A1 (còn lại bao nhiêu turn)
     * @param cd2 Cooldown skill A2
     * @param cd3 Cooldown skill A3
     * @param cdHeal Cooldown Heal
     * @param cdDef Cooldown Defend
     */
    public void updateCooldowns(int cd1, int cd2, int cd3, int cdHeal, int cdDef) {
        System.out.println("   [SkillBar] Update CD - A1:" + cd1 + " A2:" + cd2 + " A3:" + cd3);
        // Disable/Enable buttons
        A1.getButton().setDisable(cd1 > 0);
        A2.getButton().setDisable(cd2 > 0);
        A3.getButton().setDisable(cd3 > 0);
        HEAL.getButton().setDisable(cdHeal > 0);
        DEF.getButton().setDisable(cdDef > 0);

        // Visual feedback
        A1.setOpacity(cd1 > 0 ? 0.5 : 1.0);
        A2.setOpacity(cd2 > 0 ? 0.5 : 1.0);
        A3.setOpacity(cd3 > 0 ? 0.5 : 1.0);
        HEAL.setOpacity(cdHeal > 0 ? 0.5 : 1.0);
        DEF.setOpacity(cdDef > 0 ? 0.5 : 1.0);

        // Overlay
        drawOverlay(A1.getOverlay(), cd1 > 0);
        drawOverlay(A2.getOverlay(), cd2 > 0);
        drawOverlay(A3.getOverlay(), cd3 > 0);
        drawOverlay(HEAL.getOverlay(), cdHeal > 0);
        drawOverlay(DEF.getOverlay(), cdDef > 0);
    }

    /**
     * Disable tất cả buttons (khi game over hoặc không phải lượt player)
     */
    public void disableAllButtons() {
        ATK.getButton().setDisable(true);
        A1.getButton().setDisable(true);
        A2.getButton().setDisable(true);
        A3.getButton().setDisable(true);
        HEAL.getButton().setDisable(true);
        DEF.getButton().setDisable(true);

        ATK.setOpacity(0.5);
        A1.setOpacity(0.5);
        A2.setOpacity(0.5);
        A3.setOpacity(0.5);
        HEAL.setOpacity(0.5);
        DEF.setOpacity(0.5);
    }

    /**
     * Enable tất cả buttons (khi đến lượt player)
     */
    public void enableAllButtons() {
        ATK.getButton().setDisable(false);
        A1.getButton().setDisable(false);
        A2.getButton().setDisable(false);
        A3.getButton().setDisable(false);
        HEAL.getButton().setDisable(false);
        DEF.getButton().setDisable(false);

        ATK.setOpacity(1.0);
        A1.setOpacity(1.0);
        A2.setOpacity(1.0);
        A3.setOpacity(1.0);
        HEAL.setOpacity(1.0);
        DEF.setOpacity(1.0);
    }

    // =====================================================
    // GAME OVER
    // =====================================================
    public void showGameOver(String txt) {
        Label lb = new Label(txt);
        lb.setStyle(
                "-fx-font-size: 50px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + (txt.equals("YOU WIN!") ? "gold" : "red") + ";" +
                        "-fx-effect: dropshadow(gaussian, black, 10, 0.5, 0, 0);"
        );
        lb.setLayoutX(450);
        lb.setLayoutY(300);
        arena.getChildren().add(lb);

        // Delay 3 giây rồi quay về chọn sàn đấu
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> {
            try {
                MainApp.showAreaSelect();
            } catch (Exception ex) {
                System.out.println("Không thể quay về màn hình chọn sàn: " + ex.getMessage());
            }
        });
        delay.play();
    }

    // =====================================================
    // UI HELPER
    // =====================================================
    private void drawOverlay(Canvas c, boolean on) {
        GraphicsContext g = c.getGraphicsContext2D();
        g.clearRect(0, 0, c.getWidth(), c.getHeight());
        if (on) {
            g.setFill(Color.rgb(0, 0, 0, 0.6));
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
    }

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
    public void disableHealButton() {
        HEAL.getButton().setDisable(true);
        HEAL.setOpacity(0.5);
        drawOverlay(HEAL.getOverlay(), true);
    }
    public void disableDefendButton() {
        DEF.getButton().setDisable(true);
        DEF.setOpacity(0.5);
        drawOverlay(DEF.getOverlay(), true);
    }
}