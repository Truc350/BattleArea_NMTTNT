package com.example.model;

public class Move {
    public final String name;
    public final int damage;
    public final int time;

    public Move(String name, int damage, int time) {
        this.name = name;
        this.damage = damage;
        this.time = time;
    }

    public String getName() { return name; }

    @Override public String toString() { return name + " (dmg=" + damage + ")"; }
}