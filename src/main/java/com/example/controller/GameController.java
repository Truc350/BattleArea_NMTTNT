package com.example.controller;

import com.example.view.*;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller chính - điều phối các màn hình
 * Singleton pattern
 */
public class GameController {
    private static GameController instance;
    private Stage stage;

    private String selectedArena;
    private String selectedCharacter;

//    private GameController(Stage stage) {
//        this.stage = stage;
//    }

    public static GameController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GameController chưa được khởi tạo! Gọi constructor trước.");
        }
        return instance;
    }

    public GameController(Stage stage, boolean initialize) {
        this.stage = stage;
        if (initialize) {
            instance = this;
        }
    }

    // Constructor cho MainApp
    public GameController(Stage stage) {
        this.stage = stage;
        instance = this;
    }

    // =====================================================
    // NAVIGATION
    // =====================================================

    /**
     * Hiển thị màn hình intro
     */
    public void showIntro() {
        IntroView intro = new IntroView();
        Scene scene = intro.getScene();
        stage.setScene(scene);
        stage.show();

        // Auto chuyển sang chọn sàn sau 2 giây
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> showArenaSelect());
        delay.play();
    }

    /**
     * Hiển thị màn hình chọn sàn đấu
     */
    public void showArenaSelect() {
        ArenaSelectView arenaSelect = new ArenaSelectView(this);
        Scene scene = arenaSelect.getScene();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Hiển thị màn hình chọn nhân vật
     */
    public void showCharacterSelect(String arenaPath) {
        this.selectedArena = arenaPath;

        CharacterSelectView characterSelect = new CharacterSelectView(this, arenaPath);
        Scene scene = characterSelect.getScene();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Hiển thị màn hình game (battle)
     */
    public void showGame(String arenaPath, String characterPath) {
        this.selectedArena = arenaPath;
        this.selectedCharacter = characterPath;

        GameView gameView = new GameView(arenaPath, characterPath, this);
        Scene scene = gameView.getScene();
        stage.setScene(scene);
        stage.show();

        System.out.println("🎮 Game bắt đầu!");
        System.out.println("   Arena: " + arenaPath);
        System.out.println("   Character: " + characterPath);
    }

    // =====================================================
    // EVENT HANDLERS
    // =====================================================

    /**
     * Xử lý khi chọn sàn đấu
     */
    public void onArenaSelected(String arenaPath) {
        System.out.println("✅ Chọn sàn: " + arenaPath);
        showCharacterSelect(arenaPath);
    }

    /**
     * Xử lý khi chọn nhân vật
     */
    public void onCharacterSelected(String characterPath) {
        System.out.println("✅ Chọn nhân vật: " + characterPath);
        showGame(selectedArena, characterPath);
    }

    /**
     * Xử lý khi game over
     */
    public void onGameOver(boolean playerWon) {
        System.out.println("\n🏁 GAME OVER!");
        System.out.println(playerWon ? "   🎉 Player thắng!" : "   💀 AI thắng!");

        // Sau 3 giây tự động quay về chọn sàn
        // (Logic này đã có trong PlayerSkillBar.showGameOver())
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public Stage getStage() {
        return stage;
    }

    public String getSelectedArena() {
        return selectedArena;
    }

    public String getSelectedCharacter() {
        return selectedCharacter;
    }
}