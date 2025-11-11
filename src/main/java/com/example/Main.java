package com.example;

import com.example.model.AIplayer;
import com.example.model.Fighter;

public class Main {
    public static void main(String[] args) {
        long currentTime = System.currentTimeMillis();
        Fighter playerHero = new Fighter("Warrior", 120, 60, 0.0, 15, 10);
        AIplayer ai = new AIplayer("Ninja", 100, 50, 1.5, 12, 8);

        System.out.println("MP ban đầu " + playerHero.getName() + ": " + playerHero.getMp() + ", HP " + ai.getName() + ": " + ai.getHp());
        playerHero.useSkill("Basic Attack", currentTime,ai);// hồi 5mp
        System.out.println("Sau Basic Attack, MP " + playerHero.getName() + ": " + playerHero.getMp() + ", HP " + ai.getName() + ": " + ai.getHp());
        playerHero.useSkill("Mana Regen", currentTime, playerHero);// hồi 15mp
        System.out.println("Sau Mana Regen, MP " + playerHero.getName() + ": " + playerHero.getMp());

        ai.chooseBestAction(currentTime,playerHero);
        System.out.println();
        System.out.println("Sau lượt AI, HP " + playerHero.getName() + ": " + playerHero.getHp() + ", MP " + ai.getName() + ": " + ai.getMp());


    }
}