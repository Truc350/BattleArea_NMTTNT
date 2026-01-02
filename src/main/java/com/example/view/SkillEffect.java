package com.example.view;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class SkillEffect {
    public static void castSkill(ArenaView arena, double startX, double startY, String imagePath, int damage, int mpCost, String explosionPath, int explosionSize, Runnable onHit) {

        ImageView player = arena.getPlayerView();
        HealthBar enemyBar = arena.getEnemyBar();
        HealthBar playerBar = arena.getPlayerBar();

        // Không đủ MP → không đánh
        if (playerBar.getCurrentMp() < mpCost) {
            System.out.println("Không đủ MP để tung chiêu!");
            return;
        }

        // Trừ MP ngay khi bắt đầu đánh
        playerBar.takeDamage(0, mpCost);   // hpDmg = 0, mpDmg = mpCost

        ImageView skill = new ImageView(new Image(SkillEffect.class.getResourceAsStream(imagePath)));
        skill.setFitWidth(100);
        skill.setFitHeight(100);

        skill.setLayoutX(startX);
        skill.setLayoutY(startY);

        arena.getChildren().add(skill);

        ImageView enemy = arena.getEnemyView();

        // animation bay sang trái
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.0), skill);
        tt.setByX(-900);        // bay từ phải sang trái
        tt.setCycleCount(1);

        AnimationTimer checker = new AnimationTimer() {

            @Override
            public void handle(long now) {
                Bounds s = skill.localToScene(skill.getBoundsInLocal());
                Bounds e = enemy.localToScene(enemy.getBoundsInLocal());

                if (s.intersects(e)) {
                    // trúng => dừng bay, xóa hiệu ứng bay
                    tt.stop();
                    this.stop();
                    arena.getChildren().remove(skill);

                    // gọi hiệu ứng nổ
                    String ex = explosionPath != null ? explosionPath : "/img/explosion/explosion_thuong.png";
                    showExplosion(arena,
                            enemy.getLayoutX() + 50,
                            enemy.getLayoutY() + 60,
                            ex, explosionSize);

                    // trừ máu của enemy
                    enemyBar.takeDamage(damage, 0);
                    if (onHit != null) onHit.run(); // <<< QUAN TRỌNG
                    return;
                }
            }
        };


        tt.setOnFinished(e -> {
            checker.stop();
            if (arena.getChildren().contains(skill))
                arena.getChildren().remove(skill);
        });

        checker.start();
        tt.play();
    }

    private static void showExplosion(Pane arena, double x, double y, String explosionPath, int explosionSize) {
        ImageView boom = new ImageView(new Image(
                SkillEffect.class.getResourceAsStream(explosionPath)
        ));
        boom.setFitWidth(explosionSize);
        boom.setFitHeight(explosionSize);

        boom.setLayoutX(x - explosionSize / 2);
        boom.setLayoutY(y - explosionSize / 2);

        arena.getChildren().add(boom);

        PauseTransition pause = new PauseTransition(Duration.millis(250));
        pause.setOnFinished(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), boom);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(ev -> arena.getChildren().remove(boom));
            ft.play();
        });
        pause.play();
    }

    public static void castSkillAI(
            ArenaView arena,
            double startX, double startY,
            String imagePath,
            int damage,
            String explosionPath,
            int explosionSize,
            Runnable onHit
    ) {
        ImageView skill = new ImageView(
                new Image(SkillEffect.class.getResourceAsStream(imagePath))
        );

        skill.setFitWidth(100);
        skill.setFitHeight(100);
        skill.setLayoutX(startX);
        skill.setLayoutY(startY);

        arena.getChildren().add(skill);

        // Tính khoảng cách cần bay: từ vị trí bắt đầu đến trước player
        double targetX = arena.getPlayerView().getLayoutX() - 50;
        double distance = targetX - startX;

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), skill);
        tt.setByX(distance); // AI bay từ trái sang phải

        tt.setOnFinished(e -> {
            arena.getChildren().remove(skill);

            showExplosion(
                    arena,
                    arena.getPlayerView().getLayoutX() + 50,
                    arena.getPlayerView().getLayoutY() + 60,
                    explosionPath,
                    explosionSize
            );

            arena.getPlayerBar().takeDamage(damage, 0);

            if (onHit != null) onHit.run();

        });

        tt.play();
    }

    public static Node createGameOverLabel(String gameOver) {
        Label label = new Label(gameOver);
        label.setStyle("""
        -fx-font-size: 48px;
        -fx-text-fill: red;
        -fx-font-weight: bold;
    """);

        label.setLayoutX(500);
        label.setLayoutY(300);
        return label;
    }


}
