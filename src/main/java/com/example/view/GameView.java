package com.example.view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameView {

    private String arenaPath;
    private String characterPath;

    public GameView(String arenaPath, String characterPath) {
        this.arenaPath = arenaPath;
        this.characterPath = characterPath;
    }

    public Scene getScene() {
        ArenaView arena = new ArenaView(arenaPath, characterPath);
        arena.setupInitialDistance();
        PlayerSkillBar skillBar = new PlayerSkillBar(arena);

        skillBar.setLayoutX(1000);
        skillBar.setLayoutY(550);

        Pane root = new Pane(arena, skillBar);

        arena.requestFocus();

        return new Scene(root, 1300, 700);
    }


}
