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
        if (skillBar == null) {
            System.err.println("SkillBar is NULL!");
        }

        battleController.setSkillBar(skillBar);
        // Don't enable buttons here - AI will go first
        // skillBar.enableAllButtons();

        skillBar.setLayoutX(1000);
        skillBar.setLayoutY(550);
        arena.getChildren().add(skillBar);

        arena.requestFocus();
        Scene scene = new Scene(arena, 1300, 700);
        scene.setOnMouseClicked(e -> arena.requestFocus());

//        // START THE GAME - AI GOES FIRST
//        battleController.startGame();

        // HIỂN THỊ DIALOG CHỌN TURN ORDER NGAY TRONG ARENA
        arena.showTurnOrderDialog(new ArenaView.TurnOrderCallback() {
            @Override
            public void onPlayerFirst() {
                System.out.println("✅ Player chọn đi trước!");
                battleController.startGamePlayerFirst();
            }

            @Override
            public void onAIFirst() {
                System.out.println("✅ Player chọn AI đi trước!");
                battleController.startGameAIFirst();
            }
        });

        return scene;
    }

}