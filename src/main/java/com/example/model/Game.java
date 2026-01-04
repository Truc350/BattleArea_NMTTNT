package com.example.model;

public class Game {
    private Player player;
    private AIPlayer aiPlayer;
    private double distance;
    private static final double ATTACK_RANGE = 6.0;

    public Game() {
    }

    public Game(Player player, AIPlayer aiPlayer) {
        this.player = player;
        this.aiPlayer = aiPlayer;
        this.distance = player.getHero().getPosition().distanceTo(aiPlayer.getPosition());
    }

    public void start() {
        System.out.println("Trận đấu bứt đầu! " + player.getHero().getName() + " vs " + aiPlayer.getName());
    }

    public boolean isRange() {
        double dist = player.getHero().distanceTo(aiPlayer);
        double playerRange = player.getHero().getAttackRange();
        double aiRange = aiPlayer.getAttackRange();
        double effectiveRange = Math.min(playerRange, aiRange);  // ← Dùng range nhỏ hơn

        System.out.println("   [Game.isRange] Distance: " + dist);
        System.out.println("   [Game.isRange] Player Range: " + playerRange);
        System.out.println("   [Game.isRange] AI Range: " + aiRange);
        System.out.println("   [Game.isRange] Effective Range: " + effectiveRange);

        boolean inRange = dist <= effectiveRange;
        System.out.println("   [Game.isRange] Result: " + inRange);
        return inRange;
    }

    /**
     *  Check Player có thể đánh AI không (dùng range của Player)
     */
    public boolean isPlayerInRange() {
        return player.getHero().canAttack(aiPlayer);
    }
    /**
     * Check AI có thể đánh Player không (dùng range của AI)
     */
    public boolean isAIInRange() {
        return aiPlayer.canAttack(player.getHero());
    }

    public Player getPlayer() {
        return player;
    }

    public AIPlayer getAiPlayer() {
        return aiPlayer;
    }

    //    public double getDistance() {
//        return distance;
//    }
    public double getDistance() {
        return player.getHero().getPosition().distanceTo(aiPlayer.getPosition());
    }

    public String getPlayerStatus() {
        Hero p = player.getHero();
        return String.format("HP: %d/100 | MP: %d/100 | Pos: %s", p.getHp(), p.getMp(), p.getPosition());
    }

    public String getAIStatus() {
        return String.format("HP: %d/100 | MP: %d/100 | Pos: %s", aiPlayer.getHp(), aiPlayer.getMp(), aiPlayer.getPosition());
    }

    public void setPlayer(Player player) {
        this.player = player;
        // Update distance nếu cần
        if (this.aiPlayer != null) {
            this.distance = player.getHero().getPosition().distanceTo(aiPlayer.getPosition());
        }
    }
}
