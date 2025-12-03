package com.example.model;

public class Game {
    private Player player;
    private AIPlayer aiPlayer;
    private double distance;

    public Game(Player player, AIPlayer aiPlayer) {
        this.player = player;
        this.aiPlayer = aiPlayer;
        this.distance = Math.abs(player.getHero().getPosition() - aiPlayer.getPosition());
    }

    public void start() {
        System.out.println("Trận đấu bứt đầu! " + player.getHero().getName() + " vs " + aiPlayer.getName());
    }

    public boolean isRange() {
        return distance <= 1;
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
}
