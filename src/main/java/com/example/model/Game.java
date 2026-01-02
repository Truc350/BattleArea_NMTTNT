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

    public double getDistance() {
        return distance;
    }

    public void setPlayer(Player player) {

    }
}
