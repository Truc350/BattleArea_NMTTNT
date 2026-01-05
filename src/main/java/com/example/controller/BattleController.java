package com.example.controller;

import com.example.manager.MatchHistoryManager;
import com.example.model.*;
import com.example.view.ArenaView;
import com.example.view.PlayerSkillBar;
import com.example.view.SkillEffect;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.LocalDateTime;

public class BattleController {
    private ArenaView arena;
    private PlayerSkillBar skillBar;

    private Game game;
    private Player player;
    private AIPlayer aiPlayer;
    private int currentTurn = 1;

    private String characterPath;
    private String enemyCharacterPath;

    private static final double UI_TO_MODEL_SCALE = 50.0;
    private static final double MOVE_STEP = 30.0;

    private boolean usedHealThisTurn = false;
    private boolean isDefending = false;

    public BattleController(ArenaView arena, String characterPath) {
        this.arena = arena;
        this.characterPath = characterPath;

        initializeGame();
        syncHealthBars();
        syncPositionsToModel();
        setupPlayerMovement();
    }

    // =====================================================
    // PLAYER MOVEMENT CONTROLS
    // =====================================================
    private void setupPlayerMovement() {
        arena.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            if (skillBar == null || !skillBar.isButtonsEnabled()) {
                return;
            }

            double currentX = arena.getPlayerView().getLayoutX();
            double newX = currentX;

            if (code == KeyCode.LEFT || code == KeyCode.A) {
                newX = Math.max(50, currentX - MOVE_STEP);
            } else if (code == KeyCode.RIGHT || code == KeyCode.D) {
                newX = Math.min(1200, currentX + MOVE_STEP);
            } else {
                return;
            }

            arena.getPlayerView().setLayoutX(newX);
            arena.getPlayerBar().setLayoutX(newX + 70);

            syncPositionsToModel();

            event.consume();
        });

        arena.setFocusTraversable(true);
        arena.requestFocus();
    }

    // =====================================================
    // GAME INITIALIZATION
    // =====================================================
    private void initializeGame() {
        double playerUIX = 960.0;
        double aiUIX = 120.0;

        Point playerPos = new Point(playerUIX / UI_TO_MODEL_SCALE, 0.0);
        Point aiPos = new Point(aiUIX / UI_TO_MODEL_SCALE, 0.0);

        Hero playerHero = createHeroFromPath(characterPath, playerPos);
        player = new Player(playerHero);

        HeroType aiType = HeroType.values()[(int)(Math.random() * 4)];
        Hero aiHeroTemplate = Hero.getHero(aiType, "AI", aiPos);

        aiPlayer = new AIPlayer(
                aiHeroTemplate.getName(),
                aiHeroTemplate.getMaxHP(),
                aiHeroTemplate.getMaxMP(),
                aiPos,
                aiHeroTemplate.getAttack(),
                aiHeroTemplate.getDefense()
        );

        aiPlayer.getSkills().clear();
        aiPlayer.setAttackRange(aiHeroTemplate.getAttackRange());
        for (Skill skill : aiHeroTemplate.getSkills()) {
            Skill newSkill = new Skill(
                    skill.getName(),
                    skill.getMpCost(),
                    skill.getCooldownTurns(),
                    skill.getDamage(),
                    skill.getHealHP(),
                    skill.getHealMP()
            );
            aiPlayer.getSkills().add(newSkill);
        }

        game = new Game(player, aiPlayer);
        enemyCharacterPath = getEnemyPathFromType(aiType);
    }

    private Hero createHeroFromPath(String path, Point position) {
        HeroType type;
        String name;

        if (path.contains("dausi")) {
            type = HeroType.FIGHTER;
            name = "Fighter";
        } else if (path.contains("phapsu") || path.contains("phap_su")) {
            type = HeroType.MAGE;
            name = "Mage";
        } else if (path.contains("xathu")) {
            type = HeroType.MARKSMAN;
            name = "Marksman";
        } else if (path.contains("trothu")) {
            type = HeroType.SUPPORT;
            name = "Support";
        } else {
            type = HeroType.FIGHTER;
            name = "Fighter";
        }

        return Hero.getHero(type, name, position);
    }

    private String getEnemyPathFromType(HeroType type) {
        return switch (type) {
            case FIGHTER -> "/img/character/dausi_trai.png";
            case MAGE -> "/img/character/phap_su_trai.png";
            case MARKSMAN -> "/img/character/xathu.png";
            case SUPPORT -> "/img/character/trothu_trai.png";
        };
    }

    // =====================================================
    // SYNC UI WITH MODEL
    // =====================================================
    private void syncHealthBars() {
        arena.getPlayerBar().syncWithHero(player.getHero());
        arena.getEnemyBar().syncWithHero(aiPlayer);
    }

    private void syncPositionsToModel() {
        double playerUIX = arena.getPlayerView().getLayoutX();
        double aiUIX = arena.getEnemyView().getLayoutX();

        double playerModelX = playerUIX / UI_TO_MODEL_SCALE;
        double aiModelX = aiUIX / UI_TO_MODEL_SCALE;

        player.getHero().setPosition(new Point(playerModelX, 0.0));
        aiPlayer.setPosition(new Point(aiModelX, 0.0));
    }

    private void updateCooldowns() {
        Hero hero = player.getHero();

        int cd1 = hero.getSkills().size() > 2 ? calculateRemainingCooldown(hero.getSkills().get(2)) : 0;
        int cd2 = hero.getSkills().size() > 3 ? calculateRemainingCooldown(hero.getSkills().get(3)) : 0;
        int cd3 = hero.getSkills().size() > 4 ? calculateRemainingCooldown(hero.getSkills().get(4)) : 0;
        int cdHeal = hero.getSkills().size() > 1 ? calculateRemainingCooldown(hero.getSkills().get(1)) : 0;

        skillBar.updateCooldowns(cd1, cd2, cd3, cdHeal, 0);
    }

    private int calculateRemainingCooldown(Skill skill) {
        if (skill == null) return 0;
        int remaining = skill.getCooldownTurns() - (currentTurn - skill.getLastUsedTurn());
        return Math.max(0, remaining);
    }

    // =====================================================
    // PLAYER ACTIONS
    // =====================================================
    public void onAttack() {
        Hero hero = player.getHero();

        if (!game.isPlayerInRange()) {
            showMessage("NGOÀI TẦM ĐÁNH!");
            return;
        }

        skillBar.disableAllButtons();

        double startX = arena.getPlayerView().getLayoutX() + 50;
        double startY = arena.getPlayerView().getLayoutY() + 100;

        SkillEffect.castSkill(arena, startX, startY,
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
                120, () -> {
                    boolean success = hero.useSkill("Basic Attack", currentTurn, aiPlayer);

                    if (success) {
                        syncHealthBars();

                        if (checkGameOver()) return;

                        endPlayerTurn();
                    } else {
                        skillBar.enableAllButtons();
                    }
                });
    }

    public void onSkillA1() {
        Hero hero = player.getHero();

        if (hero.getSkills().size() <= 2) {
            return;
        }

        String skillName = hero.getSkills().get(2).getName();
        executePlayerSkill(skillName,
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
                150);
    }

    public void onSkillA2() {
        Hero hero = player.getHero();

        if (hero.getSkills().size() <= 3) {
            return;
        }

        String skillName = hero.getSkills().get(3).getName();
        executePlayerSkill(skillName,
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
                180);
    }

    public void onSkillA3() {
        Hero hero = player.getHero();

        if (hero.getSkills().size() <= 4) {
            return;
        }

        String skillName = hero.getSkills().get(4).getName();
        executePlayerSkill(skillName,
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
                200);
    }

    public void onHeal() {
        if (usedHealThisTurn) {
            showMessage("ĐÃ DÙNG HEAL!");
            return;
        }

        Hero hero = player.getHero();
        boolean success = hero.useSkill("Mana Regen", currentTurn, hero);

        if (success) {
            usedHealThisTurn = true;
            syncHealthBars();
            updateCooldowns();

            skillBar.disableHealButton();

            showMessage("HEAL! (+10 HP, +15 MP)", Color.GREEN);

        } else {
            showMessage("HEAL ĐANG COOLDOWN!");
        }
    }

    public void onDefend() {
        Hero hero = player.getHero();

        if (hero.isDefending()) {
            showMessage("ĐÃ ĐANG PHÒNG THỦ!");
            return;
        }

        hero.setDefending(true);
        isDefending = true;

        showMessage("PHÒNG THỦ KÍCH HOẠT!", Color.PURPLE);

        skillBar.disableDefendButton();
    }

    private void executePlayerSkill(String skillName, String imagePath, String explosionPath, int explosionSize) {
        Hero hero = player.getHero();

        if (!game.isPlayerInRange()) {
            showMessage("NGOÀI TẦM ĐÁNH!");
            return;
        }

        boolean canUse = false;
        for (Skill skill : hero.getSkills()) {
            if (skill.getName().equals(skillName) && skill.canUse(currentTurn, hero.getMp())) {
                canUse = true;
                break;
            }
        }

        if (!canUse) {
            return;
        }

        skillBar.disableAllButtons();

        double startX = arena.getPlayerView().getLayoutX() + 50;
        double startY = arena.getPlayerView().getLayoutY() + 100;

        SkillEffect.castSkill(arena, startX, startY, imagePath, explosionPath, explosionSize, () -> {
            boolean success = hero.useSkill(skillName, currentTurn, aiPlayer);

            if (success) {
                syncHealthBars();

                if (checkGameOver()) return;

                endPlayerTurn();
            } else {
                skillBar.enableAllButtons();
                updateCooldowns();
            }
        });
    }

    private void showMessage(String text) {
        showMessage(text, Color.RED);
    }

    private void showMessage(String text, javafx.scene.paint.Color color) {
        Label msg = new Label(text);
        msg.setStyle("-fx-font-size: 20px; -fx-text-fill: " + toHexString(color) + "; -fx-font-weight: bold;");
        msg.setLayoutX(450);
        msg.setLayoutY(200);
        arena.getChildren().add(msg);

        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(e -> arena.getChildren().remove(msg));
        delay.play();
    }

    private String toHexString(javafx.scene.paint.Color color) {
        return String.format("#%02X%02X%02X",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

    // =====================================================
    // TURN MANAGEMENT
    // =====================================================
    private void endPlayerTurn() {
        currentTurn++;

        player.getHero().resetDefense();

        usedHealThisTurn = false;
        isDefending = false;

        updateCooldowns();

        skillBar.disableAllButtons();

        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(e -> executeAITurn());
        delay.play();
    }

    private void executeAITurn() {
        syncPositionsToModel();

        String action = aiPlayer.chooseBestAction(currentTurn, player.getHero(), game);

        if (action.equals("Mana Regen")) {
            handleAIHeal();
        } else if (action.equals("Defend")) {
            handleAIDefend();
        } else if (action.contains("Move")) {
            handleAIMovement(action);
        } else if (action.equals("Jump Up")) {
            handleAIJumpUp();
        } else {
            handleAISkill(action);
        }
    }

    private void handleAIMovement(String action) {
        double distance = game.getDistance();
        double aiRange = aiPlayer.getAttackRange();

        int stepsNeeded = (int) Math.ceil((distance - aiRange) / (Point.MOVE_SPEED * UI_TO_MODEL_SCALE));
        stepsNeeded = Math.max(1, Math.min(stepsNeeded, 5));

        performMultipleAIMoves(action, stepsNeeded, 0);
    }

    private void performMultipleAIMoves(String action, int totalSteps, int currentStep) {
        if (currentStep >= totalSteps) {
            boolean inRange = game.isAIInRange();

            if (inRange) {
                String nextAction = aiPlayer.chooseBestAction(currentTurn, player.getHero(), game);
                if (!nextAction.contains("Move") && !nextAction.equals("Jump Up") &&
                        !nextAction.equals("Mana Regen") && !nextAction.equals("Defend")) {
                    handleAISkill(nextAction);
                } else {
                    endAITurn();
                }
            } else {
                endAITurn();
            }
            return;
        }

        double currentX = arena.getEnemyView().getLayoutX();
        double newX = action.equals("Move Closer") ? currentX + 50 : currentX - 50;

        aiPlayer.executeMovement(action, player.getHero());

        MovementController.moveTo(arena.getEnemyView(), newX, () -> {
            arena.getEnemyBar().setLayoutX(arena.getEnemyView().getLayoutX() + 70);
            syncPositionsToModel();

            performMultipleAIMoves(action, totalSteps, currentStep + 1);
        });
    }

    private void handleAIJumpUp() {
        double currentX = arena.getEnemyView().getLayoutX();
        double newX = currentX - 100;

        aiPlayer.executeMovement("Jump Up", player.getHero());

        MovementController.moveTo(arena.getEnemyView(), newX, () -> {
            arena.getEnemyBar().setLayoutX(arena.getEnemyView().getLayoutX() + 70);
            syncHealthBars();
            syncPositionsToModel();

            String nextAction = aiPlayer.chooseBestAction(currentTurn, player.getHero(), game);

            if (!nextAction.contains("Move") && !nextAction.equals("Jump Up") &&
                    !nextAction.equals("Mana Regen") && !nextAction.equals("Defend")) {
                handleAISkill(nextAction);
            } else {
                endAITurn();
            }
        });
    }

    private void handleAIHeal() {
        boolean success = aiPlayer.useSkill("Mana Regen", currentTurn, aiPlayer);

        if (success) {
            syncHealthBars();

            showMessage("AI HEAL!", Color.CYAN);

            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(e -> {
                String nextAction = aiPlayer.chooseBestAction(currentTurn, player.getHero(), game);

                if (!nextAction.equals("Mana Regen") && !nextAction.equals("Defend") &&
                        !nextAction.contains("Move") && !nextAction.equals("Jump Up")) {
                    handleAISkill(nextAction);
                } else {
                    endAITurn();
                }
            });
            delay.play();
        } else {
            endAITurn();
        }
    }

    private void handleAIDefend() {
        aiPlayer.setDefending(true);

        showMessage("AI DEFEND!", Color.PURPLE);

        PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
        delay.setOnFinished(e -> {
            String nextAction = aiPlayer.chooseBestAction(currentTurn, player.getHero(), game);

            if (!nextAction.equals("Mana Regen") && !nextAction.equals("Defend") &&
                    !nextAction.contains("Move") && !nextAction.equals("Jump Up")) {
                handleAISkill(nextAction);
            } else {
                endAITurn();
            }
        });
        delay.play();
    }

    private void handleAISkill(String skillName) {
        if (!game.isAIInRange()) {
            endAITurn();
            return;
        }

        boolean success = aiPlayer.useSkill(skillName, currentTurn, player.getHero());

        if (!success) {
            endAITurn();
            return;
        }

        syncHealthBars();

        double startX = arena.getEnemyView().getLayoutX() + 50;
        double startY = arena.getEnemyView().getLayoutY() + 100;

        String imagePath = "/img/skills/attack.png";
        String explosionPath = "/img/explosion/explosion_thuong.png";
        int explosionSize = 120;

        if (skillName.contains("Ultimate") || skillName.contains("Deadly") || skillName.contains("Meteor")) {
            explosionSize = 200;
        } else if (skillName.contains("Burst") || skillName.contains("Lightning") || skillName.contains("Snipe")) {
            explosionSize = 180;
        } else if (!skillName.equals("Basic Attack")) {
            explosionSize = 150;
        }

        SkillEffect.castSkillAI(arena, startX, startY, imagePath, explosionPath, explosionSize, () -> {
            if (checkGameOver()) return;

            endAITurn();
        });
    }

    private void endAITurn() {
        currentTurn++;

        aiPlayer.resetDefense();

        skillBar.enableAllButtons();
        updateCooldowns();
    }

    private boolean checkGameOver() {
        boolean gameOver = false;
        boolean playerWin = false;

        if (player.getHero().getHp() <= 0) {
            skillBar.disableAllButtons();
            skillBar.showGameOver("YOU LOSE!");
            gameOver = true;
            playerWin = false;
        } else if (aiPlayer.getHp() <= 0) {
            skillBar.disableAllButtons();
            skillBar.showGameOver("YOU WIN!");
            gameOver = true;
            playerWin = true;
        }

        if (gameOver) {
            MatchHistory match = new MatchHistory(
                    playerWin,
                    characterPath,
                    enemyCharacterPath,
                    LocalDateTime.now(),
                    player.getHero().getName(),
                    aiPlayer.getName(),
                    Math.max(0, player.getHero().getHp()),
                    Math.max(0, aiPlayer.getHp())
            );

            MatchHistoryManager.getInstance().addMatch(match);
        }

        return gameOver;
    }

    public void setSkillBar(PlayerSkillBar skillBar) {
        this.skillBar = skillBar;
    }
}