package com.example.model;

public class Skill {
    private String name;
    private int mpCost;
    private int cooldownTurns;        // ✓ Số lượt cooldown
    private int lastUsedTurn;         // ✓ Lượt cuối dùng
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
        this.lastUsedTurn = -999; // ✓ Bắt đầu đã sẵn sàng
    }

    // ✓ Check theo TURN thay vì TIME
    public boolean canUse(int currentTurn, int currentMP) {
        return currentTurn - lastUsedTurn >= cooldownTurns && currentMP >= mpCost;
    }

    // ✓ Use theo TURN
    public boolean use(int currentTurn, Hero user, Hero target) {
        if (canUse(currentTurn, user.getMp())) {
            user.setMp(user.getMp() - mpCost + healMP);
            if (healHP > 0 && user == target) user.setHp(user.getHp() + healHP);
            if (damage > 0 && target != null) target.takeDamage(damage);
            lastUsedTurn = currentTurn; // ✓ Lưu lượt
            return true;
        }
        return false;
    }

    // Getters/Setters
    public String getName() { return name; }
    public int getMpCost() { return mpCost; }
    public int getCooldownTurns() { return cooldownTurns; }
    public int getLastUsedTurn() { return lastUsedTurn; }
    public void setLastUsedTurn(int turn) { this.lastUsedTurn = turn; }
    public int getDamage() { return damage; }
    public int getHealHP() { return healHP; }
    public int getHealMP() { return healMP; }
}