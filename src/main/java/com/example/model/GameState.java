// File: src/com/example/model/GameState.java
package com.example.model;

public class GameState {
    public Hero aiHero;
    public Hero playerHero;
    private Move move;
    public int turn;// luot di


    // Constructor dùng cho root
    public GameState(Hero ai, Hero player, int turn) {
        this.aiHero = ai;
        this.playerHero = player;
        this.turn = turn;
        this.move = null;
    }

    // dùng khi tạo các trạng thái con
    public GameState(Hero ai, Hero player, int  turn, Move move) {
        this.aiHero = ai;
        this.playerHero = player;
        this.turn = turn;
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

    public int getTurn() {
        return turn;
    }
}