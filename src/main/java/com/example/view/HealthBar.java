package com.example.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class HealthBar extends Pane {

    public enum Align {
        LEFT, RIGHT
    }

    private Rectangle hpBar;
    private Rectangle mpBar;

    private Text hpText;
    private Text mpText;

    private double hpWidth = 180;
    private double hpHeight = 14;

    private double mpWidth = 180;
    private double mpHeight = 8;

    private int maxHp = 100;
    private int currentHp = 100;

    private int maxMp = 100;
    private int currentMp = 100;

    private Align align;

    public HealthBar(Align align) {
        this.align = align;

        hpBar = new Rectangle(hpWidth, hpHeight);
        hpBar.setFill(Color.RED);

        mpBar = new Rectangle(mpWidth, mpHeight);
        mpBar.setFill(Color.BLUE);
        mpBar.setLayoutY(hpHeight);

        hpText = new Text(currentHp + "");
        hpText.setFill(Color.WHITE);
        hpText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        mpText = new Text(currentMp + "");
        mpText.setFill(Color.WHITE);
        mpText.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        setPrefSize(hpWidth, hpHeight + mpHeight);

        layoutBars();
        getChildren().addAll(hpBar, mpBar, hpText, mpText);
    }

    // =====================
    // LAYOUT TRÁI / PHẢI
    // =====================
    private void layoutBars() {
        if (align == Align.RIGHT) {
            // Player → text nằm bên phải
            hpText.setX(hpWidth + 10);
            hpText.setY(12);

            mpText.setX(mpWidth + 10);
            mpText.setY(hpHeight + mpHeight + 5);

        } else {
            // Enemy → text nằm bên trái
            hpText.setX(-35);
            hpText.setY(12);

            mpText.setX(-35);
            mpText.setY(hpHeight + mpHeight + 5);
        }
    }

    // HP
    public int getHp() {
        return currentHp;
    }

    public void setHp(int hp) {
        currentHp = Math.max(0, Math.min(hp, maxHp));
        double percent = (double) currentHp / maxHp;
//        updateHpBar();
        hpBar.setWidth(hpWidth * percent);
        hpText.setText(currentHp + "");
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
//        updateMpBar();

        double percent = (double) currentMp / maxMp;

        mpBar.setWidth(mpWidth * percent);
        mpText.setText(currentMp + "");
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
