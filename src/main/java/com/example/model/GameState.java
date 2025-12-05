// File: src/com/example/model/GameState.java
package com.example.model;

public class GameState {
    public Hero aiHero;
    public Hero playerHero;
    public long time;
    public String moveName;
    public int damageDealt;

    // Constructor dùng cho root
    public GameState(Hero ai, Hero player, long currentTime) {
        this.aiHero = ai;
        this.playerHero = player;
        this.time = currentTime;
        this.moveName = "";
        this.damageDealt = 0;
    }

    // dùng khi tạo các trạng thái con
    public GameState(Hero ai, Hero player, long time, String move, int dmg) {
        this.aiHero = ai;
        this.playerHero = player;
        this.time = time;
        this.moveName = move;
        this.damageDealt = dmg;
    }

    public boolean isTerminal() {
        return aiHero.hp <= 0 || playerHero.hp <= 0;
    }

    @Override
    public String toString() {
        return String.format("State{AI: %d/%d, Player: %d/%d, Move: %s}",
                aiHero.hp, aiHero.maxHP, playerHero.hp, playerHero.maxHP, moveName);
    }
}