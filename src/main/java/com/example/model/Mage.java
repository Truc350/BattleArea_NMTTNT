package com.example.model;

public class Mage extends Hero {


    public Mage(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Fireball", 8, 3, attack + 3, 0, 4));
        skills.add(new Skill("Lightning Bolt", 13, 6, attack + 4, 0, 0));
        skills.add(new Skill("Meteor Strike", 25, 10, (int) (attack * 1.5), 0, 8));

    }
}
