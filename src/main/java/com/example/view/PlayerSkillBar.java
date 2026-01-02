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
    private final BattleController battleController; // Tham chiếu đến logic thật

    private SkillButton A1, A2, A3, DEF, HEAL, ATK;
    private Text a1Cooldown, a2Cooldown, a3Cooldown;
    private Text healCooldown, defCooldown;

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

        // ✅ TẠO TEXT COOLDOWN
        a1Cooldown = createCooldownText();
        a2Cooldown = createCooldownText();
        a3Cooldown = createCooldownText();
        healCooldown = createCooldownText();
        defCooldown = createCooldownText();

        // ===== LAYOUT VỚI COOLDOWN TEXT =====
        // Column cho A1, A2, A3 với cooldown text bên dưới
        VBox a1Box = new VBox(2, A1, a1Cooldown);
        VBox a2Box = new VBox(2, A2, a2Cooldown);
        VBox a3Box = new VBox(2, A3, a3Cooldown);

        // ===== LAYOUT =====
        VBox col = new VBox(5, a1Box, a2Box, a3Box);
        col.setLayoutX(0);
        col.setLayoutY(0);

        // Row cho DEF, HEAL với cooldown text bên dưới
        VBox defBox = new VBox(3, DEF, defCooldown);
        VBox healBox = new VBox(3, HEAL, healCooldown);

        HBox row = new HBox(20, defBox, healBox);
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

    private Text createCooldownText() {
        Text text = new Text("");
        text.setFont(Font.font("Arial", 14));
        text.setFill(Color.RED);
        text.setStyle("-fx-font-weight: bold;");
        return text;
    }

    public void updateCooldowns(int cd1, int cd2, int cd3, int cdHeal, int cdDef) {
//        a1Cooldown.setText(cd1 > 0 ? "CD: " + cd1 : "");
//        a2Cooldown.setText(cd2 > 0 ? "CD: " + cd2 : "");
//        a3Cooldown.setText(cd3 > 0 ? "CD: " + cd3 : "");

        // Disable button khi đang cooldown
        A1.getButton().setDisable(cd1 > 0);
        A2.getButton().setDisable(cd2 > 0);
        A3.getButton().setDisable(cd3 > 0);

        // Visual feedback
        A1.setOpacity(cd1 > 0 ? 0.5 : 1.0);
        A2.setOpacity(cd2 > 0 ? 0.5 : 1.0);
        A3.setOpacity(cd3 > 0 ? 0.5 : 1.0);

        // Overlay cho A1, A2, A3
        drawOverlay(A1.getOverlay(), cd1 > 0);
        drawOverlay(A2.getOverlay(), cd2 > 0);
        drawOverlay(A3.getOverlay(), cd3 > 0);

        // HEAL, DEF
//        healCooldown.setText(cdHeal > 0 ? "CD: " + cdHeal : "");
//        defCooldown.setText(cdDef > 0 ? "CD: " + cdDef : "");

        HEAL.getButton().setDisable(cdHeal > 0);
        DEF.getButton().setDisable(cdDef > 0);

        HEAL.setOpacity(cdHeal > 0 ? 0.5 : 1.0);
        DEF.setOpacity(cdDef > 0 ? 0.5 : 1.0);

        // Overlay cho HEAL, DEF
        drawOverlay(HEAL.getOverlay(), cdHeal > 0);
        drawOverlay(DEF.getOverlay(), cdDef > 0);
    }

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

        System.out.println("🚫 All buttons disabled!");
    }
}