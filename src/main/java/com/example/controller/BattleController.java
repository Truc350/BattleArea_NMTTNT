package com.example.controller;

import com.example.model.*;
import com.example.view.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
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

    // ===== COOLDOWN SYSTEM (đếm theo lượt) =====
    private int a1Cooldown = 0;  // A1: 2 lượt
    private int a2Cooldown = 0;  // A2: 3 lượt
    private int a3Cooldown = 0;  // A3: 4 lượt
    private int healCooldown = 0; // Heal: 3 lượt
    private int defCooldown = 0;  // Def: 3 lượt

    public BattleController(ArenaView arenaView, String characterPath) {
        this.arenaView = arenaView;

        playerHero = createPlayerHero(characterPath);
        enemyHero = createRandomEnemy();

        Player playerWrapper = new Player(playerHero);
        game = new Game(playerWrapper, (AIPlayer) enemyHero);

        currentTime = System.currentTimeMillis();

       forceUpdateHealthBars();

        arenaView.startPlayerTurn();
    }


    public void setSkillBar(PlayerSkillBar skillBar) {
        this.skillBar = skillBar;
        System.out.println("✅ SkillBar connected!");
    }

    // ===================== HERO CREATE =====================

    private Hero createPlayerHero(String path) {

        Point pos = new Point(1000, 280);
        Hero hero;
        if (path.contains("dausi")) hero = new Fighter("Bạn", 100, 100, pos, 16, 7);
        else if (path.contains("xathu")) hero = new Marksman("Bạn", 100, 100, pos, 22, 4);
        else if (path.contains("phapsu")) hero = new Mage("Bạn", 100, 100, pos, 12, 5);
        else if (path.contains("trothu")) hero = new Support("Bạn", 100, 100, pos, 10, 12);
        else hero = new Fighter("Bạn", 100, 100, pos, 16, 7);
        System.out.println("✅ Player: " + hero.getClass().getSimpleName());
        return hero;
    }

    private Hero createRandomEnemy() {
        Point pos = new Point(200, 280);
        int rand = random.nextInt(4);
        String name = "DEATH BOT ";
        return switch (rand) {
            case 0 -> new AIPlayer(name + "Fighter", 100, 100, pos, 16, 7);
            case 1 -> new AIPlayer(name + "Marksman", 100, 100, pos, 22, 4);
            case 2 -> new AIPlayer(name + "Mage", 100, 100, pos, 12, 5);
            case 3 -> new AIPlayer(name + "Support", 100, 100, pos, 10, 12);
            default -> new AIPlayer(name, 100, 100, pos, 18, 8);
        };
    }

    private void forceUpdateHealthBars() {
        int playerHp = playerHero.getHp();
        int playerMp = playerHero.getMp();
        int enemyHp = enemyHero.getHp();
        int enemyMp = enemyHero.getMp();

        arenaView.getPlayerBar().setHp(playerHp);
        arenaView.getPlayerBar().setMp(playerMp);
        arenaView.getEnemyBar().setHp(enemyHp);
        arenaView.getEnemyBar().setMp(enemyMp);
    }

    // ===================== PLAYER ACTION =====================

    public void onAttack() {
        if (!arenaView.isPlayerTurn()) return;

        int damage = playerHero.getAttack();
        enemyHero.takeDamage(damage);
        forceUpdateHealthBars();

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
        if (!arenaView.isPlayerTurn() || arenaView.isGameOver()) return;
        if (a1Cooldown > 0) {
            return;
        }
        int mpCost = 10;
        if (playerHero.getMp() < mpCost) {
            return;
        }

        castSkill(20, mpCost, "/img/attackEffect/chieu2.png", 120);
        a1Cooldown = 4;  // Cooldown 2 lượt

        updateSkillBarCooldown();
    }

    public void onSkillA2() {
        if (!arenaView.isPlayerTurn() || arenaView.isGameOver()) return;
        if (a2Cooldown > 0) {
            return;
        }

        int mpCost = 15;
        if (playerHero.getMp() < mpCost) {
            return;
        }

        castSkill(30, mpCost, "/img/attackEffect/chieu4.png", 140);
        a2Cooldown = 6;
        updateSkillBarCooldown();
    }

    public void onSkillA3() {
        if (!arenaView.isPlayerTurn() || arenaView.isGameOver()) return;
        if (a3Cooldown > 0) {
            return;
        }

        int mpCost = 25;
        if (playerHero.getMp() < mpCost) {
            return;
        }

        castSkill(45, mpCost, "/img/attackEffect/chieu4.png", 160);
        a3Cooldown = 8;
        updateSkillBarCooldown();
    }


    private void castSkill(int damage, int mpCost, String effectPath, int explosionSize) {
        if (!arenaView.isPlayerTurn()) return;

        playerHero.setMp(playerHero.getMp() - mpCost);
        enemyHero.takeDamage(damage);

        forceUpdateHealthBars();

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
        if (!arenaView.isPlayerTurn() || arenaView.isGameOver()) return;

        if (healCooldown > 0) {
            return;
        }

        playerHero.setHp(Math.min(100, playerHero.getHp() + 35));

        forceUpdateHealthBars();

        healCooldown = 6;  // ✅ 3 lượt = 6 nửa lượt
        updateSkillBarCooldown();
        endPlayerTurn();
    }

    public void onDefend() {
        if (!arenaView.isPlayerTurn() || arenaView.isGameOver()) return;

        if (defCooldown > 0) {
            return;
        }
        defCooldown = 6;  // ✅ 3 lượt = 6 nửa lượt
        updateSkillBarCooldown();

        endPlayerTurn();
    }

    // ===================== TURN & AI =====================

    private void endPlayerTurn() {
        decreaseCooldowns();

        currentTime += 1000;
        checkGameOver();
        if (arenaView.isGameOver()) return;

        arenaView.endPlayerTurn();

        PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
        delay.setOnFinished(e -> aiRandomAttack());
        delay.play();
    }

    private void aiRandomAttack() {
        if (arenaView.isGameOver()) return;
        // ===== AI quyết định di chuyển (giới hạn khoảng cách ngắn) =====
        double currentX = arenaView.getEnemyView().getLayoutX();
        double newX = random.nextBoolean() ? currentX + 80 : currentX - 80;
        int damage, mpCost = 0;

        // ===== AI quyết định di chuyển trước khi đánh (50% tiến gần, 50% lùi xa) =====
        if (random.nextBoolean() && enemyHero.getMp() >= 15) {
            // Tiến gần player
            mpCost = 15;
            damage = enemyHero.getAttack() + 30;
            enemyHero.setMp(enemyHero.getMp() - mpCost);
        } else {
            // Lùi xa player (nhưng không ra khỏi màn hình)
            damage = enemyHero.getAttack() + random.nextInt(10, 20);
        }

        playerHero.takeDamage(damage);
        forceUpdateHealthBars();

        // ===== Di chuyển với giới hạn =====
        MovementController.moveTo(
                arenaView.getEnemyView(),
                newX,
                () -> {
                    // Cập nhật thanh máu sau khi di chuyển xong
                    arenaView.getEnemyBar().setLayoutX(arenaView.getEnemyView().getLayoutX() + 70);
                    arenaView.getEnemyBar().setLayoutY(arenaView.getEnemyView().getLayoutY() - 80);
                }, false
        );

        // ===== Animation skill AI bay từ vị trí mới (sau khi di chuyển xong) =====
        SkillEffect.castSkillAI(
                arenaView,
                arenaView.getEnemyView().getLayoutX() + 200,  // vị trí bắt đầu từ enemy mới
                arenaView.getEnemyView().getLayoutY() + 60,
                "/img/attackEffect/chieu2.png",
                damage,
                "/img/explosion/explosion_thuong.png",
                120,
                () -> {
                    decreaseCooldowns();

                    checkGameOver();
                    if (!arenaView.isGameOver()) {
                        arenaView.startPlayerTurn();
                    }
                }
        );
    }

    private void decreaseCooldowns() {
        if (a1Cooldown > 0) a1Cooldown--;
        if (a2Cooldown > 0) a2Cooldown--;
        if (a3Cooldown > 0) a3Cooldown--;
        if (healCooldown > 0) healCooldown--;
        if (defCooldown > 0) defCooldown--;

        updateSkillBarCooldown();
    }

    private void updateSkillBarCooldown() {
        if (skillBar != null) {
            int cd1 = (a1Cooldown + 1) / 2;
            int cd2 = (a2Cooldown + 1) / 2;
            int cd3 = (a3Cooldown + 1) / 2;
            int cdHeal = (healCooldown + 1) / 2;
            int cdDef = (defCooldown + 1) / 2;

            skillBar.updateCooldowns(cd1, cd2, cd3, cdHeal, cdDef);
        }
    }

    private void checkGameOver() {
        if (playerHero.getHp() <= 0) {
            System.out.println("💀 GAME OVER - Player died!");
            arenaView.setGameOver(true);

            if (skillBar != null) {
                skillBar.disableAllButtons();
                skillBar.showGameOver("YOU LOSE!");
            }
//            GameController.getInstance().onGameOver(false);

        } else if (enemyHero.getHp() <= 0) {
            System.out.println("🎉 GAME OVER - Enemy died!");
            arenaView.setGameOver(true);
            // ✅ Hiển thị YOU WIN
            if (skillBar != null) {
                skillBar.disableAllButtons();  // ← Disable tất cả nút
                skillBar.showGameOver("YOU WIN!");
            }
//            GameController.getInstance().onGameOver(true);
        }
    }


    public void onMoveCloser() {
        if (!arenaView.isPlayerTurn()) return;

        double currentX = arenaView.getPlayerView().getLayoutX();
        double newX = currentX - 80; // Tiến gần AI

        MovementController.moveTo(
                arenaView.getPlayerView(),
                newX,
                () -> {
                    arenaView.getPlayerBar().setLayoutX(arenaView.getPlayerView().getLayoutX() + 70);
                    arenaView.getPlayerBar().setLayoutY(arenaView.getPlayerView().getLayoutY() - 80);
                }, true
        );

        endPlayerTurn();
    }

    public void onMoveAway() {
        if (!arenaView.isPlayerTurn()|| arenaView.isGameOver()) return;

        double currentX = arenaView.getPlayerView().getLayoutX();
        double newX = currentX + 80; // Lùi xa AI

        MovementController.moveTo(
                arenaView.getPlayerView(),
                newX,
                () -> {
                    arenaView.getPlayerBar().setLayoutX(arenaView.getPlayerView().getLayoutX() + 70);
                    arenaView.getPlayerBar().setLayoutY(arenaView.getPlayerView().getLayoutY() - 80);
                },
                true  // ← Player
        );

        endPlayerTurn();
    }

    public void onJumpUp() {
        if (!arenaView.isPlayerTurn() || arenaView.isGameOver()) return;

        double currentX = arenaView.getPlayerView().getLayoutX();
        double newX = currentX + 150; // Nhảy xa hơn

        MovementController.moveTo(
                arenaView.getPlayerView(),
                newX,
                () -> {
                    arenaView.getPlayerBar().setLayoutX(arenaView.getPlayerView().getLayoutX() + 70);
                    arenaView.getPlayerBar().setLayoutY(arenaView.getPlayerView().getLayoutY() - 80);
                }, true
        );

        if (playerHero.getMp() < 30) {
            playerHero.setMp(Math.min(100, playerHero.getMp() + 10));
            forceUpdateHealthBars();
        }

        endPlayerTurn();
    }
    private void syncPositionsToView() {
        double scale = 80.0;  // Tỷ lệ model → pixel (tùy chỉnh nếu cần)
        double offsetX = 600; // Căn giữa màn hình

        // Player
        MovementController.moveTo(
                arenaView.getPlayerView(),
                playerHero.getPosition().getX() * scale + offsetX,
                () -> updateHealthBarPositions()
        );

        // Enemy
        MovementController.moveTo(
                arenaView.getEnemyView(),
                enemyHero.getPosition().getX() * scale + offsetX,
                () -> updateHealthBarPositions()
        );
    }
    private void updateHealthBarPositions() {
        HealthBar playerBar = arenaView.getPlayerBar();
        HealthBar enemyBar = arenaView.getEnemyBar();

        playerBar.setLayoutX(arenaView.getPlayerView().getLayoutX() + 70);
        playerBar.setLayoutY(arenaView.getPlayerView().getLayoutY() - 80);

        enemyBar.setLayoutX(arenaView.getEnemyView().getLayoutX() + 70);
        enemyBar.setLayoutY(arenaView.getEnemyView().getLayoutY() - 80);
    }

    // Getter cho skillBar để kiểm tra cooldown
    public boolean isA1Ready() {
        return a1Cooldown == 0;
    }

    public boolean isA2Ready() {
        return a2Cooldown == 0;
    }

    public boolean isA3Ready() {
        return a3Cooldown == 0;
    }
}
