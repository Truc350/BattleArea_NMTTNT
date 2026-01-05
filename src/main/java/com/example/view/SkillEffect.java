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
 * ✅ FIX: Tính toán hướng bay dựa trên vị trí hiện tại của Player/AI
 */
public class SkillEffect {

    /**
     * Animation skill bay từ Player → Enemy
     * ✅ FIX: Tự động tính hướng dựa trên vị trí thực tế
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
        ImageView player = arena.getPlayerView();
        ImageView enemy = arena.getEnemyView();

        // ✅ LẤY VỊ TRÍ HIỆN TẠI THỰC TẾ
        double playerX = player.getLayoutX();
        double enemyX = enemy.getLayoutX();

        // ✅ TÍNH HƯỚNG: Player ở bên PHẢI enemy → bay TRÁI, ngược lại → bay PHẢI
        boolean flyLeft = playerX > enemyX;

        ImageView skill = new ImageView(new Image(SkillEffect.class.getResourceAsStream(imagePath)));
        skill.setFitWidth(100);
        skill.setFitHeight(100);
        skill.setLayoutX(startX);
        skill.setLayoutY(startY);

        // ✅ Flip ảnh nếu bay sang phải
        if (!flyLeft) {
            skill.setScaleX(-1);  // Lật ngang ảnh skill
        }

        arena.getChildren().add(skill);

        // ✅ TÍNH KHOẢNG CÁCH CẦN BAY
        double distance = Math.abs(enemyX - playerX);

        // ✅ Animation bay theo HƯỚNG ĐÚNG
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.0), skill);
        if (flyLeft) {
            tt.setByX(-distance);  // Bay sang trái
        } else {
            tt.setByX(distance);   // Bay sang phải
        }
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

                    // Hiệu ứng nổ TẠI VỊ TRÍ ENEMY HIỆN TẠI
                    String ex = explosionPath != null ? explosionPath : "/img/explosion/explosion_thuong.png";
                    showExplosion(arena,
                            enemy.getLayoutX() + 50,
                            enemy.getLayoutY() + 60,
                            ex, explosionSize);

                    System.out.println("   [SkillEffect] Callback triggered!");

                    // ✅ GỌI CALLBACK - Controller xử lý logic game
                    if (onHit != null) {
                        onHit.run();
                    } else {
                        System.err.println("   ❌ Callback is NULL!");
                    }
                    return;
                }
            }
        };

        tt.setOnFinished(e -> {
            checker.stop();
            if (arena.getChildren().contains(skill)) {
                arena.getChildren().remove(skill);
                System.out.println("   [SkillEffect] Animation finished without hit - removing skill");
            }
        });

        System.out.println("   [SkillEffect] Flying " + (flyLeft ? "LEFT" : "RIGHT") +
                " | Distance: " + distance + "px");
        checker.start();
        tt.play();
    }

    /**
     * Animation skill bay từ AI → Player
     * ✅ FIX: Tự động tính hướng dựa trên vị trí thực tế
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
        ImageView player = arena.getPlayerView();
        ImageView enemy = arena.getEnemyView();

        // ✅ LẤY VỊ TRÍ HIỆN TẠI THỰC TẾ
        double playerX = player.getLayoutX();
        double enemyX = enemy.getLayoutX();

        // ✅ TÍNH HƯỚNG: AI ở bên TRÁI player → bay PHẢI, ngược lại → bay TRÁI
        boolean flyRight = enemyX < playerX;

        ImageView skill = new ImageView(
                new Image(SkillEffect.class.getResourceAsStream(imagePath))
        );

        skill.setFitWidth(100);
        skill.setFitHeight(100);
        skill.setLayoutX(startX);
        skill.setLayoutY(startY);

        // ✅ Flip ảnh nếu bay sang trái
        if (!flyRight) {
            skill.setScaleX(-1);  // Lật ngang ảnh skill
        }

        arena.getChildren().add(skill);

        // ✅ TÍNH KHOẢNG CÁCH CẦN BAY
        double distance = Math.abs(playerX - enemyX);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(1), skill);
        if (flyRight) {
            tt.setByX(distance);   // Bay sang phải
        } else {
            tt.setByX(-distance);  // Bay sang trái
        }

        AnimationTimer checker = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Bounds s = skill.localToScene(skill.getBoundsInLocal());
                Bounds p = player.localToScene(player.getBoundsInLocal());

                if (s.intersects(p)) {
                    tt.stop();
                    this.stop();
                    arena.getChildren().remove(skill);

                    // Show explosion TẠI VỊ TRÍ PLAYER HIỆN TẠI
                    showExplosion(
                            arena,
                            player.getLayoutX() + 50,
                            player.getLayoutY() + 60,
                            explosionPath,
                            explosionSize
                    );

                    System.out.println("   [SkillEffect AI] Callback triggered!");

                    // ✅ GỌI CALLBACK - Controller xử lý logic game
                    if (onHit != null) {
                        onHit.run();
                    } else {
                        System.err.println("   ❌ AI Callback is NULL!");
                    }
                    return;
                }
            }
        };

        tt.setOnFinished(e -> {
            checker.stop();
            if (arena.getChildren().contains(skill)) {
                arena.getChildren().remove(skill);
                System.out.println("   [SkillEffect AI] Animation finished without hit - removing skill");
            }
        });

        System.out.println("   [SkillEffect AI] Flying " + (flyRight ? "RIGHT" : "LEFT") +
                " | Distance: " + distance + "px");
        checker.start();
        tt.play();
    }

    /**
     * Hiệu ứng nổ tại vị trí (x, y)
     */
    private static void showExplosion(Pane arena, double x, double y, String explosionPath, int explosionSize) {
        System.out.println("   [SkillEffect] Showing explosion at X=" + x + " Y=" + y);

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
            ft.setOnFinished(ev -> {
                arena.getChildren().remove(boom);
                System.out.println("   [SkillEffect] Explosion animation complete");
            });
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