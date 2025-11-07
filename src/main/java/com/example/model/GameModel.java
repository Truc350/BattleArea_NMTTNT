package com.example.model;

import java.util.Random;

/**
 * quan ly trang thai tro choi
 */
public class GameModel {
    private Charactor player;
    private Charactor ai;
    private double distance;

    public GameModel(Charactor player, Charactor ai) {
        this.player = player;
        this.ai = ai;
        this.distance = Math.abs(player.getPosition() - ai.getPosition());
    }

    public boolean isInRange() {
        return this.distance <= 1.0;
    }

    public void takeTurn(Charactor attacker, Charactor defender) {
        if (isInRange()) {
            int damage = new Random().nextInt(10) + 10;
//            defender.takeDamage(damage);
        }
    }

    public Charactor getPlayer() {
        return player;
    }

    public Charactor getAi() {
        return ai;
    }
}
