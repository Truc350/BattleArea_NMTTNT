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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;

public class CharacterSelectView {
    private GameController controller;
    private String arenaPath;
    private Stage stage;

    public CharacterSelectView() {
        this.controller = GameController.getInstance();
    }

    public CharacterSelectView(GameController controller, String arenaPath) {
        this.controller = controller;
        this.arenaPath = arenaPath;
    }

    public CharacterSelectView(String arenaPath) {
    }

    public Scene getScene() {
        Scene scene = createScene();
        scene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
            if (newWindow != null) {
                this.stage = (Stage) newWindow;
            }
        });
        return scene;
    }

    public Scene getScene(Stage stage) {
        this.stage = stage;
        return createScene();
    }

    private Scene createScene() {
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
                    GameController.getInstance().onCharacterSelected(path)
            );

            chars.getChildren().add(frame);
        }

        // ====== NÚT GIỚI THIỆU NHÂN VẬT (NHỎ HỤT) ======
        Button infoButton = new Button("GIỚI THIỆU NHÂN VẬT");
        infoButton.setStyle("""
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-background-color: #2196F3;
                -fx-text-fill: white;
                -fx-padding: 10 20;
                -fx-background-radius: 8;
                -fx-cursor: hand;
                """);
        infoButton.setEffect(new DropShadow(5, Color.BLACK));

        // Hover effect cho nút
        infoButton.setOnMouseEntered(e -> {
            infoButton.setStyle("""
                    -fx-font-size: 14px;
                    -fx-font-weight: bold;
                    -fx-background-color: #42A5F5;
                    -fx-text-fill: white;
                    -fx-padding: 10 20;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    """);
            infoButton.setScaleX(1.05);
            infoButton.setScaleY(1.05);
        });

        infoButton.setOnMouseExited(e -> {
            infoButton.setStyle("""
                    -fx-font-size: 14px;
                    -fx-font-weight: bold;
                    -fx-background-color: #2196F3;
                    -fx-text-fill: white;
                    -fx-padding: 10 20;
                    -fx-background-radius: 8;
                    -fx-cursor: hand;
                    """);
            infoButton.setScaleX(1.0);
            infoButton.setScaleY(1.0);
        });

        // Click vào nút để mở trang giới thiệu - FIX LỖI
        infoButton.setOnAction(e -> {
            // Lấy stage từ scene hiện tại
            Stage currentStage = this.stage;
            if (currentStage == null && infoButton.getScene() != null && infoButton.getScene().getWindow() != null) {
                currentStage = (Stage) infoButton.getScene().getWindow();
            }

            if (currentStage != null) {
                CharacterInfoView infoView = new CharacterInfoView();
                currentStage.setScene(infoView.getScene(currentStage));
            } else {
                System.out.println("❌ Không tìm thấy Stage để chuyển trang!");
            }
        });

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

        // ================== LAYOUT CHÍNH ==================
        // Overlay cho title và chars ở giữa
        VBox centerContent = new VBox(40, title, chars);
        centerContent.setAlignment(Pos.CENTER);

        // Đặt nút ở góc phải dưới
        StackPane.setAlignment(infoButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(infoButton, new Insets(0, 30, 30, 0));

        background.getChildren().addAll(centerContent, infoButton);

        return new Scene(background, 1300, 700);
    }

//    public Scene getScene(Stage stage) {
//        this.stage = stage;
//
//        Label title = new Label("CHỌN NHÂN VẬT");
//        title.setStyle("""
//                        -fx-font-size: 40px;
//                        -fx-font-weight: bold;
//                        -fx-text-fill: white;
//                """);
//        title.setEffect(new DropShadow(8, Color.BLACK));
//
//        HBox chars = new HBox(40);
//        chars.setAlignment(Pos.CENTER);
//
//        String[] characters = {
//                "/img/character/dausi_phai.png",
//                "/img/character/phapsu-phai.png",
//                "/img/character/trothu_phai.png",
//                "/img/character/xathu.png"
//        };
//
//        for (String path : characters) {
//            URL url = getClass().getResource(path);
//            if (url == null) {
//                System.out.println("❌ Không tìm thấy nhân vật: " + path);
//                continue;
//            }
//
//            ImageView img = new ImageView(new Image(url.toExternalForm()));
//
//            if (path.contains("/img/character/trothu_phai.png")) {
//                img.setFitWidth(240);   // rộng hơn
//                img.setFitHeight(260);  // giữ chiều cao
//            } else {
//                img.setFitWidth(200);
//                img.setFitHeight(260);
//            }
//
//            img.setPreserveRatio(true);
//
//            // ====== FRAME BỌC NHÂN VẬT ======
//            StackPane frame = new StackPane(img);
//            frame.setStyle("""
//                            -fx-border-color: white;
//                            -fx-border-width: 2;
//                            -fx-border-radius: 10;
//                            -fx-background-radius: 10;
//                    """);
//
//            // ====== HOVER EFFECT (CHỈ NHÂN VẬT ĐƯỢC CHẠM) ======
//            frame.setOnMouseEntered(e -> {
//                frame.setScaleX(1.1);
//                frame.setScaleY(1.1);
//                frame.setTranslateY(-12);
//                frame.setEffect(new DropShadow(20, Color.WHITE));
//            });
//
//            frame.setOnMouseExited(e -> {
//                frame.setScaleX(1.0);
//                frame.setScaleY(1.0);
//                frame.setTranslateY(0);
//                frame.setEffect(null);
//            });
//
//            // ====== CLICK → VÀO GAME ======
//            frame.setOnMouseClicked(e ->
//                    GameController.getInstance().onCharacterSelected(path)
//            );
//
//            chars.getChildren().add(frame);
//        }
//
//        // ====== NÚT GIỚI THIỆU NHÂN VẬT ======
//        Button infoButton = new Button("GIỚI THIỆU NHÂN VẬT");
//        infoButton.setStyle("""
//                -fx-font-size: 18px;
//                -fx-font-weight: bold;
//                -fx-background-color: #2196F3;
//                -fx-text-fill: white;
//                -fx-padding: 15 30;
//                -fx-background-radius: 10;
//                -fx-cursor: hand;
//                """);
//        infoButton.setEffect(new DropShadow(5, Color.BLACK));
//
//        // Hover effect cho nút
//        infoButton.setOnMouseEntered(e -> {
//            infoButton.setStyle("""
//                    -fx-font-size: 18px;
//                    -fx-font-weight: bold;
//                    -fx-background-color: #42A5F5;
//                    -fx-text-fill: white;
//                    -fx-padding: 15 30;
//                    -fx-background-radius: 10;
//                    -fx-cursor: hand;
//                    """);
//            infoButton.setScaleX(1.05);
//            infoButton.setScaleY(1.05);
//        });
//
//        infoButton.setOnMouseExited(e -> {
//            infoButton.setStyle("""
//                    -fx-font-size: 18px;
//                    -fx-font-weight: bold;
//                    -fx-background-color: #2196F3;
//                    -fx-text-fill: white;
//                    -fx-padding: 15 30;
//                    -fx-background-radius: 10;
//                    -fx-cursor: hand;
//                    """);
//            infoButton.setScaleX(1.0);
//            infoButton.setScaleY(1.0);
//        });
//
//        // Click vào nút để mở trang giới thiệu
//        infoButton.setOnAction(e -> {
//            if (this.stage != null) {
//                CharacterInfoView infoView = new CharacterInfoView();
//                this.stage.setScene(infoView.getScene(this.stage));
//            }
//        });
//
//        StackPane background = new StackPane();
//        background.setPrefSize(1300, 700);
//
//        URL bgUrl = getClass().getResource("/img/arena/backgroundNhanVat.jpg");
//        if (bgUrl != null) {
//            background.setBackground(new Background(
//                    new BackgroundImage(
//                            new Image(bgUrl.toExternalForm()),
//                            BackgroundRepeat.NO_REPEAT,
//                            BackgroundRepeat.NO_REPEAT,
//                            BackgroundPosition.CENTER,
//                            new BackgroundSize(
//                                    1300, 700,
//                                    false, false,
//                                    false, true
//                            )
//                    )
//            ));
//        } else {
//            background.setStyle("-fx-background-color:#1e1e1e;");
//        }
//
//        // ================== OVERLAY ==================
//        VBox overlay = new VBox(40, title, chars, infoButton);
//        overlay.setAlignment(Pos.CENTER);
//        overlay.setPadding(Insets.EMPTY);
//
//        background.getChildren().add(overlay);
//
//        return new Scene(background, 1300, 700);
//    }
}