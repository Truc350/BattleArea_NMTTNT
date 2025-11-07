package com.example.model;

public class Skill {
    private String name;
    private int mpCost;
    private int cooldown;
    private long lastUsedTime;
    private int damage;
    private int healHP;
    private int healMP;

    public Skill(String name, int mpCost, int cooldown, int damage, int healHP, int healMP) {
        this.name = name;
        this.mpCost = mpCost;
        this.cooldown = cooldown;
        this.damage = damage;
        this.healHP = healHP;
        this.healMP = healMP;
        this.lastUsedTime = 0;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMpCost() {
        return mpCost;
    }

    public void setMpCost(int mpCost) {
        this.mpCost = mpCost;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public int getHealHP() {
        return healHP;
    }

    public void setHealHP(int healHP) {
        this.healHP = healHP;
    }

    public int getHealMP() {
        return healMP;
    }

    public void setHealMP(int healMP) {
        this.healMP = healMP;
    }

    public boolean canUse(long currentTime, int currentMP) {
        return currentTime - lastUsedTime >= cooldown * 1000L && currentMP >= mpCost;
    }

    public boolean use(long currentTime, Hero user, Hero target) {
        if (canUse(currentTime, user.getMp())) {
            user.setMp(user.getMp() - mpCost + healMP);
            if (healHP > 0 && user == target) user.setHp(user.getHp() + healHP);
            if (damage > 0 && target != null) target.takeDamage(damage);
            lastUsedTime = currentTime;
            return true;
        }
        return false;
    }
}
