package com.example.model;

public class Move {
    String name;
    String nameSkill;
    int damage;
    int time;

    public Move(String nameSkill, int damage, int time) {
        this.nameSkill = nameSkill;
        this.damage = damage;
        this.time = time;
    }

    @Override
    public String toString() {
        return nameSkill + " (dmg=" + damage + ", t=" + time + ")";
    }
}
