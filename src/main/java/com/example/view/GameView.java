package com.example.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class GameView extends Application {
    private WebView webView;

    @Override
    public void start(Stage primaryStage) {
        webView = new WebView();
        // Nhúng mô hình 3D Sketchfab (thay your-model-id bằng ID thực tế)
        webView.getEngine().load("https://sketchfab.com/models/your-model-id/embed");

        Scene scene = new Scene(webView, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game UI với Sketchfab");
        primaryStage.show();
    }

    public void updateHeroDisplay(String heroName) {
        // Cập nhật mô hình 3D dựa trên heroName (cần API Sketchfab để thay đổi model)
        webView.getEngine().load("https://sketchfab.com/models/" + getModelId(heroName) + "/embed");
    }

    private String getModelId(String heroName) {
        // Logic ánh xạ tên hero với ID Sketchfab (giả lập)
        switch (heroName.toLowerCase()) {
            case "warrior": return "warrior-id";
            case "ninja": return "ninja-id";
            default: return "default-id";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}