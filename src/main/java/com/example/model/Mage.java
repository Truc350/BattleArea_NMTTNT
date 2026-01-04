package com.example.model;

public class Mage extends Hero {


    public Mage(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Fireball", 10, 3, attack + 10, 0, 0));         // 22 damage
        skills.add(new Skill("Lightning Bolt", 18, 6, attack + 18, 0, 0));   // 30 damage
        skills.add(new Skill("Meteor Strike", 30, 10, attack + 28, 0, 0));   // 40 damage

    }
}
