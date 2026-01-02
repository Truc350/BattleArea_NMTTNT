package com.example.controller;

import com.example.model.Game;
import com.example.view.*;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController {
    private Game game;
    private Stage primaryStage;

    // Lưu lại arena và character đã chọn
    private String selectedArenaPath;
    private String selectedCharacterPath;

    public GameController(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.game = new Game();

        primaryStage.setTitle("Battle Area");
        primaryStage.setResizable(false);
    }

    /**
     * Bắt đầu game - hiển thị intro
     */
    public void start() {
        showIntro();
    }

    /**
     * Hiển thị màn hình intro (3 giây tự động chuyển)
     */
    private void showIntro() {
        IntroView introView = new IntroView();
        Scene scene = introView.getScene();
        primaryStage.setScene(scene);
        primaryStage.show();

        // Tự động chuyển sang ArenaSelect sau 3 giây
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> showArenaSelect());
        delay.play();
    }

    /**
     * Hiển thị màn hình chọn Arena
     */
    public void showArenaSelect() {
        ArenaSelectView arenaSelectView = new ArenaSelectView(this);
        Scene scene = arenaSelectView.getScene();
        primaryStage.setScene(scene);
    }

    /**
     * Được gọi khi user click chọn arena
     */
    public void onArenaSelected(String arenaPath) {
        this.selectedArenaPath = arenaPath;
        showCharacterSelect();
    }

    /**
     * Hiển thị màn hình chọn nhân vật
     */
    private void showCharacterSelect() {
        CharacterSelectView characterSelectView = new CharacterSelectView(this, selectedArenaPath);
        Scene scene = characterSelectView.getScene();
        primaryStage.setScene(scene);
    }

    /**
     * Được gọi khi user click chọn nhân vật
     */
    public void onCharacterSelected(String characterPath) {
        this.selectedCharacterPath = characterPath;
        startBattle();
    }

    /**
     * Bắt đầu trận chiến
     */
    private void startBattle() {
        // Tạo GameView với arena và character đã chọn
        GameView gameView = new GameView(selectedArenaPath, selectedCharacterPath, this);
        Scene scene = gameView.getScene();
        primaryStage.setScene(scene);
    }

    /**
     * Được gọi khi game kết thúc
     */
    public void onGameOver(boolean playerWon) {
        System.out.println(playerWon ? "🎉 Player thắng!" : "💀 Player thua!");

        // Delay 2 giây rồi quay về chọn arena
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> showArenaSelect());
        delay.play();
    }

    /**
     * Getter cho game model
     */
    public Game getGame() {
        return game;
    }

    /**
     * Getter cho stage
     */
    public Stage getStage() {
        return primaryStage;
    }

}
