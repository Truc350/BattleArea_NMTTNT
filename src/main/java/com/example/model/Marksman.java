package com.example.model;

import java.util.Random;

public class Marksman extends Hero {
    float critRate = 0.3f;// tỉ lệ chí mạng

    public Marksman(String name, int maxHP, int maxMP, double position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Percision Shot", 10, 6, attack + 5, 0, 0));
        skills.add(new Skill("Snipe", 18, 10, attack * 2, 0, 0));
        skills.add(new Skill("Deadly Arrow", 25, 15, attack * 3, 0, 0));
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
