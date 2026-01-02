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

    // Quản lý lượt chơi
    public enum Turn {
        PLAYER, AI
    }

    private Turn currentTurn = Turn.PLAYER;  // Player đánh trước
    private boolean gameOver = false;

    public ArenaView(String arenaPath, String playerPath) {
        this.controller = GameController.getInstance();  // Lấy singleton

        setPrefSize(1300, 700);

        // Background sân đấu
        ImageView bg = new ImageView(new Image(getClass().getResourceAsStream(arenaPath)));
        bg.setFitWidth(1300);
        bg.setFitHeight(700);

        // Tạo enemy random bên trái
        String enemyPath = randomEnemy();
        enemy = new ImageView(new Image(getClass().getResourceAsStream(enemyPath)));
        // AI - Set kích thước riêng cho trothu
        if (enemyPath.contains("/img/character/trothu_trai.png")) {
            enemy.setFitWidth(280);  // ← Rộng hơn (như trong CharacterSelectView)
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

        // Player - Set kích thước riêng cho trothu
        if (playerPath.contains("/img/character/trothu_phai.png")) {
            player.setFitWidth(280);  // ← Rộng hơn
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

        // Thêm tất cả vào Pane (thứ tự: bg đầu tiên để nằm dưới)
        getChildren().addAll(bg, enemy, player, enemyBar, playerBar);

//        // Đặt vị trí ban đầu có khoảng cách (animation mượt)
//        setupInitialDistance();

        // ===== DI CHUYỂN BẰNG PHÍM MŨI TÊN =====
        setOnKeyPressed(event -> {
            double step = 10; // Tốc độ mỗi lần nhấn

            switch (event.getCode()) {
                case LEFT:  // Lùi xa
                    player.setLayoutX(player.getLayoutX() - step);
                    break;
                case RIGHT: // Tiến gần
                    player.setLayoutX(player.getLayoutX() + step);
                    break;
                default:
                    break;
            }

            // Thanh máu player di chuyển theo nhân vật
            playerBar.setLayoutX(player.getLayoutX() + 70);
        });

        // Cho phép Pane nhận sự kiện phím
        setFocusTraversable(true);
        requestFocus(); // Tập trung ngay khi vào trận
    }

    // Random enemy mỗi trận
    private String randomEnemy() {
        Random r = new Random();
        return ENEMY_LIST.get(r.nextInt(ENEMY_LIST.size()));
    }

    // ===== GETTER CHO CÁC CLASS KHÁC DÙNG =====
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

    // ===== QUẢN LÝ LƯỢT =====
    public boolean isPlayerTurn() {
        return currentTurn == Turn.PLAYER;
    }

    public void endPlayerTurn() {
        currentTurn = Turn.AI;
    }

    public void endAITurn() {
        currentTurn = Turn.PLAYER;
    }

    public void startPlayerTurn() {
        currentTurn = Turn.PLAYER;
    }

    // ===== GAME OVER =====
    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean value) {
        gameOver = value;

        if (value && controller != null) {
            // Kiểm tra ai thắng dựa vào HP
            boolean playerWon = enemyBar.getCurrentHp() <= 0;
            controller.onGameOver(playerWon);
        }
    }

    // ===== ĐẶT VỊ TRÍ BAN ĐẦU (CÓ KHOẢNG CÁCH) =====
    public void setupInitialDistance() {
        MovementController.moveTo(enemy, 220, () -> {
            // ✅ Callback: cập nhật thanh máu SAU KHI animation xong
            enemyBar.setLayoutX(enemy.getLayoutX() + 70);
        });

        MovementController.moveTo(player, 1300 - 220 - 250, () -> {
            // ✅ Callback: cập nhật thanh máu SAU KHI animation xong
            playerBar.setLayoutX(player.getLayoutX() + 70);
        });
    }
}
