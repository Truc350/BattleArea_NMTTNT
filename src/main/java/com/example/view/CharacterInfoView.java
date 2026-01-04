package com.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.shape.SVGPath;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;

public class CharacterInfoView {

    public Scene getScene(Stage stage) {
        // Tiêu đề
        Label title = new Label("GIỚI THIỆU NHÂN VẬT");
        title.setStyle("""
                -fx-font-size: 36px;
                -fx-font-weight: bold;
                -fx-text-fill: white;
                """);
        title.setEffect(new DropShadow(8, Color.BLACK));

        // Container cho 4 nhân vật
        HBox charactersContainer = new HBox(50);
        charactersContainer.setAlignment(Pos.CENTER);
        charactersContainer.setPadding(new Insets(40));

        // Thêm 4 nhân vật - CHỈ HIỆN ẢNH VÀ TÊN
        charactersContainer.getChildren().addAll(
                createSimpleCharacterCard(stage, "Fighter", "/img/character/dausi_phai.png", "FIGHTER\nCHIẾN BINH"),
                createSimpleCharacterCard(stage, "Mage", "/img/character/phapsu-phai.png", "MAGE\nPHÁP SƯ"),
                createSimpleCharacterCard(stage, "Marksman", "/img/character/xathu.png", "MARKSMAN\nXẠ THỦ"),
                createSimpleCharacterCard(stage, "Support", "/img/character/trothu_phai.png", "SUPPORT\nTRỢ THỦ")
        );

        // ScrollPane để có thể scroll nếu cần
        ScrollPane scrollPane = new ScrollPane(charactersContainer);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPannable(true);

        // Nút quay lại
        Button backButton = new Button();
        SVGPath arrow = new SVGPath();
        arrow.setContent("M 15 8 L 8 15 L 15 22 M 8 15 L 30 15");  // Mũi tên nhỏ hơn
        arrow.setStroke(Color.WHITE);
        arrow.setStrokeWidth(2.5);  // Mảnh hơn
        arrow.setFill(Color.TRANSPARENT);

        backButton.setGraphic(arrow);
        backButton.setStyle("""
        -fx-background-color: rgba(211, 47, 47, 0.8);
        -fx-padding: 8 12;
        -fx-background-radius: 8;
        -fx-cursor: hand;
        """);
        backButton.setEffect(new DropShadow(5, Color.BLACK));

        backButton.setOnMouseEntered(e -> {
            backButton.setStyle("""
            -fx-background-color: rgba(244, 67, 54, 0.9);
            -fx-padding: 8 12;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            """);
            backButton.setScaleX(1.1);
            backButton.setScaleY(1.1);
        });

        backButton.setOnMouseExited(e -> {
            backButton.setStyle("""
            -fx-background-color: rgba(211, 47, 47, 0.8);
            -fx-padding: 8 12;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            """);
            backButton.setScaleX(1.0);
            backButton.setScaleY(1.0);
        });

        backButton.setOnAction(e -> {
            CharacterSelectView characterSelectView = new CharacterSelectView();
            stage.setScene(characterSelectView.getScene());
        });

        // Layout chính
        StackPane  mainLayout = new StackPane ();
        // Nội dung chính
        VBox contentBox = new VBox(50);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));
        contentBox.getChildren().addAll(title, charactersContainer);

        // Thêm nút vào góc trên trái
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20, 0, 0, 20));

        mainLayout.getChildren().addAll(contentBox, backButton);

        // Background
        StackPane root = new StackPane();
        root.setPrefSize(1300, 700);

        URL bgUrl = getClass().getResource("/img/arena/backgroundNhanVat.jpg");
        if (bgUrl != null) {
            root.setBackground(new Background(
                    new BackgroundImage(
                            new Image(bgUrl.toExternalForm()),
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundRepeat.NO_REPEAT,
                            BackgroundPosition.CENTER,
                            new BackgroundSize(1300, 700, false, false, false, true)
                    )
            ));
        } else {
            root.setStyle("-fx-background-color: #1e1e1e;");
        }

        root.getChildren().add(mainLayout);

        return new Scene(root, 1300, 700);
    }

    private VBox createSimpleCharacterCard(Stage stage, String heroType, String imagePath, String displayName) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setMaxWidth(250);
        card.setStyle("""
                -fx-background-color: rgba(0, 0, 0, 0.7);
                -fx-border-color: #FFD700;
                -fx-border-width: 3;
                -fx-border-radius: 15;
                -fx-background-radius: 15;
                -fx-cursor: hand;
                """);
        card.setEffect(new DropShadow(10, Color.GOLD));

        // Hình ảnh nhân vật
        URL imgUrl = getClass().getResource(imagePath);
        if (imgUrl != null) {
            ImageView img = new ImageView(new Image(imgUrl.toExternalForm()));
            if (heroType.equals("Support")) {
                img.setFitWidth(180);
                img.setFitHeight(200);
            } else {
                img.setFitWidth(150);
                img.setFitHeight(200);
            }
            img.setPreserveRatio(true);
            card.getChildren().add(img);
        }

        // Tên nhân vật
        Label nameLabel = new Label(displayName);
        nameLabel.setStyle("""
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                -fx-text-fill: #FFD700;
                -fx-text-alignment: center;
                """);
        nameLabel.setWrapText(true);

        card.getChildren().add(nameLabel);

        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setScaleX(1.08);
            card.setScaleY(1.08);
            card.setEffect(new DropShadow(20, Color.WHITE));
            card.setStyle("""
                    -fx-background-color: rgba(0, 0, 0, 0.85);
                    -fx-border-color: white;
                    -fx-border-width: 3;
                    -fx-border-radius: 15;
                    -fx-background-radius: 15;
                    -fx-cursor: hand;
                    """);
        });

        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            card.setEffect(new DropShadow(10, Color.GOLD));
            card.setStyle("""
                    -fx-background-color: rgba(0, 0, 0, 0.7);
                    -fx-border-color: #FFD700;
                    -fx-border-width: 3;
                    -fx-border-radius: 15;
                    -fx-background-radius: 15;
                    -fx-cursor: hand;
                    """);
        });

        // Click vào để xem chi tiết
        card.setOnMouseClicked(e -> {
            CharacterDetailView detailView = new CharacterDetailView();
            stage.setScene(detailView.getScene(stage, heroType));
        });

        return card;


    }


}
