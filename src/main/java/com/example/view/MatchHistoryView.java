package com.example.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.net.URL;

public class MatchHistoryView {
    public Scene getScene(Stage stage) {
        // Nút quay lại góc trên trái
        Button backButton = createBackButton(stage);

        // Tiêu đề
        Label title = new Label("LỊCH SỬ ĐẤU");
        title.setStyle("""
                -fx-font-size: 36px;
                -fx-font-weight: bold;
                -fx-text-fill: #FFD700;
                """);
        title.setEffect(new DropShadow(10, Color.BLACK));

        // Container cho danh sách trận đấu
        VBox matchList = new VBox(15);
        matchList.setAlignment(Pos.TOP_CENTER);
        matchList.setPadding(new Insets(20));

        // Thêm dữ liệu mẫu (sau này thay bằng dữ liệu thật)
        matchList.getChildren().addAll(
                createMatchRow(true, "/img/character/dausi_phai.png", "24/12 20:56"),
                createMatchRow(true, "/img/character/trothu_phai.png", "06/10 08:53"),
                createMatchRow(false, "/img/character/dausi_phai.png", "21/09 15:01"),
                createMatchRow(true, "/img/character/dausi_phai.png", "20/09 16:41"),
                createMatchRow(false, "/img/character/dausi_phai.png", "19/09 14:22")
        );

        // ScrollPane để cuộn danh sách
        ScrollPane scrollPane = new ScrollPane(matchList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefHeight(500);
        scrollPane.setMaxHeight(500);

        // Layout chính
        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(60, 50, 30, 50));
        contentBox.getChildren().addAll(title, scrollPane);

        // StackPane để đặt nút ở góc
        StackPane mainLayout = new StackPane();
        mainLayout.getChildren().addAll(contentBox, backButton);
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20, 0, 0, 20));

        // Background
        StackPane root = new StackPane();
        root.setPrefSize(1300, 700);

        URL bgUrl = getClass().getResource("/img/arena/backgroundSanDau.png");
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
            root.setStyle("-fx-background-color: #2c3e50;");
        }

        root.getChildren().add(mainLayout);

        return new Scene(root, 1300, 700);
    }

    // Tạo 1 hàng trận đấu
    private HBox createMatchRow(boolean isVictory, String characterPath, String matchTime) {
        HBox row = new HBox(30);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 30, 15, 30));
        row.setPrefWidth(900);
        row.setMaxWidth(900);
        row.setStyle("""
                -fx-background-color: rgba(44, 62, 80, 0.85);
                -fx-border-color: rgba(255, 255, 255, 0.3);
                -fx-border-width: 1;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                """);
        row.setEffect(new DropShadow(8, Color.BLACK));

        // Hình nhân vật
        URL imgUrl = getClass().getResource(characterPath);
        ImageView characterImg = new ImageView();
        if (imgUrl != null) {
            characterImg.setImage(new Image(imgUrl.toExternalForm()));
        }
        characterImg.setFitWidth(70);
        characterImg.setFitHeight(70);
        characterImg.setPreserveRatio(true);

        // Khung cho ảnh
        StackPane imgFrame = new StackPane(characterImg);
        imgFrame.setStyle("""
                -fx-background-color: rgba(0, 0, 0, 0.5);
                -fx-border-color: #FFD700;
                -fx-border-width: 2;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);
        imgFrame.setPadding(new Insets(5));

        // Kết quả VICTORY/DEFEAT
        Label resultLabel = new Label(isVictory ? "VICTORY" : "DEFEAT");
        if (isVictory) {
            resultLabel.setStyle("""
                    -fx-font-size: 28px;
                    -fx-font-weight: bold;
                    -fx-text-fill: #FFD700;
                    """);
        } else {
            resultLabel.setStyle("""
                    -fx-font-size: 28px;
                    -fx-font-weight: bold;
                    -fx-text-fill: #E74C3C;
                    """);
        }
        resultLabel.setEffect(new DropShadow(5, Color.BLACK));
        resultLabel.setPrefWidth(200);

        // Spacer để đẩy thời gian sang phải
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Thời gian
        VBox timeBox = new VBox(5);
        timeBox.setAlignment(Pos.CENTER_RIGHT);

        Label timeTitle = new Label("Đấu hạng");
        timeTitle.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: #BDC3C7;
                """);

        Label timeLabel = new Label(matchTime);
        timeLabel.setStyle("""
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-text-fill: white;
                """);

        timeBox.getChildren().addAll(timeTitle, timeLabel);

        row.getChildren().addAll(imgFrame, resultLabel, spacer, timeBox);

        // Hover effect
        row.setOnMouseEntered(e -> {
            row.setStyle("""
                    -fx-background-color: rgba(52, 73, 94, 0.95);
                    -fx-border-color: #FFD700;
                    -fx-border-width: 2;
                    -fx-border-radius: 10;
                    -fx-background-radius: 10;
                    """);
            row.setScaleX(1.02);
            row.setScaleY(1.02);
        });

        row.setOnMouseExited(e -> {
            row.setStyle("""
                    -fx-background-color: rgba(44, 62, 80, 0.85);
                    -fx-border-color: rgba(255, 255, 255, 0.3);
                    -fx-border-width: 1;
                    -fx-border-radius: 10;
                    -fx-background-radius: 10;
                    """);
            row.setScaleX(1.0);
            row.setScaleY(1.0);
        });

        return row;
    }

    // Tạo nút quay lại
    private Button createBackButton(Stage stage) {
        Button backButton = new Button();

        SVGPath arrow = new SVGPath();
        arrow.setContent("M 15 8 L 8 15 L 15 22 M 8 15 L 30 15");
        arrow.setStroke(Color.WHITE);
        arrow.setStrokeWidth(2.5);
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
            ArenaSelectView arenaView = new ArenaSelectView();
            stage.setScene(arenaView.getScene());
        });

        return backButton;
    }
}
