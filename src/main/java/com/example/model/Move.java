package com.example.model;

public class Move {
    public final String name;
    public final int damage;
    public final int turn; // ✓ Đổi từ time → turn

    public Move(String name, int damage, int turn) {
        this.name = name;
        this.damage = damage;
        this.turn = turn;
    }

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public int getTurn() { return turn; }

    @Override
    public String toString() {
        return name + " (dmg=" + damage + ", turn=" + turn + ")";
    }
}