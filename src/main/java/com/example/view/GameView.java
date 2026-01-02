package com.example.view;

import com.example.controller.BattleController;
import com.example.controller.GameController;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

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
        ArenaView arena = new ArenaView(arenaPath, characterPath);
        arena.setupInitialDistance();

        // Tạo BattleController trước (nó cần arena và characterPath)
        BattleController battleController = new BattleController(arena, characterPath);

        // Bây giờ tạo PlayerSkillBar với 2 tham số: arena + battleController
        PlayerSkillBar skillBar = new PlayerSkillBar(arena, battleController);

        battleController.setSkillBar(skillBar);

        skillBar.setLayoutX(1000);
        skillBar.setLayoutY(550);
        arena.getChildren().add(skillBar);

        arena.requestFocus();
        Scene scene = new Scene(arena, 1300, 700);
        scene.setOnMouseClicked(e -> arena.requestFocus());
        return scene;
    }

}
