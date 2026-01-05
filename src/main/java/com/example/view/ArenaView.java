package com.example.view;

import com.example.controller.GameController;
import com.example.controller.MovementController;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

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
}