package com.example.model;

public class Fighter extends  Hero {


    public Fighter(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Rage Strike", 10, 4, attack +6, 0,0));
        skills.add(new Skill("Fury Burst", 15, 6, attack +12, 0,0));
        skills.add(new Skill("Ultimate Rage", 22, 10, (int)(attack * 1.8), 0,0));
    }
}
