package com.example.model;

public class Game {
    private Player player;
    private AIPlayer aiPlayer;
    private double distance;
    private static final double ATTACK_RANGE = 2.0;

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
        return player.getHero().distanceTo(aiPlayer) <= ATTACK_RANGE;
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
    public  double getDistance(){
        return player.getHero().getPosition().distanceTo(aiPlayer.getPosition());
    }
    public String getPlayerStatus(){
        Hero p = player.getHero();
        return String.format("HP: %d/100 | MP: %d/100 | Pos: %s", p.getHp(), p.getMp(), p.getPosition());
    }
    public  String  getAIStatus(){
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
