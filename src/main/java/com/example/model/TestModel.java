package com.example.model;

import java.util.List;
import java.util.Scanner;

public class TestModel {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== TURN-BASED FIGHTING GAME - AI SIÊU MẠNH ===\n");

        // Bắt đầu ở khoảng cách hợp lý để có không gian di chuyển
        AIPlayer ai = new AIPlayer("DEATH BOT", 100, 100, new Point(8, 0), 18, 8);  // Khoảng cách 8.0
// Hoặc Point(10, 0) nếu muốn đúng 10

        // Chọn tướng người chơi
        int choice = 0;
        while (choice < 1 || choice > 4) {
            System.out.println("Chọn tướng của bạn:");
            System.out.println("1. Fighter   (cân bằng, skill mạnh)");
            System.out.println("2. Marksman  (chí mạng kinh hồn)");
            System.out.println("3. Mage      (phép sát thương cao)");
            System.out.println("4. Support   (hồi máu, sống dai)");
            System.out.print("Nhập lựa chọn (1-4): ");
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
                if (choice < 1 || choice > 4) {
                    System.out.println("❌ Chỉ nhập từ 1 đến 4 thôi!\n");
                }
            } catch (Exception e) {
                System.out.println("❌ Phải nhập số!\n");
                choice = 0;
            }
        }

        Hero playerHero;
        switch (choice) {
            case 1 -> playerHero = new Fighter("Bạn", 100, 100, new Point(0, 0), 16, 7);
            case 2 -> playerHero = new Marksman("Bạn", 100, 100, new Point(0, 0), 22, 4);
            case 3 -> playerHero = new Mage("Bạn", 100, 100, new Point(0, 0), 12, 5);
            case 4 -> playerHero = new Support("Bạn", 100, 100, new Point(0, 0), 10, 12);
            default -> playerHero = new Fighter("Bạn", 100, 100, new Point(0, 0), 16, 7);
        }

        Player player = new Player(playerHero);
        Game game = new Game(player, ai);
        long currentTime = System.currentTimeMillis();

        System.out.println("\n=== TRẬN ĐẤU BẮT ĐẦU ===\n");
        printStatus(playerHero, ai);
        System.out.println("──────────────────────────────────");

        while (playerHero.getHp() > 0 && ai.getHp() > 0) {

            // === LƯỢT NGƯỜI CHƠI ===
            playerTurn(playerHero, ai, game, currentTime);
            currentTime += 1000;
            printStatus(playerHero, ai);
            System.out.println("──────────────────────────────────");

            if (ai.getHp() <= 0) break;

            // === LƯỢT AI ===
            System.out.println("\n--- Lượt của DEATH BOT ---");
            String aiAction = ai.chooseBestAction(currentTime, playerHero, game);

            if ("Move Closer".equals(aiAction)) {
                ai.getPosition().moveToward(playerHero.getPosition(), Point.MOVE_SPEED);
                System.out.println("🔥 DEATH BOT lao tới gần hơn!");
            } else {
                System.out.println("⚔️ AI dùng: " + aiAction);
            }

            currentTime += 1000;
            printStatus(playerHero, ai);
            System.out.println("──────────────────────────────────");
        }

        System.out.println("\n=== KẾT THÚC TRẬN ĐẤU ===");
        if (playerHero.getHp() <= 0) {
            System.out.println("💀 DEATH BOT ĐÃ TIÊU DIỆT BẠN!");
        } else {
            System.out.println("🏆 BẠN ĐÃ ĐÁNH BẠI DEATH BOT!!! (Huyền thoại!)");
        }
    }

    // LƯỢT NGƯỜI CHƠI - CÓ THỂ DI CHUYỂN HOẶC ĐÁNH
    // LƯỢT NGƯỜI CHƠI - MANA REGEN LÀ HÀNH ĐỘNG RIÊNG, CÓ THỂ COMBO VỚI ĐÁNH
    private static void playerTurn(Hero player, Hero ai, Game game, long time) {
        System.out.println("\n--- LƯỢT CỦA BẠN ---");
        System.out.println("HP: " + player.getHp() + " | MP: " + player.getMp());

        boolean inRange = game.isRange();
        if (inRange) {
            System.out.println("   ✅ Bạn đang trong tầm đánh!");
        } else {
            System.out.println("   ⚠️  Bạn đang ngoài tầm đánh!");
        }

        // Bước 1: Hỏi có muốn dùng Mana Regen trước không (luôn hỏi, kể cả trong tầm)
        Skill manaRegenSkill = player.getSkills().stream()
                .filter(s -> s.getName().equals("Mana Regen"))
                .findFirst()
                .orElse(null);

        boolean canRegen = manaRegenSkill != null && manaRegenSkill.canUse(time, player.getMp());

        System.out.println("\nBạn có muốn dùng Mana Regen trước không?");
        System.out.println(canRegen ? "0. Có (hồi 15 MP - vẫn được hành động tiếp)" : "0. Không thể (đang cooldown)");
        System.out.println("1. Không, bỏ qua hồi mana");

        int regenChoice;
        while (true) {
            System.out.print("Chọn (0/1): ");
            try {
                regenChoice = Integer.parseInt(sc.nextLine().trim());
                if (regenChoice == 0 || regenChoice == 1) break;
                System.out.println("❌ Chỉ chọn 0 hoặc 1!");
            } catch (Exception e) {
                System.out.println("❌ Nhập số đi!");
            }
        }

        if (regenChoice == 0 && canRegen) {
            player.useSkill("Mana Regen", time, player);
            System.out.println("💙 Bạn hồi 15 MP thành công! Giờ chọn hành động chính:");
            // Cập nhật lại MP hiển thị ở lượt tiếp
            System.out.println("MP hiện tại: " + player.getMp());
        } else if (regenChoice == 0) {
            System.out.println("⚠️ Mana Regen đang cooldown, bỏ qua.");
        }

        // Bước 2: Chọn hành động chính (di chuyển hoặc tấn công)
        System.out.println("\nChọn hành động chính:");
        System.out.println("8. Move Closer (tiến " + Point.MOVE_SPEED + " đơn vị)");
        System.out.println("9. Đứng yên (giữ vị trí)");
        System.out.println("10. Move Away (lùi xa " + Point.MOVE_SPEED + " đơn vị)");
        System.out.println("11. Jump Up (nhảy lùi xa gấp đôi + hồi 5 MP)");


        if (inRange) {
            System.out.println("1. Basic Attack");
            List<Skill> skills = player.getSkills();
            int num = 2;
            for (Skill s : skills) {
                if (!s.getName().equals("Basic Attack") && !s.getName().equals("Mana Regen")) {
                    String status = s.canUse(time, player.getMp()) ? "✅ OK" : "❌ CD/MP";
                    System.out.println(num + ". " + s.getName() + " " + status);
                    num++;
                }
            }
        }

        while (true) {
            System.out.print("Nhập lựa chọn: ");
            String input = sc.nextLine().trim();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("❌ Vui lòng nhập số!");
                continue;
            }

            // Di chuyển hoặc đứng yên
            if (choice == 8) {
                player.getPosition().moveToward(ai.getPosition(), Point.MOVE_SPEED);
                System.out.println("➡️ Bạn tiến gần đối thủ!");
                return;
            }
            if (choice == 9) {
                System.out.println("🛡️ Bạn đứng yên.");
                return;
            }
            if (choice == 10) {
                player.moveAway(ai, Point.MOVE_SPEED);
                System.out.println("⬅️ Bạn lùi xa đối thủ!");
                return;
            }
            if (choice == 11) {
                player.moveAway(ai, Point.MOVE_SPEED * 2);
                if (player.getMp() < 20) {
                    player.setMp(Math.min(100, player.getMp() + 5));
                    System.out.println("⬅️ Jump Up! Lùi xa + hồi 5 MP!");
                } else {
                    System.out.println("⬅️ Jump Up! Lùi xa!");
                }
                return;
            }

            // Chỉ được tấn công nếu trong tầm
            if (!inRange) {
                System.out.println("❌ Phải trong tầm mới tấn công được! Chọn 8 hoặc 9.");
                continue;
            }

            // Basic Attack
            if (choice == 1) {
                ai.takeDamage(player.getAttack());
                System.out.println("⚔️ Basic Attack gây " + player.getAttack() + " sát thương!");
                return;
            }


            // Skill đặc trưng
            if (choice >= 2) {
                int count = 2;
                boolean used = false;
                for (Skill s : player.getSkills()) {
                    if (!s.getName().equals("Basic Attack") && !s.getName().equals("Mana Regen")) {
                        if (count == choice) {
                            if (player.useSkill(s.getName(), time, ai)) {
                                System.out.println("🔥 " + s.getName() + " thành công!");
                            } else {
                                System.out.println("Skill thất bại → Basic Attack thay thế!");
                                ai.takeDamage(player.getAttack());
                            }
                            used = true;
                            break;
                        }
                        count++;
                    }
                }
                if (used) return;
            }

            System.out.println("❌ Lựa chọn không hợp lệ!");
        }
    }

    private static void printStatus(Hero player, Hero ai) {
        double distance = player.getPosition().distanceTo(ai.getPosition());
        String status = distance <= 2.0 ? "✅ TRONG TẦM ĐÁNH" : "⚠️ NGOÀI TẦM";
        System.out.printf("[Bạn] %-8s | HP: %3d | MP: %3d | Vị trí: %-10s\n",
                player.getName(), player.getHp(), player.getMp(), player.getPosition());
        System.out.printf("[AI]  DEATH BOT | HP: %3d | MP: %3d | Vị trí: %-10s\n",
                ai.getHp(), ai.getMp(), ai.getPosition());
        System.out.printf("→ Khoảng cách: %.2f  |  %s\n", distance, status);
    }
}