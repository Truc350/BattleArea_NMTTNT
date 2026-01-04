package com.example.view;

import com.example.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;

public class ArenaSelectView {
    private GameController controller;

    public ArenaSelectView() {
        this.controller = GameController.getInstance();
    }

    public ArenaSelectView(GameController controller) {
        this.controller = controller;
    }

    public Scene getScene() {

        Label title = new Label("CHỌN SÀN ĐẤU");
        title.setStyle("""
                    -fx-font-size: 40px;
                    -fx-font-weight: bold;
                    -fx-text-fill: white;
                """);
        title.setEffect(new DropShadow(8, Color.BLACK));

        HBox arenas = new HBox(40);
        arenas.setAlignment(Pos.CENTER);

        String[] arenaImages = {
                "/img/arena/sandau1.jpg",
                "/img/arena/sandau2.jpg",
                "/img/arena/sandau3.jpg"
        };

        for (String path : arenaImages) {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.out.println("❌ Không tìm thấy ảnh: " + path);
                continue; // bỏ ảnh lỗi, không làm crash app
            }
            ImageView img = new ImageView(new Image(url.toExternalForm()));

            img.setFitWidth(360);
            img.setFitHeight(200);
            img.setPreserveRatio(false);

            // Bọc ảnh trong khung
            StackPane frame = new StackPane(img);

            frame.setOnMouseEntered(e -> {
                frame.setScaleX(1.08);
                frame.setScaleY(1.08);
//                frame.setTranslateY(-10);
                frame.setEffect(new DropShadow(20, Color.GOLD));
            });

            frame.setOnMouseExited(e -> {
                frame.setScaleX(1.0);
                frame.setScaleY(1.0);
//                frame.setTranslateY(0);
                frame.setEffect(null);
            });
            frame.setOnMouseClicked(e -> {
                controller.onArenaSelected(path);
            });
            arenas.getChildren().add(frame);
        }

        // ================== Background phía sau arenas ==================
        StackPane arenaBackground = new StackPane(arenas);
        arenaBackground.setAlignment(Pos.CENTER);
        arenaBackground.setPrefSize(1300, 700);
        arenaBackground.setMaxSize(1300, 700);
        arenaBackground.setMinSize(1300, 700);

        URL bgUrl = getClass().getResource("/img/arena/backgroundSanDau.png");
        if (bgUrl != null) {
            BackgroundImage bg = new BackgroundImage(
                    new Image(bgUrl.toExternalForm()),
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            1300, 700, false, false, false, true
                    )
            );
            arenaBackground.setBackground(new Background(bg));
        }

        // Nút Lịch sử đấu ở góc dưới phải
        Button historyButton = new Button("LỊCH SỬ ĐẤU");
        historyButton.setStyle("""
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-background-color: #3498db;
                -fx-text-fill: white;
                -fx-padding: 12 30;
                -fx-background-radius: 10;
                -fx-cursor: hand;
                """);

        historyButton.setEffect(new DropShadow(5, Color.BLACK));

        historyButton.setOnMouseEntered(e -> {
            historyButton.setStyle("""
                    -fx-font-size: 16px;
                    -fx-font-weight: bold;
                    -fx-background-color: #5dade2;
                    -fx-text-fill: white;
                    -fx-padding: 12 30;
                    -fx-background-radius: 10;
                    -fx-cursor: hand;
                    """);
            historyButton.setScaleX(1.05);
            historyButton.setScaleY(1.05);
        });

        historyButton.setOnMouseExited(e -> {
            historyButton.setStyle("""
                    -fx-font-size: 16px;
                    -fx-font-weight: bold;
                    -fx-background-color: #3498db;
                    -fx-text-fill: white;
                    -fx-padding: 12 30;
                    -fx-background-radius: 10;
                    -fx-cursor: hand;
                    """);
            historyButton.setScaleX(1.0);
            historyButton.setScaleY(1.0);
        });

        historyButton.setOnAction(e -> {
            MatchHistoryView historyView = new MatchHistoryView();
            controller.getStage().setScene(historyView.getScene(controller.getStage()));
        });

        StackPane main = new StackPane(arenaBackground);

        VBox overlay = new VBox(40, title, arenas);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(Insets.EMPTY);

        // Đặt nút ở góc dưới phải
        StackPane.setAlignment(historyButton, Pos.TOP_LEFT);
        StackPane.setMargin(historyButton, new Insets(30, 0, 0, 30));

        main.getChildren().addAll(overlay, historyButton);

        return new Scene(main, 1300, 700);
    }
}
