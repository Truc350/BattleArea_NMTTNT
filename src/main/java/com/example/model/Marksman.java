package com.example.model;

import java.util.Random;

public class Marksman extends Hero {
    float critRate = 0.3f;// tỉ lệ chí mạng

    public Marksman(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }


    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Precision Shot", 6, 3, attack + 5, 0, 0));   // 25 dmg
        skills.add(new Skill("Snipe", 12, 6, (int)(attack * 1.7), 0, 0)); // ~34 dmg → crit 68
        skills.add(new Skill("Deadly Arrow", 20, 10, (int)(attack * 2.5), 0, 0)); // 50 dmg → crit 100
    }

    // Trong Marksman.java
    @Override
    public boolean useSkill(String skillName, long currentTime, Hero target) {
        for (Skill skill : skills) {
            if (skill.getName().equals(skillName) && skill.canUse(currentTime, mp)) {
                int finalDamage = skill.getDamage();
                boolean isCrit = new Random().nextFloat() < critRate;
                if (isCrit) {
                    finalDamage = (int) (finalDamage * 2.0);
                    System.out.println(name + " CHÍ MẠNG! x2");
                }
                mp -= skill.getMpCost();
                target.takeDamage(finalDamage);
                skill.setLastUsedTime(currentTime);
                return true;
            }
        }
        return false;
    }
}
