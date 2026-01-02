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
        this.maxHP = Math.min(100, maxHP); // Giới hạn maxHP là 100
        this.hp = Math.min(100, maxHP);    // Ban đầu hp = maxHP, giới hạn 100
        this.maxMP = Math.min(100, maxMP); // Giới hạn maxMP là 100
        this.mp = Math.min(100, maxMP);    // Ban đầu mp = maxMP, giới hạn 100
        this.position = position;
        this.attack = attack;
        this.defense = defense;
        initSkills();
    }

    protected void initSkills() {
        // Basic Attack: KHÔNG hồi mana (healMP=0), luôn dùng
        skills.add(new Skill("Basic Attack", 0, 0, attack, 0, 0));
        // Mana Regen: Hồi mana
        skills.add(new Skill("Mana Regen", 0, 3, 0, 10, 15));
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

    public void takeDamage(int damage) {
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

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }


    // tạo hero theo loại
    public static Hero getHero(HeroType type, String name, Point position) {
        int maxHP = 100;
        int maxMP = 100;
        int attack = 15;
        int defense = 10;

        // Stats khác nhau theo loại
        switch (type) {
            case FIGHTER:// Tanky, damage cao
                attack = 8;
                defense = 10;
                break;
            case MARKSMAN:// Damage cực cao, yếu thủ
                attack = 10;
                defense = 5;
                break;
            case MAGE:// Phép mạnh, MP đầy
                attack = 5;
                maxMP = 100;
                break;
            case SUPPORT:// Heal tốt, sống dai
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

    // Di chuyển RA XA đối thủ
    // Di chuyển RA XA đối thủ (lùi lại)
    public void moveAway(Hero target, double speed) {
        double dx = this.position.getX() - target.getPosition().getX();
        double dy = this.position.getY() - target.getPosition().getY();
        double dist = Math.hypot(dx, dy);

        if (dist > 0.001) {
            double newX = this.position.getX() + (dx / dist) * speed;
            double newY = this.position.getY() + (dy / dist) * speed;

            this.position = new Point(newX, newY);  // constructor sẽ tự clamp nếu vượt giới hạn
        }
    }

    public double distanceTo(AIPlayer aiPlayer) {
        return this.position.distanceTo(aiPlayer.getPosition());
    }

}
