package com.example.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Hero {
    protected String name;
    protected int hp, maxHP;
    protected int mp, maxMP;
    protected double position;
    protected int attack;
    protected int defense;
    protected List<Skill> skills = new ArrayList<Skill>();

    public Hero(String name, int maxHP, int maxMP, double position, int attack, int defense) {
        this.name = name;
        this.maxHP = maxHP;
        this.hp = maxHP;
        this.maxMP = maxMP;
        this.mp = maxMP;
        this.position = position;
        this.attack = attack;
        this.defense = defense;
        initSkills();
    }

    public void initSkills() {
        skills.add(new Skill("Basic Attack", 0, 0, attack, 0, 5));//đánh thường , hồi 5mp
        skills.add(new Skill("Mana Regen", 0, 2, 0, 0, 15));// hoi 15mp
    }

    public boolean useSkill(String skillName, long currentTime, Hero target) {
        for (Skill skill : skills) {
            if (skill.getName().equals(skillName) && skill.canUse(currentTime, mp)) {
                return skill.use(currentTime, this, target);
            }
        }
        System.out.println(name + "Không đủ mana hoặc skill còn cooldown!");
        return false;
    }
    public void takeDamage(int damage){
        int effective = Math.max(0, damage - defense);
        setHp(hp - effective);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, Math.min(maxHP, hp));
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = Math.max(0,Math.min(maxMP, mp));
    }

    public int getMaxMP() {
        return maxMP;
    }

    public void setMaxMP(int maxMP) {
        this.maxMP = maxMP;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
}
