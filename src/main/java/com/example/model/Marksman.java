package com.example.model;

import java.util.Random;

public class Marksman extends Hero {
    private float critRate = 0.3f;

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

    @Override
    public boolean useSkill(String skillName, long currentTime, Hero target) {
        if (super.useSkill(skillName, currentTime, target)) {
            if (new Random().nextFloat() < critRate) {
                target.takeDamage(attack * 2);
                System.out.println(name + " chí mạng");
            }
            return true;
        }
        return false;
    }
}
