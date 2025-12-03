package com.example.model;

public class Mage extends Hero{

    public Mage(String name, int maxHP, int maxMP, double position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Fireball", 8, 3, attack + 6, 0, 4));      // 20 dmg
        skills.add(new Skill("Lightning Bolt", 13, 6, attack + 10, 0, 0)); // 24 dmg
        skills.add(new Skill("Meteor Strike", 25, 10, attack * 2, 0, 8)); // 28 dmg

    }
}
