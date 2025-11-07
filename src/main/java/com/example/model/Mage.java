package com.example.model;

public class Mage extends Hero{

    public Mage(String name, int maxHP, int maxMP, double position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Fireball", 12, 5, attack + 8, 0 ,5));
        skills.add(new Skill("Lighting Bolt", 20, 10, attack  + 15, 0, 0));
        skills.add(new Skill("Meteor Strike", 35, 25, attack  * 2 + 20, 0, 10));

    }
}
