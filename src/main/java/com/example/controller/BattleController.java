package com.example.controller;

import com.example.model.*;
import com.example.view.ArenaView;
import com.example.view.PlayerSkillBar;
import com.example.view.SkillEffect;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class BattleController {
    private ArenaView arena;
    private PlayerSkillBar skillBar;

    // Model thực
    private Game game;
    private Player player;
    private AIPlayer aiPlayer;
    private int currentTurn = 1;

    // Character path để xác định Hero type
    private String characterPath;

    public BattleController(ArenaView arena, String characterPath) {
        this.arena = arena;
        this.characterPath = characterPath;

        initializeGame();
        syncHealthBars();
    }

    // =====================================================
    // KHỞI TẠO GAME
    // =====================================================
    private void initializeGame() {
        // Tạo vị trí ban đầu
        Point playerPos = new Point(8.0, 0.0);  // Bên phải
        Point aiPos = new Point(-8.0, 0.0);     // Bên trái

        // Tạo Hero cho Player dựa trên characterPath
        Hero playerHero = createHeroFromPath(characterPath, playerPos);
        player = new Player(playerHero);

        // Tạo AI (random hero)
        HeroType aiType = HeroType.values()[(int) (Math.random() * 4)];
        aiPlayer = new AIPlayer("AI", 100, 100, aiPos, 10, 5);

        // Khởi tạo Game
        game = new Game(player, aiPlayer);

        System.out.println("🎮 Game khởi tạo:");
        System.out.println("   Player: " + playerHero.getName() + " (HP:" + playerHero.getHp() + ", MP:" + playerHero.getMp() + ")");
        System.out.println("   AI: " + aiPlayer.getName() + " (HP:" + aiPlayer.getHp() + ", MP:" + aiPlayer.getMp() + ")");
    }

    /**
     * Tạo Hero từ character path
     */
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

    // =====================================================
    // SYNC UI VỚI MODEL
    // =====================================================
    private void syncHealthBars() {
        arena.getPlayerBar().syncWithHero(player.getHero());
        arena.getEnemyBar().syncWithHero(aiPlayer);
    }

    private void updateCooldowns() {
        Hero hero = player.getHero();

        // Tính cooldown còn lại (lastUsedTurn + cooldown - currentTurn)
        int cd1 = calculateRemainingCooldown(hero.getSkills().get(2)); // Skill thứ 3
        int cd2 = calculateRemainingCooldown(hero.getSkills().get(3)); // Skill thứ 4
        int cd3 = calculateRemainingCooldown(hero.getSkills().get(4)); // Skill thứ 5
        int cdHeal = calculateRemainingCooldown(hero.getSkills().get(1)); // Mana Regen

        skillBar.updateCooldowns(cd1, cd2, cd3, cdHeal, 0);
    }

    private int calculateRemainingCooldown(Skill skill) {
        int remaining = skill.getCooldownTurns() - (currentTurn - skill.getLastUsedTurn());
        return Math.max(0, remaining);
    }

    // =====================================================
    // PLAYER ACTIONS
    // =====================================================
    public void onAttack() {
        System.out.println("\n⚔️ Player: Basic Attack (Turn " + currentTurn + ")");
        executePlayerSkill("Basic Attack",
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
                120);
    }

    public void onSkillA1() {
        System.out.println("\n🔵 Player: Skill A1 (Turn " + currentTurn + ")");
        Hero hero = player.getHero();
        String skillName = hero.getSkills().get(2).getName(); // Skill thứ 3

        executePlayerSkill(skillName,
                "/img/skills/skill1.png",
                "/img/explosion/explosion_manh.png",
                150);
    }

    public void onSkillA2() {
        System.out.println("\n🟡 Player: Skill A2 (Turn " + currentTurn + ")");
        Hero hero = player.getHero();
        String skillName = hero.getSkills().get(3).getName(); // Skill thứ 4

        executePlayerSkill(skillName,
                "/img/skills/skill2.png",
                "/img/explosion/explosion_lon.png",
                180);
    }

    public void onSkillA3() {
        System.out.println("\n🟠 Player: Skill A3 (Turn " + currentTurn + ")");
        Hero hero = player.getHero();
        String skillName = hero.getSkills().get(4).getName(); // Skill thứ 5 (Ultimate)

        executePlayerSkill(skillName,
                "/img/skills/skill3.png",
                "/img/explosion/explosion_cuc_manh.png",
                200);
    }

    public void onHeal() {
        System.out.println("\n💚 Player: Heal (Turn " + currentTurn + ")");
        Hero hero = player.getHero();

        boolean success = hero.useSkill("Mana Regen", currentTurn, hero);

        if (success) {
            System.out.println("   ✅ Heal thành công!");
            syncHealthBars();
            updateCooldowns();
            endPlayerTurn();
        } else {
            System.out.println("   ❌ Không thể Heal (không đủ MP hoặc đang cooldown)");
        }
    }

    public void onDefend() {
        System.out.println("\n🛡️ Player: Defend (Turn " + currentTurn + ")");
        // Defend chỉ skip turn (tăng defense tạm thời nếu muốn)
        System.out.println("   → Player phòng thủ, skip turn");
        endPlayerTurn();
    }

    /**
     * Thực hiện skill của Player
     */
    private void executePlayerSkill(String skillName, String imagePath, String explosionPath, int explosionSize) {
        Hero hero = player.getHero();

        // Kiểm tra có thể dùng skill không
        boolean canUse = false;
        for (Skill skill : hero.getSkills()) {
            if (skill.getName().equals(skillName) && skill.canUse(currentTurn, hero.getMp())) {
                canUse = true;
                break;
            }
        }

        if (!canUse) {
            System.out.println("   ❌ Không thể dùng skill (không đủ MP hoặc đang cooldown)");
            return;
        }

        // Disable buttons ngay
        skillBar.disableAllButtons();

        // Animation
        double startX = arena.getPlayerView().getLayoutX() + 50;
        double startY = arena.getPlayerView().getLayoutY() + 100;

        SkillEffect.castSkill(arena, startX, startY, imagePath, explosionPath, explosionSize, () -> {
            // Callback sau khi animation chạm mục tiêu

            // Thực hiện damage trong Model
            boolean success = hero.useSkill(skillName, currentTurn, aiPlayer);

            if (success) {
                System.out.println("   ✅ Skill hit! AI HP: " + aiPlayer.getHp() + " | MP: " + aiPlayer.getMp());

                // Sync UI
                syncHealthBars();

                // Kiểm tra game over
                if (checkGameOver()) return;

                // Kết thúc lượt player
                endPlayerTurn();
            }
        });
    }

    // =====================================================
    // TURN MANAGEMENT
    // =====================================================
    private void endPlayerTurn() {
        currentTurn++;
        updateCooldowns();

        System.out.println("📍 Kết thúc lượt Player. Turn hiện tại: " + currentTurn);

        // Delay 1 giây rồi chuyển sang AI
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(e -> executeAITurn());
        delay.play();
    }

    private void executeAITurn() {
        System.out.println("\n🤖 AI Turn " + currentTurn);

        // Gọi AI Minimax
        String action = aiPlayer.chooseBestAction(currentTurn, player.getHero(), game);

        System.out.println("   AI chọn: " + action);

        // Xử lý action của AI
        if (action.contains("Move")) {
            handleAIMovement(action);
        } else if (action.equals("Jump Up")) {
            handleAIJumpUp();
        } else {
            handleAISkill(action);
        }
    }

    private void handleAIMovement(String action) {
        // Di chuyển AI
        double currentX = arena.getEnemyView().getLayoutX();
        double newX;

        if (action.equals("Move Closer")) {
            newX = currentX + 50; // Tiến gần
        } else {
            newX = currentX - 50; // Lùi xa
        }

        MovementController.moveTo(arena.getEnemyView(), newX, () -> {
            arena.getEnemyBar().setLayoutX(arena.getEnemyView().getLayoutX() + 70);
            endAITurn();
        });
    }

    private void handleAIJumpUp() {
        // Lùi xa x2
        double currentX = arena.getEnemyView().getLayoutX();
        double newX = currentX - 100;

        MovementController.moveTo(arena.getEnemyView(), newX, () -> {
            arena.getEnemyBar().setLayoutX(arena.getEnemyView().getLayoutX() + 70);
            syncHealthBars(); // AI có thể regen MP
            endAITurn();
        });
    }

    private void handleAISkill(String skillName) {
        // Animation AI skill
        double startX = arena.getEnemyView().getLayoutX() + 50;
        double startY = arena.getEnemyView().getLayoutY() + 100;

        String imagePath = "/img/skills/attack.png";
        String explosionPath = "/img/explosion/explosion_thuong.png";
        int explosionSize = 120;

        // Phân biệt skill để chọn effect
        if (skillName.contains("Ultimate") || skillName.contains("Deadly") || skillName.contains("Meteor")) {
            imagePath = "/img/skills/skill3.png";
            explosionPath = "/img/explosion/explosion_cuc_manh.png";
            explosionSize = 200;
        } else if (skillName.contains("Burst") || skillName.contains("Lightning") || skillName.contains("Snipe")) {
            imagePath = "/img/skills/skill2.png";
            explosionPath = "/img/explosion/explosion_lon.png";
            explosionSize = 180;
        } else if (!skillName.equals("Basic Attack")) {
            imagePath = "/img/skills/skill1.png";
            explosionPath = "/img/explosion/explosion_manh.png";
            explosionSize = 150;
        }

        // ✅ Tạo final reference
        final String finalImagePath = imagePath;
        final String finalExplosionPath = explosionPath;
        final int finalExplosionSize = explosionSize;

        SkillEffect.castSkillAI(arena, startX, startY, imagePath, explosionPath, explosionSize, new Runnable() {
            @Override
            public void run() {
                syncHealthBars();
                System.out.println("   ✅ AI skill hit! Player HP: " + player.getHero().getHp());
                if (checkGameOver()) return;
                endAITurn();
            }
        });
    }

    private void endAITurn() {
        currentTurn++;

        System.out.println("📍 Kết thúc lượt AI. Turn hiện tại: " + currentTurn);
        System.out.println("   Distance: " + game.getDistance());

        // Enable buttons cho player
        skillBar.enableAllButtons();
        updateCooldowns();
    }

    // =====================================================
    // GAME OVER
    // =====================================================
    private boolean checkGameOver() {
        if (player.getHero().getHp() <= 0) {
            System.out.println("\n💀 GAME OVER - AI WINS!");
            skillBar.disableAllButtons();
            skillBar.showGameOver("YOU LOSE!");
            return true;
        }

        if (aiPlayer.getHp() <= 0) {
            System.out.println("\n🎉 GAME OVER - PLAYER WINS!");
            skillBar.disableAllButtons();
            skillBar.showGameOver("YOU WIN!");
            return true;
        }

        return false;
    }

    public void setSkillBar(PlayerSkillBar skillBar) {
        this.skillBar = skillBar;
    }
}