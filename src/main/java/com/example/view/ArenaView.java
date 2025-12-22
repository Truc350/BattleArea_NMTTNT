package com.example.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.List;
import java.util.Random;

public class ArenaView extends Pane{

    private static final List<String> ENEMY_LIST = List.of(
            "/img/character/dausi_trai.png",
            "/img/character/phap_su_trai.png",
            "/img/character/xathu.png",
            "/img/character/trothu_trai.png"
    );

    private ImageView player, enemy;
    private HealthBar playerBar, enemyBar;

    public enum Turn {
        PLAYER, AI
    }
    private Turn currentTurn = Turn.PLAYER;
    private boolean gameOver = false;

    public ArenaView(String arenaPath, String playerPath){
        setPrefSize(1300, 700);

        ImageView bg = new ImageView(new Image(getClass().getResourceAsStream(arenaPath)));
        bg.setFitWidth(1300);
        bg.setFitHeight(700);

        // AI - trái
        String enemyPath = randomEnemy();
        enemy = new ImageView( new Image(getClass().getResourceAsStream(enemyPath)));
        enemy.setFitWidth(220);
        enemy.setFitHeight(260);
        enemy.setLayoutX(120);
        enemy.setLayoutY(280);

        // Player - phải
        player = new ImageView(new Image(getClass().getResourceAsStream(playerPath)));
        player.setFitWidth(220);
        player.setFitHeight(260);
        player.setLayoutX(1300 - 220 - 120);
        player.setLayoutY(280);

        // thanh máu của AI
        enemyBar = new HealthBar(HealthBar.Align.LEFT);
        enemyBar.setLayoutX(enemy.getLayoutX() + 70);
        enemyBar.setLayoutY(enemy.getLayoutY() - 80);

        // thanh máu của player
        playerBar = new HealthBar(HealthBar.Align.RIGHT);
        playerBar.setLayoutX(player.getLayoutX() + 70);
        playerBar.setLayoutY(player.getLayoutY() - 80);

        getChildren().addAll(bg, enemy, player, enemyBar, playerBar);

        // di chuyen player
        setOnKeyPressed(event -> {
            double step = 10; // tốc độ di chuyển mỗi lần nhấn

            switch (event.getCode()) {
                case LEFT: // phím mũi tên trái
                    player.setLayoutX(player.getLayoutX() - step);
                    break;

                case RIGHT: // phím mũi tên phải
                    player.setLayoutX(player.getLayoutX() + step);
                    break;
            }

            // Cập nhật vị trí thanh máu chạy theo nhân vật
            playerBar.setLayoutX(player.getLayoutX() + 70);
        });

        setFocusTraversable(true);
    }

    private String randomEnemy() {
        Random r = new Random();
        return ENEMY_LIST.get(r.nextInt(ENEMY_LIST.size()));
    }

    public ImageView getPlayerView() { return player; }
    public ImageView getEnemyView() { return enemy; }

    public HealthBar getPlayerBar() { return playerBar; }
    public HealthBar getEnemyBar() { return enemyBar; }


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

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean value) {
        gameOver = value;
    }
}
