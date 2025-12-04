package com.example.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class SkillButton extends StackPane {
    Button btn;
    Canvas overlay;

    public SkillButton(Button btn) {
        if (btn == null) {
            throw new IllegalArgumentException("Button cannot be null!");
        }
        this.btn = btn;

        this.overlay = new Canvas(btn.getPrefWidth(), btn.getPrefHeight());

        overlay = new Canvas(btn.getPrefWidth(), btn.getPrefHeight());
        overlay.setMouseTransparent(true);

        setPrefSize(btn.getPrefWidth(), btn.getPrefHeight());
        getChildren().addAll(btn, overlay);
    }

    public Canvas getOverlay() {
        return overlay;
    }

    public Button getButton() {
        return btn;
    }
}
