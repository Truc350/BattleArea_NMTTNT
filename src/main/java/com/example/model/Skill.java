package com.example.model;

public class Skill {
    private String name;
    private int mpCost;
    private int cooldownTurns;
    private int lastUsedTurn;
    private int damage;
    private int healHP;
    private int healMP;

    public Skill(String name, int mpCost, int cooldownTurns, int damage, int healHP, int healMP) {
        this.name = name;
        this.mpCost = mpCost;
        this.cooldownTurns = cooldownTurns;
        this.damage = damage;
        this.healHP = healHP;
        this.healMP = healMP;
        this.lastUsedTurn = -999;
    }

    public boolean canUse(int currentTurn, int currentMP) {
        boolean cooldownReady = currentTurn - lastUsedTurn >= cooldownTurns;
        boolean enoughMP = currentMP >= mpCost;

        return cooldownReady && enoughMP;
    }

    public boolean use(int currentTurn, Hero user, Hero target) {
        if (canUse(currentTurn, user.getMp())) {
            user.setMp(user.getMp() - mpCost + healMP);
            if (healHP > 0 && user == target) user.setHp(user.getHp() + healHP);

            if (damage > 0 && target != null) {
                target.takeDamage(damage);
            }

            lastUsedTurn = currentTurn;
            return true;
        }
        return false;
    }

    public String getName() { return name; }
    public int getMpCost() { return mpCost; }
    public int getCooldownTurns() { return cooldownTurns; }
    public int getLastUsedTurn() { return lastUsedTurn; }
    public void setLastUsedTurn(int turn) { this.lastUsedTurn = turn; }
    public int getDamage() { return damage; }
    public int getHealHP() { return healHP; }
    public int getHealMP() { return healMP; }
}