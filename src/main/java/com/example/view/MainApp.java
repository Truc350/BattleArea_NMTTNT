package com.example.view;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {
    private static Stage mainStage;
    private PauseTransition introDelay;

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        stage.setTitle("Battle Arena");
        stage.setResizable(false);

        showIntro();

        stage.setOnShown(e -> startIntroDelay());

        stage.show();
    }

    private void showIntro() {
        Scene introScene = new IntroView().getScene();
        mainStage.setScene(introScene);
    }

    private void startIntroDelay() {
        introDelay = new PauseTransition(Duration.seconds(3));
        introDelay.setOnFinished(e -> showArenaSelect());
        introDelay.playFromStart();
    }

    public static void showArenaSelect() {
        mainStage.setScene(new ArenaSelectView().getScene());
    }

    public static void showCharacterSelect(String arenaPath) {
        mainStage.setScene(new CharacterSelectView(arenaPath).getScene());
    }

    public static void showGame(String arenaPath, String characterPath) {
        mainStage.setScene(new GameView(arenaPath, characterPath).getScene());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
