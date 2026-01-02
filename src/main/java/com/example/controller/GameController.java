package com.example.controller;

import com.example.view.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class GameController {

    private static GameController instance;
    private final Stage stage;

    private String selectedArenaPath;
    private String selectedCharacterPath;

    public GameController(Stage stage) {
        this.stage = stage;
        instance = this;
    }

    public static GameController getInstance() {
        return instance;
    }

    public void showIntro() {
        Scene introScene = new IntroView().getScene();
        stage.setScene(introScene);
        stage.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> showArenaSelect());
        delay.play();
    }

    public void showArenaSelect() {
        Scene scene = new ArenaSelectView().getScene();
        stage.setScene(scene);
    }

    public void onArenaSelected(String arenaPath) {
        this.selectedArenaPath = arenaPath;
        showCharacterSelect(arenaPath);
    }

    public void showCharacterSelect(String arenaPath) {
        Scene scene = new CharacterSelectView(arenaPath).getScene();
        stage.setScene(scene);
    }

    public void onCharacterSelected(String characterPath) {
        this.selectedCharacterPath = characterPath;

        if (selectedArenaPath != null && selectedCharacterPath != null) {
            showGame(selectedArenaPath, selectedCharacterPath);
        }
    }

    public void showGame(String arenaPath, String characterPath) {
        Scene scene = new GameView(arenaPath, characterPath).getScene();
        stage.setScene(scene);
    }

    public void returnToArenaSelect() {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> showArenaSelect());
        delay.play();
    }

    public void onGameOver(boolean playerWon) {
        System.out.println(playerWon ? "🎉 Player thắng!" : "💀 Player thua!");

        // Delay 2 giây rồi quay về chọn arena
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> showArenaSelect());
        delay.play();
    }
}