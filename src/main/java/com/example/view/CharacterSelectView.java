package com.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;

import java.net.URL;

public class CharacterSelectView {
    private String arenaPath;
    public CharacterSelectView(String arenaPath) {
        this.arenaPath = arenaPath;
    }

    public Scene getScene() {

        Label title = new Label("CHỌN NHÂN VẬT");
        title.setStyle("""
                -fx-font-size: 40px;
                -fx-font-weight: bold;
                -fx-text-fill: white;
        """);
        title.setEffect(new DropShadow(8, Color.BLACK));

        HBox chars = new HBox(40);
        chars.setAlignment(Pos.CENTER);

        String[] characters = {
                "/img/character/dausi_phai.png",
                "/img/character/phapsu-phai.png",
                "/img/character/trothu_phai.png",
                "/img/character/xathu.png"
        };

        for (String path : characters) {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.out.println("❌ Không tìm thấy nhân vật: " + path);
                continue;
            }

            ImageView img = new ImageView(new Image(url.toExternalForm()));

            if (path.contains("/img/character/trothu_phai.png")) {
                img.setFitWidth(240);   // rộng hơn
                img.setFitHeight(260);  // giữ chiều cao
            } else {
                img.setFitWidth(200);
                img.setFitHeight(260);
            }

            img.setPreserveRatio(true);

            // ====== FRAME BỌC NHÂN VẬT ======
            StackPane frame = new StackPane(img);
            frame.setStyle("""
                    -fx-border-color: white;
                    -fx-border-width: 2;
                    -fx-border-radius: 10;
                    -fx-background-radius: 10;
            """);

            // ====== HOVER EFFECT (CHỈ NHÂN VẬT ĐƯỢC CHẠM) ======
            frame.setOnMouseEntered(e -> {
                frame.setScaleX(1.1);
                frame.setScaleY(1.1);
                frame.setTranslateY(-12);
                frame.setEffect(new DropShadow(20, Color.WHITE));
            });

            frame.setOnMouseExited(e -> {
                frame.setScaleX(1.0);
                frame.setScaleY(1.0);
                frame.setTranslateY(0);
                frame.setEffect(null);
            });

            // ====== CLICK → VÀO GAME ======
            frame.setOnMouseClicked(e ->
                    MainApp.showGame(arenaPath, path)
            );

            chars.getChildren().add(frame);
        }

        StackPane background = new StackPane();
        background.setPrefSize(1300, 700);

        URL bgUrl = getClass().getResource("/img/arena/backgroundNhanVat.jpg");
        if (bgUrl != null) {
            background.setBackground(new Background(
                    new BackgroundImage(
                            new Image(bgUrl.toExternalForm()),
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(
                                    1300, 700,
                                    false, false,
                                    false, true
                            )
                    )
            ));
        } else {
            background.setStyle("-fx-background-color:#1e1e1e;");
        }

        // ================== OVERLAY ==================
        VBox overlay = new VBox(40, title, chars);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(Insets.EMPTY);

        background.getChildren().add(overlay);

        return new Scene(background, 1300, 700);
    }
}
