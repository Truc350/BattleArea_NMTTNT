package com.example.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Hero {
    protected String name;
    protected int hp, maxHP;
    protected int mp, maxMP;
    protected Point position;
    protected int attack;
    protected int defense;
    protected double attackRange;
    protected List<Skill> skills = new ArrayList<Skill>();

    protected boolean isDefending = false;
    protected int baseDefense;

    public Hero(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        this.name = name;
        this.maxHP = Math.min(100, maxHP);
        this.hp = Math.min(100, maxHP);
        this.maxMP = Math.min(100, maxMP);
        this.mp = Math.min(100, maxMP);
        this.position = position;
        this.attack = attack;
        this.defense = defense;
        this.baseDefense = defense;
        this.attackRange = 6.0;
        this.isDefending = false;
        initSkills();
    }

    public double getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(double attackRange) {
        this.attackRange = attackRange;
    }

    protected void initSkills() {
        skills.add(new Skill("Basic Attack", 0, 0, attack, 0, 0));
        skills.add(new Skill("Mana Regen", 0, 3, 0, 10, 15));
    }

    public void setDefending(boolean defending) {
        this.isDefending = defending;

        if (defending) {
            this.defense = this.baseDefense * 2;
        } else {
            this.defense = this.baseDefense;
        }
    }

    public void activateDefending() {
        if (this.isDefending) {
            return;
        }

        this.isDefending = true;
        this.defense = this.baseDefense * 2;
    }

    public boolean isDefending() {
        return isDefending;
    }

    public void resetDefense() {
        if (isDefending) {
            this.defense = this.baseDefense;
            this.isDefending = false;
        }
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public boolean useSkill(String skillName, int currentTurn, Hero target) {
        for (Skill skill : skills) {
            if (skill.getName().equals(skillName) && skill.canUse(currentTurn, mp)) {
                return skill.use(currentTurn, this, target);
            }
        }
        return false;
    }

    public boolean useSkillDeterministic(String skillName, int currentTurn,
                                         Hero target, int fixedDamage) {
        for (Skill skill : skills) {
            if (skill.getName().equals(skillName) && skill.canUse(currentTurn, mp)) {
                this.setMp(this.getMp() - skill.getMpCost());
                if (skill.getHealHP() > 0) this.setHp(this.getHp() + skill.getHealHP());
                if (skill.getHealMP() > 0) this.setMp(this.getMp() + skill.getHealMP());
                if (fixedDamage > 0 && target != null) target.takeDamage(fixedDamage);
                skill.setLastUsedTurn(currentTurn);
                return true;
            }
        }
        return false;
    }

    public void takeDamage(int damage) {
        int effective = Math.max((int) (damage * 0.2), damage - defense);
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
        this.hp = Math.max(0, Math.min(100, hp));
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
        this.mp = Math.max(0, Math.min(100, mp));
    }

    public int getMaxMP() {
        return maxMP;
    }

    public void setMaxMP(int maxMP) {
        this.maxMP = maxMP;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
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

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public static Hero getHero(HeroType type, String name, Point position) {
        int maxHP = 100;
        int maxMP = 100;
        int attack = 15;
        int defense = 10;
        double attackRange = 6.0;

        switch (type) {
            case FIGHTER:
                maxHP = 100;
                maxMP = 100;
                attack = 15;
                defense = 12;
                attackRange = 8.0;
                break;

            case MARKSMAN:
                maxHP = 100;
                maxMP = 90;
                attack = 18;
                defense = 6;
                attackRange = 12.0;
                break;

            case MAGE:
                maxHP = 100;
                maxMP = 100;
                attack = 12;
                defense = 7;
                attackRange = 10.0;
                break;

            case SUPPORT:
                maxHP = 100;
                maxMP = 100;
                attack = 10;
                defense = 15;
                attackRange = 10.0;
                break;
        }

        Hero hero;
        switch (type) {
            case MARKSMAN:
                hero = new Marksman(name, maxHP, maxMP, position, attack, defense);
                break;
            case MAGE:
                hero = new Mage(name, maxHP, maxMP, position, attack, defense);
                break;
            case SUPPORT:
                hero = new Support(name, maxHP, maxMP, position, attack, defense);
                break;
            default:
                hero = new Fighter(name, maxHP, maxMP, position, attack, defense);
        }

        hero.setAttackRange(attackRange);
        hero.setMp(hero.getMaxMP());
        return hero;
    }

    public void moveAway(Hero target, double speed) {
        double dx = this.position.getX() - target.getPosition().getX();
        double dy = this.position.getY() - target.getPosition().getY();
        double dist = Math.hypot(dx, dy);

        if (dist > 0.001) {
            double newX = this.position.getX() + (dx / dist) * speed;
            double newY = this.position.getY() + (dy / dist) * speed;
            this.position = new Point(newX, newY);
        }
    }

    public double distanceTo(AIPlayer aiPlayer) {
        return this.position.distanceTo(aiPlayer.getPosition());
    }

    public boolean canAttack(Hero target) {
        double distance = this.position.distanceTo(target.getPosition());
        return distance <= this.attackRange;
    }
}
