package com.example.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ArenaView extends Pane{
    private ImageView player, enemy;
    private HealthBar playerBar, enemyBar;
    public ArenaView(){
        setPrefSize(1300, 700);

        ImageView bg = new ImageView(new Image(getClass().getResourceAsStream("/img/arena/sandau1.jpg")));
        bg.setFitWidth(1300);
        bg.setFitHeight(700);

        // AI - trái
        enemy = new ImageView( new Image(getClass().getResourceAsStream("/img/character/dausi_trai.png")));
        enemy.setFitWidth(220);
        enemy.setFitHeight(260);
        enemy.setLayoutX(120);
        enemy.setLayoutY(300);

        // Player - phải
        player = new ImageView(new Image(getClass().getResourceAsStream("/img/character/trothu.png")));
        player.setFitWidth(220);
        player.setFitHeight(260);
        player.setLayoutX(1300 - 220 - 120);
        player.setLayoutY(300);

        // thanh máu của AI
        enemyBar = new HealthBar();
        enemyBar.setLayoutX(enemy.getLayoutX() + 70);
        enemyBar.setLayoutY(enemy.getLayoutY() - 80);

        // thanh máu của player
        playerBar = new HealthBar();
        playerBar.setLayoutX(player.getLayoutX() + 70);
        playerBar.setLayoutY(player.getLayoutY() - 80);

        getChildren().addAll(bg, enemy, player, enemyBar, playerBar);
    }

    public ImageView getPlayerView() { return player; }
    public ImageView getEnemyView() { return enemy; }

    public HealthBar getPlayerBar() { return playerBar; }
    public HealthBar getEnemyBar() { return enemyBar; }

}
