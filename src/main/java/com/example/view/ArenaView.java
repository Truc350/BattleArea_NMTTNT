package com.example.view;

import com.example.controller.GameController;
import com.example.controller.MovementController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

public class ArenaView extends Pane {

    // Danh sách ảnh enemy (AI) random mỗi trận
    private static final List<String> ENEMY_LIST = List.of(
            "/img/character/dausi_trai.png",
            "/img/character/phap_su_trai.png",
            "/img/character/xathu.png",
            "/img/character/trothu_trai.png"
    );

    private ImageView player;      // Nhân vật người chơi (bên phải)
    private ImageView enemy;       // Nhân vật AI (bên trái)
    private HealthBar playerBar;   // Thanh HP/MP người chơi
    private HealthBar enemyBar;    // Thanh HP/MP AI
    private GameController controller;  // Tham chiếu đến controller chính
    private String enemyPath;

    private TurnOrderCallback turnOrderCallback;

    public ArenaView(String arenaPath, String playerPath) {
        this.controller = GameController.getInstance();

        setPrefSize(1300, 700);

        // Background sân đấu
        ImageView bg = new ImageView(new Image(getClass().getResourceAsStream(arenaPath)));
        bg.setFitWidth(1300);
        bg.setFitHeight(700);

        // Tạo enemy random bên trái
        enemyPath = randomEnemy();
        enemy = new ImageView(new Image(getClass().getResourceAsStream(enemyPath)));

        if (enemyPath.contains("/img/character/trothu_trai.png")) {
            enemy.setFitWidth(280);
            enemy.setFitHeight(280);
        } else {
            enemy.setFitWidth(200);
            enemy.setFitHeight(260);
        }
        enemy.setPreserveRatio(true);
        enemy.setLayoutX(120);
        enemy.setLayoutY(280);

        // Tạo player bên phải
        player = new ImageView(new Image(getClass().getResourceAsStream(playerPath)));

        if (playerPath.contains("/img/character/trothu_phai.png")) {
            player.setFitWidth(280);
            player.setFitHeight(280);
        } else {
            player.setFitWidth(200);
            player.setFitHeight(260);
        }
        player.setPreserveRatio(true);
        player.setLayoutX(1300 - 220 - 120);
        player.setLayoutY(280);

        // Thanh máu enemy (căn trái)
        enemyBar = new HealthBar(HealthBar.Align.LEFT);
        enemyBar.setLayoutX(enemy.getLayoutX() + 70);
        enemyBar.setLayoutY(enemy.getLayoutY() - 80);

        // Thanh máu player (căn phải)
        playerBar = new HealthBar(HealthBar.Align.RIGHT);
        playerBar.setLayoutX(player.getLayoutX() + 70);
        playerBar.setLayoutY(player.getLayoutY() - 80);

        getChildren().addAll(bg, enemy, player, enemyBar, playerBar);

        // ===== DI CHUYỂN BẰNG PHÍM MŨI TÊN =====
        setOnKeyPressed(event -> {
            double step = 10;

            switch (event.getCode()) {
                case LEFT:
                    player.setLayoutX(player.getLayoutX() - step);
                    break;
                case RIGHT:
                    player.setLayoutX(player.getLayoutX() + step);
                    break;
                default:
                    break;
            }

            playerBar.setLayoutX(player.getLayoutX() + 70);
        });

        setFocusTraversable(true);
        requestFocus();
    }

    private String randomEnemy() {
        Random r = new Random();
        return ENEMY_LIST.get(r.nextInt(ENEMY_LIST.size()));
    }

    // ===== GETTER =====
    public ImageView getPlayerView() {
        return player;
    }

    public ImageView getEnemyView() {
        return enemy;
    }

    public HealthBar getPlayerBar() {
        return playerBar;
    }

    public HealthBar getEnemyBar() {
        return enemyBar;
    }

    // ===== ĐẶT VỊ TRÍ BAN ĐẦU =====
    public void setupInitialDistance() {
        MovementController.moveTo(enemy, 220, () -> {
            enemyBar.setLayoutX(enemy.getLayoutX() + 70);
        });

        MovementController.moveTo(player, 1300 - 220 - 250, () -> {
            playerBar.setLayoutX(player.getLayoutX() + 70);
        });
    }

    public String getEnemyImagePath() {
        return enemyPath;
    }

    // ===== HIỂN THỊ DIALOG CHỌN TURN ORDER =====
    public void showTurnOrderDialog(TurnOrderCallback callback) {
        this.turnOrderCallback = callback;

        // Tạo overlay tối
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: transparent;");
        overlay.setPrefSize(1300, 700);

        // Container chính
        VBox dialogBox = new VBox(6);
        dialogBox.setAlignment(Pos.CENTER);
        dialogBox.setPadding(new Insets(12, 20, 12, 20));
        dialogBox.setStyle("""
                -fx-background-color: rgba(30, 30, 30, 0.95);
                -fx-border-color: #FFD700;
                -fx-border-width: 2.5;
                -fx-border-radius: 12;
                -fx-background-radius: 20;
                """);
        dialogBox.setMaxWidth(350);
        dialogBox.setMaxHeight(180);
        dialogBox.setEffect(new DropShadow(20, Color.GOLD));

        // Tiêu đề
        Label title = new Label("CHỌN LƯỢT ĐI");
        title.setStyle("""
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-text-fill: #FFD700;
                """);
        title.setEffect(new DropShadow(10, Color.BLACK));

        // Mô tả
        Label description = new Label("Bạn muốn đánh trước hay để AI đánh trước?");
        description.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: white;
                -fx-text-alignment: center;
                """);
        description.setWrapText(true);
        description.setMaxWidth(280);

        // Nút Player đánh trước
        Button playerFirstBtn = createDialogButton(
                "TÔI ĐI TRƯỚC",
                "#2ecc71",
                "#27ae60"
        );

        // Nút AI đánh trước
        Button aiFirstBtn = createDialogButton(
                "AI ĐI TRƯỚC",
                "#e74c3c",
                "#c0392b"
        );

        // Container cho 2 nút
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(playerFirstBtn, aiFirstBtn);

        // Thêm vào dialog
        dialogBox.getChildren().addAll(title, description, buttonBox);

        // Thêm vào overlay
        overlay.getChildren().add(dialogBox);

        // Xử lý sự kiện
        playerFirstBtn.setOnAction(e -> {
            getChildren().remove(overlay);
            if (callback != null) {
                callback.onPlayerFirst();
            }
        });

        aiFirstBtn.setOnAction(e -> {
            getChildren().remove(overlay);
            if (callback != null) {
                callback.onAIFirst();
            }
        });

        // Thêm overlay vào ArenaView
        getChildren().add(overlay);
    }

    private Button createDialogButton(String text, String normalColor, String hoverColor) {
        Button btn = new Button(text);
        btn.setPrefSize(120, 35);
        btn.setStyle(String.format("""
                -fx-font-size: 13px;
                -fx-font-weight: bold;
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-background-radius: 12;
                -fx-cursor: hand;
                """, normalColor));
        btn.setEffect(new DropShadow(8, Color.BLACK));

        // Hover effect
        btn.setOnMouseEntered(e -> {
            btn.setStyle(String.format("""
                    -fx-font-size: 16px;
                    -fx-font-weight: bold;
                    -fx-background-color: %s;
                    -fx-text-fill: white;
                    -fx-background-radius: 12;
                    -fx-cursor: hand;
                    """, hoverColor));
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(String.format("""
                    -fx-font-size: 16px;
                    -fx-font-weight: bold;
                    -fx-background-color: %s;
                    -fx-text-fill: white;
                    -fx-background-radius: 12;
                    -fx-cursor: hand;
                    """, normalColor));
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });

        return btn;
    }

    // Interface callback
    public interface TurnOrderCallback {
        void onPlayerFirst();
        void onAIFirst();
    }
}