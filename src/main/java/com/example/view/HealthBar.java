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

    public HealthBar() {
        hpBar = new Rectangle(hpWidth, hpHeight);
        hpBar.setFill(Color.RED);

        mpBar = new Rectangle(mpWidth, mpHeight);
        mpBar.setFill(Color.BLUE);
        mpBar.setLayoutY(hpHeight);

        setPrefSize(hpWidth, hpHeight + mpHeight);
        getChildren().addAll(hpBar, mpBar);
    }

    public void setHp(double percent) {
        hpBar.setWidth(hpWidth * percent);
    }

    public void setMp(double percent) {
        mpBar.setWidth(mpWidth * percent);
    }
}
