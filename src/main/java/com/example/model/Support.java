package com.example.model;

public class Support extends Hero {

    public Support(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Holy Strike", 12, 5, attack + 12, 0, 0));      // 22 damage
        skills.add(new Skill("Divine Blast", 20, 7, attack + 18, 5, 0));     // 28 damage + 5 HP heal
        skills.add(new Skill("Judgment", 30, 10, attack + 25, 15, 10));      // 35 damage + 15 HP + 10 MP

    }
}