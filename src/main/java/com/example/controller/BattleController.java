package com.example.controller;

import com.example.model.*;
import com.example.view.ArenaView;
import com.example.view.PlayerSkillBar;
import com.example.view.SkillEffect;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
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

    // ✅ UI to Model scaling factor
    private static final double UI_TO_MODEL_SCALE = 50.0; // 50 pixels = 1 unit trong model

    public BattleController(ArenaView arena, String characterPath) {
        this.arena = arena;
        this.characterPath = characterPath;

        initializeGame();
        syncHealthBars();

        // ✅ Sync position ban đầu
        syncPositionsToModel();
    }

    // =====================================================
    // KHỞI TẠO GAME
    // =====================================================
    private void initializeGame() {
        double playerUIX = 960.0;
        double aiUIX = 120.0;

        Point playerPos = new Point(playerUIX / UI_TO_MODEL_SCALE, 0.0);
        Point aiPos = new Point(aiUIX / UI_TO_MODEL_SCALE, 0.0);

        // Tạo Hero cho Player
        Hero playerHero = createHeroFromPath(characterPath, playerPos);
        player = new Player(playerHero);

        // ✅ Tạo AI hero đúng cách
        HeroType aiType = HeroType.values()[(int)(Math.random() * 4)];
        Hero aiHeroTemplate = Hero.getHero(aiType, "AI", aiPos);

        // ✅ Tạo AIPlayer với stats từ template
        aiPlayer = new AIPlayer(
                aiHeroTemplate.getName(),
                aiHeroTemplate.getMaxHP(),
                aiHeroTemplate.getMaxMP(),
                aiPos,
                aiHeroTemplate.getAttack(),    // ← Dùng attack từ hero type
                aiHeroTemplate.getDefense()    // ← Dùng defense từ hero type
        );

        // ✅ Copy skills từ template sang AIPlayer
        aiPlayer.getSkills().clear();
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

        System.out.println("🎮 Game khởi tạo:");
        System.out.println("   Player: " + playerHero.getName() +
                " (Type: " + getHeroTypeName(playerHero) + ")" +
                " HP:" + playerHero.getHp() +
                ", MP:" + playerHero.getMp() +
                ", ATK:" + playerHero.getAttack() +
                ", DEF:" + playerHero.getDefense() + ")");
        System.out.println("   AI: " + aiPlayer.getName() +
                " (Type: " + aiType + ")" +
                " HP:" + aiPlayer.getHp() +
                ", MP:" + aiPlayer.getMp() +
                ", ATK:" + aiPlayer.getAttack() +
                ", DEF:" + aiPlayer.getDefense() + ")");
        System.out.println("   Player Position: " + playerPos);
        System.out.println("   AI Position: " + aiPos);
        System.out.println("   Initial Distance: " + game.getDistance());

        // ✅ In ra skills của AI
        System.out.println("   AI Skills:");
        for (Skill s : aiPlayer.getSkills()) {
            System.out.println("     - " + s.getName() + " (DMG:" + s.getDamage() + ", MP:" + s.getMpCost() + ")");
        }
    }

    // Helper method
    private String getHeroTypeName(Hero hero) {
        if (hero instanceof Fighter) return "Fighter";
        if (hero instanceof Marksman) return "Marksman";
        if (hero instanceof Mage) return "Mage";
        if (hero instanceof Support) return "Support";
        return "Unknown";
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

    // =====================================================
    // SYNC UI VỚI MODEL
    // =====================================================
    private void syncHealthBars() {
        arena.getPlayerBar().syncWithHero(player.getHero());
        arena.getEnemyBar().syncWithHero(aiPlayer);
    }

    /**
     * ✅ Sync UI positions sang Model positions
     */
    private void syncPositionsToModel() {
        double playerUIX = arena.getPlayerView().getLayoutX();
        double aiUIX = arena.getEnemyView().getLayoutX();

        double playerModelX = playerUIX / UI_TO_MODEL_SCALE;
        double aiModelX = aiUIX / UI_TO_MODEL_SCALE;

        player.getHero().setPosition(new Point(playerModelX, 0.0));
        aiPlayer.setPosition(new Point(aiModelX, 0.0));

        System.out.println("   [Sync] Player UI X=" + playerUIX + " → Model X=" + playerModelX);
        System.out.println("   [Sync] AI UI X=" + aiUIX + " → Model X=" + aiModelX);
        System.out.println("   [Sync] Distance: " + game.getDistance());
    }

    private void updateCooldowns() {
        Hero hero = player.getHero();

        // Tính cooldown còn lại với null check
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
        System.out.println("\n⚔️ Player: Basic Attack (Turn " + currentTurn + ")");
        Hero hero = player.getHero();
        if (!game.isPlayerInRange()) {
            System.out.println("   ❌ Ngoài tầm đánh! Distance: " + game.getDistance() +
                    " | Range: " + hero.getAttackRange());
            showMessage("NGOÀI TẦM ĐÁNH!");
            return;
        }

        // Basic Attack luôn có thể dùng
        skillBar.disableAllButtons();

        double startX = arena.getPlayerView().getLayoutX() + 50;
        double startY = arena.getPlayerView().getLayoutY() + 100;

        SkillEffect.castSkill(arena, startX, startY,
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
                120, () -> {
                    // Callback sau animation

                    boolean success = hero.useSkill("Basic Attack", currentTurn, aiPlayer);

                    if (success) {
                        System.out.println("   ✅ Attack hit! AI HP: " + aiPlayer.getHp());
                        syncHealthBars();

                        if (checkGameOver()) return;

                        endPlayerTurn();
                    } else {
                        System.err.println("   ❌ Attack failed!");
                        skillBar.enableAllButtons();
                    }
                });
    }

    public void onSkillA1() {
        System.out.println("\n🔵 Player: Skill A1 (Turn " + currentTurn + ")");
        Hero hero = player.getHero();

        if (hero.getSkills().size() <= 2) {
            System.err.println("   ❌ Skill A1 not available!");
            return;
        }

        String skillName = hero.getSkills().get(2).getName();

        executePlayerSkill(skillName,
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
                150);
    }

    public void onSkillA2() {
        System.out.println("\n🟡 Player: Skill A2 (Turn " + currentTurn + ")");
        Hero hero = player.getHero();

        if (hero.getSkills().size() <= 3) {
            System.err.println("   ❌ Skill A2 not available!");
            return;
        }

        String skillName = hero.getSkills().get(3).getName();

        executePlayerSkill(skillName,
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
                180);
    }

    public void onSkillA3() {
        System.out.println("\n🟠 Player: Skill A3 (Turn " + currentTurn + ")");
        Hero hero = player.getHero();

        if (hero.getSkills().size() <= 4) {
            System.err.println("   ❌ Skill A3 not available!");
            return;
        }

        String skillName = hero.getSkills().get(4).getName();

        executePlayerSkill(skillName,
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
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
        System.out.println("   → Player phòng thủ, skip turn");
        endPlayerTurn();
    }

    private void executePlayerSkill(String skillName, String imagePath, String explosionPath, int explosionSize) {
        Hero hero = player.getHero();

        if (!game.isPlayerInRange()) {
            System.out.println("   ❌ Ngoài tầm đánh! Player range: " + hero.getAttackRange() +
                    " | Distance: " + game.getDistance());
            skillBar.enableAllButtons();
            updateCooldowns();

            // Hiển thị thông báo cho user
            showMessage("NGOÀI TẦM ĐÁNH!");
            return;
        }

        // Kiểm tra có thể dùng skill không
        boolean canUse = false;
        for (Skill skill : hero.getSkills()) {
            if (skill.getName().equals(skillName) && skill.canUse(currentTurn, hero.getMp())) {
                canUse = true;
                break;
            }
        }

        if (!canUse) {
            System.out.println("   ❌ Không thể dùng skill: " + skillName);
            skillBar.enableAllButtons();
            updateCooldowns();
            return;
        }

        // Disable buttons ngay
        skillBar.disableAllButtons();

        double startX = arena.getPlayerView().getLayoutX() + 50;
        double startY = arena.getPlayerView().getLayoutY() + 100;

        SkillEffect.castSkill(arena, startX, startY, imagePath, explosionPath, explosionSize, () -> {
            // Callback sau animation
            System.out.println("   [BEFORE] AI HP: " + aiPlayer.getHp() + " | Player MP: " + hero.getMp());
            boolean success = hero.useSkill(skillName, currentTurn, aiPlayer);

            if (success) {
                System.out.println("   [AFTER] AI HP: " + aiPlayer.getHp() + " | Player MP: " + hero.getMp());
                syncHealthBars();

                if (checkGameOver()) return;

                endPlayerTurn();
            } else {
                System.err.println("   ❌ Skill failed!");
                skillBar.enableAllButtons();
                updateCooldowns();
            }
        });
    }

    private void showMessage(String text) {
        Label msg = new Label(text);
        msg.setStyle("-fx-font-size: 24px; -fx-text-fill: red; -fx-font-weight: bold;");
        msg.setLayoutX(450);
        msg.setLayoutY(200);
        arena.getChildren().add(msg);

        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(e -> arena.getChildren().remove(msg));
        delay.play();
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

        // ✅ Sync positions TRƯỚC KHI AI quyết định
        syncPositionsToModel();

        // Kiểm tra distance
        double distance = game.getDistance();
        System.out.println("   Khoảng cách: " + distance);
        System.out.println("   Tầm đánh: 6.0");
        System.out.println("   Trong tầm? " + game.isRange());

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
        double currentX = arena.getEnemyView().getLayoutX();
        double newX;

        if (action.equals("Move Closer")) {
            newX = currentX + 50; // Tiến gần
            System.out.println("   → AI moving closer: " + currentX + " → " + newX);
        } else {
            newX = currentX - 50; // Lùi xa
            System.out.println("   → AI moving away: " + currentX + " → " + newX);
        }

        // ✅ Execute movement trong model
        aiPlayer.executeMovement(action, player.getHero());

        MovementController.moveTo(arena.getEnemyView(), newX, () -> {
            arena.getEnemyBar().setLayoutX(arena.getEnemyView().getLayoutX() + 70);

            // Sync position sau khi di chuyển
            syncPositionsToModel();

            endAITurn();
        });
    }

    private void handleAIJumpUp() {
        double currentX = arena.getEnemyView().getLayoutX();
        double newX = currentX - 100;

        // ✅ Execute jump trong model
        aiPlayer.executeMovement("Jump Up", player.getHero());

        MovementController.moveTo(arena.getEnemyView(), newX, () -> {
            arena.getEnemyBar().setLayoutX(arena.getEnemyView().getLayoutX() + 70);
            syncHealthBars(); // AI có thể regen MP

            // Sync position sau khi di chuyển
            syncPositionsToModel();

            endAITurn();
        });
    }

    private void handleAISkill(String skillName) {
        System.out.println("   → AI đang thực hiện skill: " + skillName);
        if (!game.isAIInRange()) {
            System.err.println("   ❌ AI ngoài tầm! Distance: " + game.getDistance());
            endAITurn();
            return;
        }
        // ✅ EXECUTE SKILL THẬT TRƯỚC KHI CHẠY ANIMATION
        boolean success = aiPlayer.useSkill(skillName, currentTurn, player.getHero());

        if (!success) {
            System.err.println("   ❌ AI không thể dùng skill: " + skillName);
            endAITurn();
            return;
        }


        System.out.println("   ✅ Skill executed! Player HP: " + player.getHero().getHp() + " | AI MP: " + aiPlayer.getMp());

        // Sync UI ngay sau khi damage
        syncHealthBars();

        // Animation skill bay
        double startX = arena.getEnemyView().getLayoutX() + 50;
        double startY = arena.getEnemyView().getLayoutY() + 100;

        String imagePath = "/img/skills/attack.png";
        String explosionPath = "/img/explosion/explosion_thuong.png";
        int explosionSize = 120;

        // Phân biệt skill để chọn effect
        if (skillName.contains("Ultimate") || skillName.contains("Deadly") || skillName.contains("Meteor")) {
            explosionSize = 200;
        } else if (skillName.contains("Burst") || skillName.contains("Lightning") || skillName.contains("Snipe")) {
            explosionSize = 180;
        } else if (!skillName.equals("Basic Attack")) {
            explosionSize = 150;
        }

        SkillEffect.castSkillAI(arena, startX, startY, imagePath, explosionPath, explosionSize, () -> {
            // Callback sau animation - chỉ kiểm tra game over
            System.out.println("   [Animation] Skill animation hoàn thành");

            if (checkGameOver()) return;

            endAITurn();
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

    // =====================================================
    // SETTER
    // =====================================================
    public void setSkillBar(PlayerSkillBar skillBar) {
        this.skillBar = skillBar;
    }
}