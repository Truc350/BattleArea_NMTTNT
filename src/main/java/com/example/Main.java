// File: src/com/example/model/Main.java
package com.example;

import com.example.model.AIPlayer;
import com.example.model.Game;
import com.example.model.Player;

public class Main {
    public static void main(String[] args) {
        Player player = new Player(null);
        player.selectPlayerHero(0.0); // Người chơi chọn tướng

        AIPlayer ai = new AIPlayer("AI GOD", 100, 100, 1.0, 22, 12);
        Game game = new Game(player, ai);
        game.start();

        long currentTime = System.currentTimeMillis();

        while (player.getHero().getHp() > 0 && ai.getHp() > 0) {
            System.out.println("\n--- LƯỢT MỚI ---");
            System.out.println("AI: " + ai.getHp() + "/" + ai.getMaxHP() + " HP | " + ai.getMp() + " MP");
            System.out.println("Bạn: " + player.getHero().getHp() + "/" + player.getHero().getMaxHP() + " HP | " + player.getHero().getMp() + " MP");

            if (game.isRange()) {
                String aiMove = ai.chooseBestAction(currentTime, player.getHero(), game);
                System.out.println("[AI] " + aiMove);

                // Người chơi phản công
                System.out.print("Bạn dùng skill (hoặc 'basic'): ");
                String playerMove = new java.util.Scanner(System.in).nextLine();
                if (playerMove.isEmpty() || playerMove.equalsIgnoreCase("basic")) {
                    ai.takeDamage(player.getHero().getAttack());
                } else {
                    player.getHero().useSkill(playerMove, currentTime, ai);
                }
            } else {
                System.out.println("[AI] Đang di chuyển lại gần...");
            }

            currentTime += 1000;
            try { Thread.sleep(800); } catch (Exception e) {}
        }

        System.out.println("\n=== KẾT THÚC TRẬN ĐẤU ===");
        if (ai.getHp() <= 0) System.out.println("BẠN THẮNG!");
        else System.out.println("AI THẮNG!");
    }
}