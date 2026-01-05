package com.example.controller;

import com.example.manager.MatchHistoryManager;
import com.example.model.*;
import com.example.view.ArenaView;
import com.example.view.PlayerSkillBar;
import com.example.view.SkillEffect;
import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    private static final double UI_TO_MODEL_SCALE = 50.0;

    // ✅ Trạng thái phòng thủ và heal trong turn hiện tại
    private boolean usedHealThisTurn = false;
    private boolean isDefending = false;

    public BattleController(ArenaView arena, String characterPath) {
        this.arena = arena;
        this.characterPath = characterPath;

        initializeGame();
        syncHealthBars();
        syncPositionsToModel();
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

        System.out.println("🎮 Game initialized");
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

        System.out.println("   [Sync] Distance: " + game.getDistance());
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
        System.out.println("\n⚔️ Player: Basic Attack (Turn " + currentTurn + ")");
        Hero hero = player.getHero();

        if (!game.isPlayerInRange()) {
            System.out.println("   ❌ Ngoài tầm đánh! Distance: " + game.getDistance() +
                    " | Range: " + hero.getAttackRange());
            showMessage("NGOÀI TẦM ĐÁNH!");
            return;
        }

        // ✅ KHÓA NGAY KHI BẮT ĐẦU HÀNH ĐỘNG
        skillBar.disableAllButtons();

        double startX = arena.getPlayerView().getLayoutX() + 50;
        double startY = arena.getPlayerView().getLayoutY() + 100;

        SkillEffect.castSkill(arena, startX, startY,
                "/img/skills/attack.png",
                "/img/explosion/explosion_thuong.png",
                120, () -> {
                    boolean success = hero.useSkill("Basic Attack", currentTurn, aiPlayer);

                    if (success) {
                        System.out.println("   ✅ Attack hit! AI HP: " + aiPlayer.getHp());
                        if (hero.isDefending()) {
                            System.out.println("   🛡️ Bạn đang phòng thủ - sẵn sàng chống đỡ!");
                        }

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

    // ✅ HEAL - KHÔNG KẾT THÚC LƯỢT
    public void onHeal() {
        System.out.println("\n💚 Player: Heal (Turn " + currentTurn + ")");

        if (usedHealThisTurn) {
            System.out.println("   ⚠️ Đã dùng Heal rồi trong lượt này!");
            showMessage("ĐÃ DÙNG HEAL!");
            return;
        }

        Hero hero = player.getHero();
        boolean success = hero.useSkill("Mana Regen", currentTurn, hero);

        if (success) {
            System.out.println("   ✅ Heal thành công! HP/MP đã hồi phục");
            System.out.println("   → Bạn vẫn có thể đánh tiếp!");

            usedHealThisTurn = true;
            syncHealthBars();
            updateCooldowns();

            skillBar.disableHealButton();

            showMessage("HEAL! (+10 HP, +15 MP)", Color.GREEN);

            // ✅ KHÔNG endPlayerTurn() - người chơi tiếp tục đánh!

        } else {
            System.out.println("   ❌ Không thể Heal (cooldown hoặc đã dùng)");
            showMessage("HEAL ĐANG COOLDOWN!");
        }
    }

    // ✅ DEFEND - KHÔNG KẾT THÚC LƯỢT
    public void onDefend() {
        System.out.println("\n🛡️ Player: Defend (Turn " + currentTurn + ")");

        Hero hero = player.getHero();

        if (hero.isDefending()) {
            System.out.println("   ⚠️ Đã bật Defend rồi!");
            showMessage("ĐÃ ĐANG PHÒNG THỦ!");
            return;
        }

        hero.setDefending(true);
        isDefending = true;

        System.out.println("   → Player kích hoạt tư thế phòng thủ!");
        System.out.println("   → Defense tăng gấp đôi!");
        System.out.println("   → Bạn vẫn có thể đánh tiếp!");

        showMessage("PHÒNG THỦ KÍCH HOẠT!", Color.PURPLE);

        skillBar.disableDefendButton();
    }

    private void executePlayerSkill(String skillName, String imagePath, String explosionPath, int explosionSize) {
        Hero hero = player.getHero();

        if (!game.isPlayerInRange()) {
            System.out.println("   ❌ Ngoài tầm đánh! Player range: " + hero.getAttackRange() +
                    " | Distance: " + game.getDistance());
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
            System.out.println("   ❌ Không thể dùng skill: " + skillName);
            return;
        }

        // ✅ KHÓA NGAY KHI BẮT ĐẦU HÀNH ĐỘNG
        skillBar.disableAllButtons();

        double startX = arena.getPlayerView().getLayoutX() + 50;
        double startY = arena.getPlayerView().getLayoutY() + 100;

        SkillEffect.castSkill(arena, startX, startY, imagePath, explosionPath, explosionSize, () -> {
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

        // ✅ Reset trạng thái hero
        player.getHero().resetDefense();

        // ✅ Reset flag controller
        usedHealThisTurn = false;
        isDefending = false;

        updateCooldowns();

        // ✅ KHÓA TẤT CẢ SKILL KHI ĐẾN LƯỢT AI
        skillBar.disableAllButtons();

        System.out.println("📍 Kết thúc lượt Player. Turn hiện tại: " + currentTurn);
        System.out.println("🔒 Đã khóa tất cả skill của Player - Đến lượt AI");

        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(e -> executeAITurn());
        delay.play();
    }

    private void executeAITurn() {
        System.out.println("\n🤖 AI Turn " + currentTurn);

        syncPositionsToModel();

        double distance = game.getDistance();
        System.out.println("   Khoảng cách: " + distance);
        System.out.println("   Trong tầm? " + game.isAIInRange());

        String action = aiPlayer.chooseBestAction(currentTurn, player.getHero(), game);

        System.out.println("   AI chọn: " + action);

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
            newX = currentX + 50;
            System.out.println("   → AI moving closer: " + currentX + " → " + newX);
        } else {
            newX = currentX - 50;
            System.out.println("   → AI moving away: " + currentX + " → " + newX);
        }

        aiPlayer.executeMovement(action, player.getHero());

        MovementController.moveTo(arena.getEnemyView(), newX, () -> {
            arena.getEnemyBar().setLayoutX(arena.getEnemyView().getLayoutX() + 70);
            syncPositionsToModel();
            endAITurn();
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

        boolean success = aiPlayer.useSkill(skillName, currentTurn, player.getHero());

        if (!success) {
            System.err.println("   ❌ AI không thể dùng skill: " + skillName);
            endAITurn();
            return;
        }

        System.out.println("   ✅ Skill executed! Player HP: " + player.getHero().getHp());

        if (player.getHero().isDefending()) {
            System.out.println("   🛡️ Player defense đã block một phần damage!");
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
            System.out.println("   [Animation] Skill animation hoàn thành");

            if (checkGameOver()) return;

            endAITurn();
        });
    }

    private void endAITurn() {
        currentTurn++;

        System.out.println("📍 Kết thúc lượt AI. Turn hiện tại: " + currentTurn);
        System.out.println("   Distance: " + game.getDistance());
        System.out.println("🔓 Mở khóa tất cả skill cho Player - Đến lượt Player");

        // ✅ MỞ KHÓA TẤT CẢ SKILL KHI ĐẾN LƯỢT PLAYER
        skillBar.enableAllButtons();
        updateCooldowns();
    }

    private String getEnemyImagePath() {
        // Lấy ImageView của enemy từ ArenaView
        ImageView enemyView = arena.getEnemyView();

        // Lấy URL của ảnh hiện tại
        Image image = enemyView.getImage();
        if (image != null) {
            String url = image.getUrl();

            // Chuyển từ file://.../xathu.png -> /img/character/xathu.png
            if (url != null && url.contains("/img/character/")) {
                int startIndex = url.indexOf("/img/character/");
                return url.substring(startIndex);
            }
        }

        // Fallback: dùng enemy name để đoán
        String enemyName = aiPlayer.getName().toLowerCase();
        if (enemyName.contains("fighter")) {
            return "/img/character/dausi_trai.png";
        } else if (enemyName.contains("mage")) {
            return "/img/character/phap_su_trai.png";
        } else if (enemyName.contains("marksman")) {
            return "/img/character/xathu.png";
        } else if (enemyName.contains("support")) {
            return "/img/character/trothu_trai.png";
        }

        return "/img/character/dausi_trai.png"; // Default
    }


    private boolean checkGameOver() {
        boolean gameOver = false;
        boolean playerWin = false;

        if (player.getHero().getHp() <= 0) {
            System.out.println("\n💀 GAME OVER - AI WINS!");
            skillBar.disableAllButtons();
            skillBar.showGameOver("YOU LOSE!");
            gameOver = true;
            playerWin = false;
        } else if (aiPlayer.getHp() <= 0) {
            System.out.println("\n🎉 GAME OVER - PLAYER WINS!");
            skillBar.disableAllButtons();
            skillBar.showGameOver("YOU WIN!");
            gameOver = true;
            playerWin = true;
        }

        // ✅ LƯU LỊCH SỬ KHI GAME OVER
        if (gameOver) {
            String enemyPath = getEnemyImagePath();
            MatchHistory match = new MatchHistory(
                    playerWin,
                    characterPath,
                    enemyPath,
                    LocalDateTime.now(),
                    player.getHero().getName(),
                    aiPlayer.getName(),
                    Math.max(0, player.getHero().getHp()),
                    Math.max(0, aiPlayer.getHp())
            );

            MatchHistoryManager.getInstance().addMatch(match);

            System.out.println("📜 Lịch sử đã được lưu: " +
                    (playerWin ? "THẮNG" : "THUA"));
        }

        return gameOver;
    }

    public void setSkillBar(PlayerSkillBar skillBar) {
        this.skillBar = skillBar;
    }
}