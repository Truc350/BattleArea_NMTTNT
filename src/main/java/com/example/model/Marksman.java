package com.example.model;

import java.util.Random;

public class Marksman extends Hero {
    private float critRate = 0.3f; // 30% crit chance
    private Random random = new Random();

    public Marksman(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Precision Shot", 8, 3, attack + 7, 0, 0));      // 25 damage (có thể crit x2 = 50)
        skills.add(new Skill("Snipe", 15, 6, attack + 12, 0, 0));             // 30 damage (crit = 60)
        skills.add(new Skill("Deadly Arrow", 25, 10, attack + 22, 0, 0));     // 40 damage (crit = 80)
    }

    /**
     *OVERRIDE useSkill() - Có crit cho REAL GAMEPLAY
     * Dùng khi Player hoặc AI thực sự thực hiện action trong game
     */
    @Override
    public boolean useSkill(String skillName, int currentTurn, Hero target) {
        for (Skill skill : skills) {
            if (skill.getName().equals(skillName) && skill.canUse(currentTurn, mp)) {
                int finalDamage = skill.getDamage();

                // ✓ Roll crit cho gameplay thật
                boolean isCrit = random.nextFloat() < critRate;
                if (isCrit) {
                    finalDamage = (int) (finalDamage * 2.0);
                    System.out.println("   💥 " + name + " CHÍ MẠNG x2!");
                }

                // Apply damage và cost
                mp -= skill.getMpCost();
                target.takeDamage(finalDamage);
                skill.setLastUsedTurn(currentTurn);
                return true;
            }
        }
        return false;
    }

    /**
     * ⚠️ KHÔNG CẦN override useSkillDeterministic()
     * Vì Hero.useSkillDeterministic() đã nhận fixedDamage từ ngoài
     * AI đã tính expectedDamage = damage * 1.3 rồi
     */
    // Không cần code gì ở đây, dùng của Hero là đủ

    public float getCritRate() {
        return critRate;
    }

    public void setCritRate(float critRate) {
        this.critRate = critRate;
    }
}