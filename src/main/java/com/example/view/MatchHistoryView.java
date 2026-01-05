package com.example.view;

import com.example.manager.MatchHistoryManager;
import com.example.model.MatchHistory;
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
import java.util.List;

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

        // Thống kê
        MatchHistoryManager manager = MatchHistoryManager.getInstance();
        Label statsLabel = new Label(String.format("Tổng: %d trận\nThắng: %d\nThua: %d",
                manager.getTotalMatches(), manager.getWins(), manager.getLosses()));
        statsLabel.setStyle("""
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-text-fill: #ECF0F1;
                -fx-line-spacing: 5;
                """);
        statsLabel.setMinHeight(85);
        statsLabel.setEffect(new DropShadow(5, Color.BLACK));

        // Container cho danh sách trận đấu
        VBox matchList = new VBox(15);
        matchList.setAlignment(Pos.TOP_CENTER);
        matchList.setPadding(new Insets(20));

        // Lay DL that
        List<MatchHistory> matches = manager.getAllMatches();

        if(matches.isEmpty()){
            Label emptyLabel = new Label("Chưa có trận đấu nào\n\nHãy bắt đầu một trận đấu!");
            emptyLabel.setStyle("""
                    -fx-font-size: 24px;
                    -fx-text-fill: #BDC3C7;
                    -fx-text-alignment: center;
                    """);
            emptyLabel.setEffect(new DropShadow(5, Color.BLACK));
            matchList.getChildren().add(emptyLabel);
        } else {
            // Thêm các trận đấu thật
            for (MatchHistory match : matches) {
                matchList.getChildren().add(createMatchRow(match));
            }
        }

        // ScrollPane để cuộn danh sách
        ScrollPane scrollPane = new ScrollPane(matchList);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPrefHeight(450);
        scrollPane.setMaxHeight(450);

        // Layout chính
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(60, 50, 30, 50));
        contentBox.getChildren().addAll(title, statsLabel, scrollPane);

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
    private HBox createMatchRow(MatchHistory match) {
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

        // ===== 1. PLAYER IMAGE =====
        ImageView playerImg = createCharacterImage(match.getCharacterPath(), 60);
        StackPane playerFrame = createImageFrame(playerImg);

        // ===== 2. VS LABEL =====
        Label vsLabel = new Label("VS");
        vsLabel.setStyle("""
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-text-fill: #FF6347;
                """);
        vsLabel.setEffect(new DropShadow(5, Color.BLACK));

        // ===== 3. AI IMAGE =====
        ImageView aiImg = createCharacterImage(match.getEnemyCharacterPath(), 60);
        StackPane aiFrame = createImageFrame(aiImg);

        // ===== 4. NGƯỜI THẮNG =====
        VBox winnerBox = new VBox(3);
        winnerBox.setAlignment(Pos.CENTER);
        winnerBox.setPrefWidth(150);

        Label winnerLabel;
        if (match.isVictory()) {
            winnerLabel = new Label("PLAYER THẮNG");
            winnerLabel.setStyle("""
                    -fx-font-size: 18px;
                    -fx-font-weight: bold;
                    -fx-text-fill: #FFD700;
                    -fx-text-alignment: center;
                    """);
        } else {
            winnerLabel = new Label("AI THẮNG");
            winnerLabel.setStyle("""
                    -fx-font-size: 18px;
                    -fx-font-weight: bold;
                    -fx-text-fill: #E74C3C;
                    -fx-text-alignment: center;
                    """);
        }
        winnerLabel.setEffect(new DropShadow(5, Color.BLACK));
        winnerBox.getChildren().add(winnerLabel);

        winnerLabel.setAlignment(Pos.CENTER);
        winnerBox.setAlignment(Pos.CENTER);

        // Spacer để đẩy thời gian sang phải
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Thong tin chi tiet
        VBox inforBox = new VBox(5);
        inforBox.setAlignment(Pos.CENTER_RIGHT);

        Label modelLabel = new Label("Đấu hạng");
        modelLabel.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: #BDC3C7;
                """);

        Label timeLabel = new Label(match.getFormattedTime());
        timeLabel.setStyle("""
                -fx-font-size: 16px;
                -fx-font-weight: bold;
                -fx-text-fill: white;
                """);

        Label hpLabel = new Label(String.format("HP: %d vs %d",
                match.getPlayerFinalHP(), match.getEnemyFinalHP()));
        hpLabel.setStyle("""
                -fx-font-size: 13px;
                -fx-text-fill: #95A5A6;
                """);

        inforBox.getChildren().addAll(modelLabel, timeLabel, hpLabel);

        row.getChildren().addAll(playerFrame, vsLabel, aiFrame, winnerBox, spacer, inforBox);

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

    private ImageView createCharacterImage(String path, int size) {
        ImageView img = new ImageView();
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            img.setImage(new Image(imgUrl.toExternalForm()));
        }
        img.setFitWidth(size);
        img.setFitHeight(size);
        img.setPreserveRatio(true);
        return img;
    }

    private StackPane createImageFrame(ImageView img) {
        StackPane frame = new StackPane(img);
        frame.setStyle("""
                -fx-background-color: rgba(0, 0, 0, 0.5);
                -fx-border-color: #FFD700;
                -fx-border-width: 2;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                """);
        frame.setPadding(new Insets(5));
        return frame;
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
