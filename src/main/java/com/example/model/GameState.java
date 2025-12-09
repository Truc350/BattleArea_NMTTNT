// File: src/com/example/model/GameState.java
package com.example.model;

public class GameState {
    public Hero aiHero;
    public Hero playerHero;
    private Move move;
    public long time;


    // Constructor dùng cho root
    public GameState(Hero ai, Hero player, long currentTime) {
        this.aiHero = ai;
        this.playerHero = player;
        this.time = currentTime;
        this.move = null;
    }

    // dùng khi tạo các trạng thái con
    public GameState(Hero ai, Hero player, long time, Move move) {
        this.aiHero = ai;
        this.playerHero = player;
        this.time = time;
        this.move = move;
    }

    public boolean isTerminal() {
        return aiHero.hp <= 0 || playerHero.hp <= 0;
    }

    @Override
    public String toString() {
        return String.format("State{AI %d/%d, Player %d/%d, Move: %s}",
                aiHero.hp, aiHero.maxHP,
                playerHero.hp, playerHero.maxHP,
                move == null ? "ROOT" : move.toString());
    }

    public Hero getAiHero() {
        return aiHero;
    }

    public Hero getPlayerHero() {
        return playerHero;
    }

    public Move getMove() {
        return move;
    }

    public long getTime() {
        return time;
    }
}