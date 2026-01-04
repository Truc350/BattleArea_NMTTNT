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
    protected List<Skill> skills = new ArrayList<Skill>();

    public Hero(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        this.name = name;
        this.maxHP = Math.min(100, maxHP);
        this.hp = Math.min(100, maxHP);
        this.maxMP = Math.min(100, maxMP);
        this.mp = Math.min(100, maxMP);
        this.position = position;
        this.attack = attack;
        this.defense = defense;
        initSkills();
    }

    protected void initSkills() {
        skills.add(new Skill("Basic Attack", 0, 0, attack, 0, 0));
        skills.add(new Skill("Mana Regen", 0, 3, 0, 10, 15));
    }

    // ============== PHƯƠNG THỨC GỐC (có random cho Marksman) ==============
    public boolean useSkill(String skillName, int currentTurn, Hero target) {
        for (Skill skill : skills) {
            if (skill.getName().equals(skillName) && skill.canUse(currentTurn, mp)) {
                return skill.use(currentTurn, this, target);
            }
        }
        System.out.println(name + " Không đủ mana hoặc skill còn cooldown!");
        return false;
    }

    // ============== PHƯƠNG THỨC DETERMINISTIC (cho AI planning) ==============
    /**
     * Phiên bản deterministic của useSkill - dùng cho AI Minimax planning
     * Không có yếu tố random (như crit của Marksman)
     *
     * @param skillName Tên skill cần dùng
     * @param target Mục tiêu (có thể là null cho self-buff)
     * @param fixedDamage Damage cố định (đã tính trước, không random)
     * @return true nếu skill được dùng thành công
     */
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
        int effective = Math.max(0, damage - defense);
        setHp(hp - effective);
    }

    // ============== GETTERS & SETTERS ==============
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(100, hp)); }

    public int getMaxHP() { return maxHP; }
    public void setMaxHP(int maxHP) { this.maxHP = maxHP; }

    public int getMp() { return mp; }
    public void setMp(int mp) { this.mp = Math.max(0, Math.min(100, mp)); }

    public int getMaxMP() { return maxMP; }
    public void setMaxMP(int maxMP) { this.maxMP = maxMP; }

    public Point getPosition() { return position; }
    public void setPosition(Point position) { this.position = position; }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }

    // ============== FACTORY METHOD ==============
    public static Hero getHero(HeroType type, String name, Point position) {
        int maxHP = 100;
        int maxMP = 100;
        int attack = 15;
        int defense = 10;

        switch (type) {
            case FIGHTER:
                attack = 8;
                defense = 10;
                break;
            case MARKSMAN:
                attack = 10;
                defense = 5;
                break;
            case MAGE:
                attack = 5;
                maxMP = 100;
                break;
            case SUPPORT:
                attack = 5;
                defense = 15;
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
        return hero;
    }

    // ============== MOVEMENT ==============
    /**
     * Di chuyển RA XA đối thủ (lùi lại)
     */
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
}