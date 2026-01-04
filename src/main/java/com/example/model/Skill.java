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
        this.lastUsedTurn = -999; // ✅ Đảm bảo skill luôn sẵn sàng ở đầu game
    }

    // ✓ Check theo TURN thay vì TIME
    public boolean canUse(int currentTurn, int currentMP) {
        boolean cooldownReady = currentTurn - lastUsedTurn >= cooldownTurns;
        boolean enoughMP = currentMP >= mpCost;

        // Debug log
        if (!cooldownReady) {
            System.out.println("   [" + name + "] Cooldown: còn " +
                    (cooldownTurns - (currentTurn - lastUsedTurn)) + " turns");
        }
        if (!enoughMP) {
            System.out.println("   [" + name + "] MP: cần " + mpCost + ", có " + currentMP);
        }

        return cooldownReady && enoughMP;
    }

    // ✓ Use theo TURN
    public boolean use(int currentTurn, Hero user, Hero target) {
        if (canUse(currentTurn, user.getMp())) {
            System.out.println("   [Skill.use] " + name + " - User: " + user.getName() + " → Target: " + target.getName());
            System.out.println("   [Skill.use] Damage: " + damage + " | Target HP BEFORE: " + target.getHp());

            user.setMp(user.getMp() - mpCost + healMP);
            if (healHP > 0 && user == target) user.setHp(user.getHp() + healHP);

            if (damage > 0 && target != null) {
                target.takeDamage(damage);
                System.out.println("   [Skill.use] Target HP AFTER takeDamage: " + target.getHp());
            }

            lastUsedTurn = currentTurn;
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