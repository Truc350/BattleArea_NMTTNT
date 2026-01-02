package com.example.view;

import com.example.controller.GameController;
import javafx.scene.Scene;

public class GameView {

    private String arenaPath;
    private String characterPath;
    private GameController controller;

    public GameView(String arenaPath, String characterPath) {
        this.arenaPath = arenaPath;
        this.characterPath = characterPath;
        this.controller = GameController.getInstance();
    }

    public GameView(String arenaPath, String characterPath, GameController controller) {
        this.arenaPath = arenaPath;
        this.characterPath = characterPath;
        this.controller = controller;
    }

    public Scene getScene() {
        ArenaView arena = new ArenaView(arenaPath, characterPath, controller);
        arena.setupInitialDistance();

        PlayerSkillBar skillBar = new PlayerSkillBar(arena);
        skillBar.setLayoutX(1000);
        skillBar.setLayoutY(550);
        arena.getChildren().add(skillBar);

        arena.requestFocus();

        return new Scene(arena, 1300, 700);
    }


}
