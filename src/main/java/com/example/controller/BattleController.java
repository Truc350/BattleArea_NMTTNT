package com.example.controller;

import com.example.model.*;
import com.example.view.ArenaView;
import com.example.view.PlayerSkillBar;
import com.example.view.SkillEffect;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.Random;

public class BattleController {
    private final ArenaView arenaView;
    private PlayerSkillBar skillBar; // 🔥 gán sau bằng setter
    private final Hero playerHero;
    private final Hero enemyHero;
    private final Game game;
    private long currentTime;
    private final Random random = new Random();


    public BattleController(ArenaView arenaView, String characterPath) {
        this.arenaView = arenaView;

        playerHero = createPlayerHero(characterPath);
        enemyHero = createRandomEnemy();

        Player playerWrapper = new Player(playerHero);
        game = new Game(playerWrapper, (AIPlayer) enemyHero);

        currentTime = System.currentTimeMillis();
        updateHealthBars();

        arenaView.startPlayerTurn();
    }


    public void setSkillBar(PlayerSkillBar skillBar) {
        this.skillBar = skillBar;
    }

    // ===================== HERO CREATE =====================

    private Hero createPlayerHero(String path) {
        Point pos = new Point(1000, 280);
        if (path.contains("dausi")) return new Fighter("Bạn", 100, 100, pos, 16, 7);
        if (path.contains("xathu")) return new Marksman("Bạn", 100, 100, pos, 22, 4);
        if (path.contains("phapsu")) return new Mage("Bạn", 100, 100, pos, 12, 5);
        if (path.contains("trothu")) return new Support("Bạn", 100, 100, pos, 10, 12);
        return new Fighter("Bạn", 100, 100, pos, 16, 7);
    }

    private Hero createRandomEnemy() {
        Point pos = new Point(200, 280);
        int rand = random.nextInt(4);
        String name = "DEATH BOT ";
        return switch (rand) {
            case 0 -> new AIPlayer(name + "Fighter", 100, 100, pos, 18, 8);
            case 1 -> new AIPlayer(name + "Marksman", 100, 100, pos, 22, 4);
            case 2 -> new AIPlayer(name + "Mage", 100, 100, pos, 12, 5);
            case 3 -> new AIPlayer(name + "Support", 100, 100, pos, 10, 12);
            default -> new AIPlayer(name, 100, 100, pos, 18, 8);
        };
    }

    private void updateHealthBars() {
        arenaView.getPlayerBar().setHp(playerHero.getHp());
        arenaView.getPlayerBar().setMp(playerHero.getMp());
        arenaView.getEnemyBar().setHp(enemyHero.getHp());
        arenaView.getEnemyBar().setMp(enemyHero.getMp());
    }

    // ===================== PLAYER ACTION =====================

    public void onAttack() {
        if (!arenaView.isPlayerTurn()) return;

        int damage = playerHero.getAttack();
        enemyHero.takeDamage(damage);
        updateHealthBars();

        SkillEffect.castSkill(
                arenaView,
                arenaView.getPlayerView().getLayoutX() - 20,
                arenaView.getPlayerView().getLayoutY() + 60,
                "/img/attackEffect/chieu2.png",
                damage,
                0,
                "/img/explosion/explosion_thuong.png",
                120,
                this::endPlayerTurn
        );
    }

    public void onSkillA1() {
        castSkill(20, 10, "/img/attackEffect/chieu2.png", 120);
    }

    public void onSkillA2() {
        castSkill(30, 15, "/img/attackEffect/chieu4.png", 140);
    }

    public void onSkillA3() {
        castSkill(45, 25, "/img/attackEffect/chieu4.png", 160);
    }

    private void castSkill(int damage, int mpCost, String effectPath, int explosionSize) {
        if (!arenaView.isPlayerTurn()) return;
        if (playerHero.getMp() < mpCost) return;

        playerHero.setMp(playerHero.getMp() - mpCost);
        enemyHero.takeDamage(damage);
        updateHealthBars();

        SkillEffect.castSkill(
                arenaView,
                arenaView.getPlayerView().getLayoutX() - 20,
                arenaView.getPlayerView().getLayoutY() + 60,
                effectPath,
                damage,
                mpCost,
                "/img/explosion/explosion_1.png",
                explosionSize,
                this::endPlayerTurn
        );
    }

    public void onHeal() {
        if (!arenaView.isPlayerTurn()) return;
        playerHero.setHp(Math.min(100, playerHero.getHp() + 35));
        updateHealthBars();
        endPlayerTurn();
    }

    // ===================== TURN & AI =====================

    private void endPlayerTurn() {
        currentTime += 1000;
        checkGameOver();
        if (arenaView.isGameOver()) return;

        arenaView.endPlayerTurn();

        PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
        delay.setOnFinished(e -> aiRandomAttack());
        delay.play();
    }

    private void aiRandomAttack() {
        int damage = enemyHero.getAttack() + random.nextInt(10, 20);
        playerHero.takeDamage(damage);
        updateHealthBars();

        SkillEffect.castSkillAI(
                arenaView,
                arenaView.getEnemyView().getLayoutX() + 200,
                arenaView.getEnemyView().getLayoutY() + 60,
                "/img/attackEffect/chieu2.png",
                damage,
                "/img/explosion/explosion_thuong.png",
                120,
                () -> {
                    checkGameOver();
                    if (!arenaView.isGameOver()) {
                        arenaView.startPlayerTurn();
                    }
                }
        );
    }

    private void checkGameOver() {
        if (playerHero.getHp() <= 0) {
            arenaView.setGameOver(true);
            GameController.getInstance().onGameOver(false);
        } else if (enemyHero.getHp() <= 0) {
            arenaView.setGameOver(true);
            GameController.getInstance().onGameOver(true);
        }
    }

    public void onDefend() {
    }
}
