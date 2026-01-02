package com.example.view;

import com.example.controller.GameController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

            frame.setStyle("""
                            -fx-border-color: white;
                            -fx-border-width: 3;
                            -fx-border-radius: 10;
                            -fx-background-radius: 10;
                    """);

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

        StackPane main = new StackPane(arenaBackground);

        VBox overlay = new VBox(40, title, arenas);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(Insets.EMPTY);

        main.getChildren().add(overlay);

        return new Scene(main, 1300, 700);
    }
}
