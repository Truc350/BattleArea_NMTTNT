package com.example.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HealthBar extends Pane {
    private Rectangle hpBar;
    private Rectangle mpBar;

    private double hpWidth = 180;
    private double hpHeight = 14;

    private double mpWidth = 180;
    private double mpHeight = 8;

    private int maxHp = 100;
    private int currentHp = 100;

    private int maxMp = 100;
    private int currentMp = 100;

    public HealthBar() {
        hpBar = new Rectangle(hpWidth, hpHeight);
        hpBar.setFill(Color.RED);

        mpBar = new Rectangle(mpWidth, mpHeight);
        mpBar.setFill(Color.BLUE);
        mpBar.setLayoutY(hpHeight);

        setPrefSize(hpWidth, hpHeight + mpHeight);
        getChildren().addAll(hpBar, mpBar);
    }

    // HP
    public int getHp() {
        return currentHp;
    }

    public void setHp(int hp) {
        currentHp = Math.max(0, Math.min(hp, maxHp));
        updateHpBar();
    }

    private void updateHpBar() {
        double percent = (double) currentHp / maxHp;
        hpBar.setWidth(hpWidth * percent);
    }

    // --- MP ---
    public int getMp() {
        return currentMp;
    }

    public void setMp(int mp) {
        currentMp = Math.max(0, Math.min(mp, maxMp));
        updateMpBar();
    }

    private void updateMpBar() {
        double percent = (double) currentMp / maxMp;
        mpBar.setWidth(mpWidth * percent);
    }

    public void takeDamage(int hpDmg, int mpDmg) {
        setHp(currentHp - hpDmg);
        setMp(currentHp - mpDmg);
    }

    // --- MP ---
    public void setMpPercent(double percent) {
        mpBar.setWidth(mpWidth * percent);
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
        setHp(currentHp); // cập nhật lại thanh
    }
}
