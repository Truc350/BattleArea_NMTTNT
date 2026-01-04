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

/**
 * SkillEffect - CHỈ XỬ LÝ ANIMATION, KHÔNG CÓ LOGIC GAME
 * Tất cả damage/MP được tính trong BattleController
 */
public class SkillEffect {

    /**
     * Animation skill bay từ Player → Enemy
     * @param onHit Callback khi skill chạm mục tiêu (gọi logic game ở đây)
     */
    public static void castSkill(
            ArenaView arena,
            double startX,
            double startY,
            String imagePath,
            String explosionPath,
            int explosionSize,
            Runnable onHit
    ) {
        ImageView skill = new ImageView(new Image(SkillEffect.class.getResourceAsStream(imagePath)));
        skill.setFitWidth(100);
        skill.setFitHeight(100);
        skill.setLayoutX(startX);
        skill.setLayoutY(startY);

        arena.getChildren().add(skill);

        ImageView enemy = arena.getEnemyView();

        // Animation bay sang trái
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.0), skill);
        tt.setByX(-900);
        tt.setCycleCount(1);

        AnimationTimer checker = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Bounds s = skill.localToScene(skill.getBoundsInLocal());
                Bounds e = enemy.localToScene(enemy.getBoundsInLocal());

                if (s.intersects(e)) {
                    // Trúng → dừng animation
                    tt.stop();
                    this.stop();
                    arena.getChildren().remove(skill);

                    // Hiệu ứng nổ
                    String ex = explosionPath != null ? explosionPath : "/img/explosion/explosion_thuong.png";
                    showExplosion(arena,
                            enemy.getLayoutX() + 50,
                            enemy.getLayoutY() + 60,
                            ex, explosionSize);

                    // ✅ GỌI CALLBACK - Controller xử lý logic game
                    if (onHit != null) onHit.run();
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

    /**
     * Animation skill bay từ AI → Player
     * @param onHit Callback khi skill chạm mục tiêu
     */
    public static void castSkillAI(
            ArenaView arena,
            double startX,
            double startY,
            String imagePath,
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

        ImageView player = arena.getPlayerView();

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), skill);
        tt.setByX(900); // AI bay từ trái sang phải

        AnimationTimer checker = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Bounds s = skill.localToScene(skill.getBoundsInLocal());
                Bounds p = player.localToScene(player.getBoundsInLocal());

                if (s.intersects(p)) {
                    tt.stop();
                    this.stop();
                    arena.getChildren().remove(skill);

                    // Show explosion tại vị trí player
                    showExplosion(
                            arena,
                            player.getLayoutX() + 50,
                            player.getLayoutY() + 60,
                            explosionPath,
                            explosionSize
                    );

                    // ✅ GỌI CALLBACK - Controller xử lý logic game
                    if (onHit != null) onHit.run();
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

    /**
     * Hiệu ứng nổ tại vị trí (x, y)
     */
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

    /**
     * Tạo label game over
     */
    public static Node createGameOverLabel(String text) {
        Label label = new Label(text);
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