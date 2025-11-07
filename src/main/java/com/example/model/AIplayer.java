package com.example.model;

public class AIplayer extends Hero {
    private int maxDepth = 2;

    public AIplayer(String name, int maxHP, int maxMP, double position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Rage Strike", 15, 8, attack + 10, 0, 0));
        skills.add(new Skill("Fury Burst", 20, 12, attack + 20, 0, 0));
        skills.add(new Skill("Ultimate Rage", 30, 20, attack * 2, 0, 0));
    }

}
