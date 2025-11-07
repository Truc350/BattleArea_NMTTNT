package com.example.model;

public class Support extends Hero{

    public Support(String name, int maxHP, int maxMP, double position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Heal Wave", 15, 7,0,25,10));
        skills.add(new Skill("Group Shield", 18, 9,0,0,20));
        skills.add(new Skill("Revive", 40, 30,0,50,30));
    }
}
