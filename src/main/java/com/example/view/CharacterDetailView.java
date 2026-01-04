package com.example.view;

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
import javafx.stage.Stage;
import javafx.scene.shape.SVGPath;
import java.net.URL;

public class CharacterDetailView {

    public Scene getScene(Stage stage, String heroType) {
        // Lấy thông tin nhân vật
        CharacterInfo info = getCharacterInfo(heroType);

        StackPane root = new StackPane();
        root.setPrefSize(1300, 700);

        // Set background theo nhân vật
        setBackgroundForHero(root, heroType);

        StackPane mainLayout;  // Đổi từ VBox sang StackPane
        if (heroType.equals("Fighter")) {
            mainLayout = createFighterLayout(stage, info, heroType);
        } else {
            mainLayout = createStandardLayout(stage, info, heroType);
        }

        root.getChildren().add(mainLayout);

        return new Scene(root, 1300, 700);
    }

    private CharacterInfo getCharacterInfo(String heroType) {
        return switch (heroType) {
            case "Fighter" -> new CharacterInfo(
                    "FIGHTER - CHIẾN BINH",
                    "HP: 100  |  MP: 100\nAttack: 8  |  Defense: 10",
                    "✨ Cân bằng, dễ chơi, sống dai",
                    "1. Basic Attack\n   • MP: 0  |  CD: 0s  |  Damage: 8\n   • Đòn tấn công cơ bản\n\n" +
                            "2. Mana Regen\n   • MP: 0  |  CD: 3s  |  Hồi: +10 HP, +15 MP\n   • Kỹ năng hồi phục\n\n" +
                            "3. Rage Strike\n   • MP: 10  |  CD: 4s  |  Damage: 14\n   • Đòn giận dữ mạnh mẽ",
                    "4. Fury Burst\n   • MP: 15  |  CD: 6s  |  Damage: 20\n   • Bùng nổ cuồng nộ\n\n" +
                            "5. Ultimate Rage\n   • MP: 22  |  CD: 10s  |  Damage: 14 (x1.8 attack)\n   • Cơn thịnh nộ tối thượng"
            );

            case "Marksman" -> new CharacterInfo(
                    "MARKSMAN - XẠ THỦ",
                    "HP: 100  |  MP: 100\nAttack: 10  |  Defense: 5\nCrit Rate: 30% (x2 dmg)",
                    "✨ Damage cao nhất, yếu\n★ Có yếu tố RNG (crit)",
                    "1. Basic Attack\n   • MP: 0  |  CD: 0s  |  Dmg: 10 | Crit: 20\n   • Tấn công có crit\n\n" +
                            "2. Mana Regen\n   • MP: 0  |  CD: 3s  |  Hồi: +10 HP, +15 MP\n   • Kỹ năng hồi phục\n\n" +
                            "3. Precision Shot\n   • MP: 6  |  CD: 3s  |  Dmg: 15 | Crit: 30\n   • Bắn chính xác",
                    "4. Snipe\n   • MP: 12  |  CD: 6s  |  Dmg: 17 | Crit: 34\n   • Bắn tỉa từ xa\n\n" +
                            "5. Deadly Arrow\n   • MP: 20  |  CD: 10s  |  Dmg: 25 | Crit: 50\n   • Mũi tên chết chóc\n   • Crit 50 damage!"
            );

            case "Mage" -> new CharacterInfo(
                    "MAGE - PHÁP SƯ",
                    "HP: 100  |  MP: 100\nAttack: 5  |  Defense: 10",
                    "✨ Sustain tốt với MP regen\n✨ Damage ổn định",
                    "1. Basic Attack\n   • MP: 0  |  CD: 0s  |  Damage: 5\n   • Tấn công phép thuật\n\n" +
                            "2. Mana Regen\n   • MP: 0  |  CD: 3s  |  Hồi: +10 HP, +15 MP\n   • Kỹ năng hồi phục\n\n" +
                            "3. Fireball\n   • MP: 8  |  CD: 3s  |  Dmg: 8 | +4 MP\n   • Cầu lửa hồi mana",
                    "4. Lightning Bolt\n   • MP: 13  |  CD: 6s  |  Damage: 9\n   • Tia sét mạnh mẽ\n\n" +
                            "5. Meteor Strike\n   • MP: 25  |  CD: 10s  |  Dmg: 7 | +8 MP\n   • Thiên thạch hủy diệt\n   • 💡 Sustain cực mạnh"
            );

            case "Support" -> new CharacterInfo(
                    "SUPPORT - TRỢ THỦ",
                    "HP: 100  |  MP: 100\nAttack: 5  |  Defense: 15",
                    "✨ Sống dai nhất\n✨ Heal mạnh, damage thấp",
                    "1. Basic Attack\n   • MP: 0  |  CD: 0s  |  Damage: 5\n   • Tấn công nhẹ nhàng\n\n" +
                            "2. Mana Regen\n   • MP: 0  |  CD: 3s  |  Hồi: +10 HP, +15 MP\n   • Kỹ năng hồi phục\n\n" +
                            "3. Heal Wave\n   • MP: 15  |  CD: 7s  |  Hồi: +25 HP, +10 MP\n   • Làn sóng hồi phục",
                    "4. Group Shield\n   • MP: 18  |  CD: 9s  |  Hồi: +20 MP\n   • Lá chắn nhóm\n\n" +
                            "5. Revive\n   • MP: 40  |  CD: 30s  |  Hồi: +50 HP, +30 MP\n   • Hồi sinh kỳ diệu\n   • 💚 Skill cứu cánh"
            );

            default -> new CharacterInfo("Unknown", "", "", "", "");
        };
    }

    private void setBackgroundForHero(StackPane root, String heroType) {
        String backgroundPath = switch (heroType) {
            case "Fighter" -> "/img/arena/backgroundDauSi.png";
            case "Mage" -> "/img/arena/backgroundPhapSu.png";
            case "Marksman" -> "/img/arena/backgroundXaThu.png";
            case "Support" -> "/img/arena/backgroundTroThu.png";
            default -> "/img/arena/backgroundNhanVat.jpg";
        };

        URL bgUrl = getClass().getResource(backgroundPath);
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
    }

    private StackPane createStandardLayout(Stage stage, CharacterInfo info, String heroType) {
        StackPane mainLayout = new StackPane();  // Đổi từ VBox sang StackPane

        // Nút mũi tên ở góc trên trái
        Button backButton = createBackButton(stage);
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20, 0, 0, 20));

        // Nội dung chính
        VBox contentBox = new VBox(20);  // Tăng spacing
        contentBox.setAlignment(Pos.CENTER);

        // Tiêu đề
        Label title = new Label(info.title);
        title.setStyle("""
            -fx-font-size: 32px;
            -fx-font-weight: bold;
            -fx-text-fill: #FFD700;
            """);
        title.setEffect(new DropShadow(10, Color.BLACK));

        // Layout chính: INFO (trái) + NHÂN VẬT (giữa) + SKILL (phải)
        HBox mainContent = new HBox(80);  // Tăng từ 40 → 80
        mainContent.setAlignment(Pos.CENTER);

        // Thông tin bên trái
        VBox infoBox = createInfoBox(info);
        infoBox.setMaxWidth(300);

        // Nhân vật ở giữa - XÍCH XUỐNG
        VBox characterBox = createCharacterImage(heroType);
        characterBox.setTranslateY(30);  // Xích xuống 30px

        // Kỹ năng bên phải
        VBox skillsBox = createSkillsBox(info);
        skillsBox.setMaxWidth(400);

        mainContent.getChildren().addAll(infoBox, characterBox, skillsBox);

        contentBox.getChildren().addAll(title, mainContent);

        mainLayout.getChildren().addAll(contentBox, backButton);

        return mainLayout;
    }

    private StackPane createFighterLayout(Stage stage, CharacterInfo info, String heroType) {
        StackPane mainLayout = new StackPane();  // Đổi từ VBox sang StackPane

        // Nút mũi tên ở góc trên trái
        Button backButton = createBackButton(stage);
        StackPane.setAlignment(backButton, Pos.TOP_LEFT);
        StackPane.setMargin(backButton, new Insets(20, 0, 0, 20));

        // Nội dung chính
        VBox contentBox = new VBox(5);  // Tăng spacing
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(5, 20, 5, 20));  // Thêm padding

        // Tiêu đề
        Label title = new Label(info.title);
        title.setStyle("""
            -fx-font-size: 26px;
            -fx-font-weight: bold;
            -fx-text-fill: #FFD700;
            """);
        title.setEffect(new DropShadow(10, Color.BLACK));

        // Phần trên: CHỈ SỐ CƠ BẢN + ĐẶC ĐIỂM
        HBox topInfo = new HBox(20);
        topInfo.setAlignment(Pos.CENTER);

        VBox infoBox = createCompactInfoBox(info);

        topInfo.getChildren().add(infoBox);

        // Phần giữa: KỸ NĂNG (trái) + NHÂN VẬT (giữa)
        HBox middleContent = new HBox(50);
        middleContent.setAlignment(Pos.CENTER);

        // Kỹ năng bên trái
        VBox skillsBox = createCompactSkillsBox(info);
        skillsBox.setMaxWidth(350);
        skillsBox.setTranslateY(-10);

        // Nhân vật ở giữa
        VBox characterBox = createCharacterImage(heroType);
        characterBox.setTranslateY(10);

        middleContent.getChildren().addAll(skillsBox, characterBox);

        contentBox.getChildren().addAll(title, topInfo, middleContent);

        mainLayout.getChildren().addAll(contentBox, backButton);

        return mainLayout;  // Trả về StackPane nhưng kiểu VBox
    }

    private Button createBackButton(Stage stage) {
        Button backButton = new Button();

        // Tạo mũi tên SVG
        SVGPath arrow = new SVGPath();
        arrow.setContent("M 15 8 L 8 15 L 15 22 M 8 15 L 30 15");  // Nhỏ hơn
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
            CharacterInfoView infoView = new CharacterInfoView();
            stage.setScene(infoView.getScene(stage));
        });

        return backButton;
    }

    private VBox createCharacterImage(String heroType) {
        VBox characterBox = new VBox();
        characterBox.setAlignment(Pos.CENTER);

        String imagePath = switch (heroType) {
            case "Fighter" -> "/img/character/dausi_phai.png";
            case "Mage" -> "/img/character/phapsu-phai.png";
            case "Marksman" -> "/img/character/xathu.png";
            case "Support" -> "/img/character/trothu_phai.png";
            default -> "";
        };

        URL imgUrl = getClass().getResource(imagePath);
        if (imgUrl != null) {
            ImageView img = new ImageView(new Image(imgUrl.toExternalForm()));

            // Kích thước lớn hơn cho nhân vật ở giữa
            if (heroType.equals("Support")) {
                img.setFitWidth(350);
                img.setFitHeight(400);
            } else {
                img.setFitWidth(300);
                img.setFitHeight(400);
            }

            img.setPreserveRatio(true);
            characterBox.getChildren().add(img);
        }

        return characterBox;
    }

    private VBox createSkillsBox(CharacterInfo info) {
        VBox skillsBox = new VBox(10);
        skillsBox.setAlignment(Pos.TOP_LEFT);

        Label skillsTitle = new Label("KỸ NĂNG");
        skillsTitle.setStyle("""
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                -fx-text-fill: #FF6347;
                """);
        skillsTitle.setEffect(new DropShadow(5, Color.BLACK));

        // Gộp tất cả kỹ năng
        String allSkills = info.skills1 + "\n\n" + info.skills2;
        Label skills = new Label(allSkills);
        skills.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-line-spacing: 2;
                """);
        skills.setEffect(new DropShadow(3, Color.BLACK));
        skills.setWrapText(true);

        skillsBox.getChildren().addAll(skillsTitle, skills);

        return skillsBox;
    }

    // Hàm riêng cho Fighter
    private VBox createCompactSkillsBox(CharacterInfo info) {
        VBox skillsBox = new VBox(6);  // Spacing nhỏ hơn
        skillsBox.setAlignment(Pos.TOP_LEFT);

        Label skillsTitle = new Label("KỸ NĂNG");
        skillsTitle.setStyle("""
            -fx-font-size: 19px;
            -fx-font-weight: bold;
            -fx-text-fill: #FF6347;
            """);
        skillsTitle.setEffect(new DropShadow(5, Color.BLACK));

        // Gộp tất cả kỹ năng
        String allSkills = info.skills1 + "\n\n" + info.skills2;
        Label skills = new Label(allSkills);
        skills.setStyle("""
            -fx-font-size: 13px;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-line-spacing: 1;
            """);
        skills.setEffect(new DropShadow(3, Color.BLACK));
        skills.setWrapText(true);

        skillsBox.getChildren().addAll(skillsTitle, skills);

        return skillsBox;
    }

    private VBox createInfoBox(CharacterInfo info) {
        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.TOP_CENTER);

        // Chỉ số cơ bản
        Label statsTitle = new Label("CHỈ SỐ CƠ BẢN");
        statsTitle.setStyle("""
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-text-fill: #00BFFF;
                """);
        statsTitle.setEffect(new DropShadow(5, Color.BLACK));

        Label stats = new Label(info.stats);
        stats.setStyle("""
                -fx-font-size: 15px;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-line-spacing: 3;
                -fx-text-alignment: center;
                """);
        stats.setEffect(new DropShadow(3, Color.BLACK));
        stats.setWrapText(true);
        stats.setMaxWidth(300);

        // Đường phân cách
        Region separator = new Region();
        separator.setPrefHeight(2);
        separator.setMaxWidth(280);
        separator.setStyle("-fx-background-color: #FFD700;");

        // Đặc điểm
        Label traitTitle = new Label("ĐẶC ĐIỂM");
        traitTitle.setStyle("""
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                -fx-text-fill: #FFA500;
                """);
        traitTitle.setEffect(new DropShadow(5, Color.BLACK));

        Label trait = new Label(info.trait);
        trait.setStyle("""
                -fx-font-size: 15px;
                -fx-text-fill: #FFD700;
                -fx-font-weight: bold;
                -fx-line-spacing: 3;
                -fx-text-alignment: center;
                """);
        trait.setEffect(new DropShadow(3, Color.BLACK));
        trait.setWrapText(true);
        trait.setMaxWidth(300);

        infoBox.getChildren().addAll(statsTitle, stats, separator, traitTitle, trait);

        return infoBox;
    }

    // Hàm riêng cho Fighter - GỌN HƠN
    private VBox createCompactInfoBox(CharacterInfo info) {
        VBox infoBox = new VBox(6);  // Spacing nhỏ hơn
        infoBox.setAlignment(Pos.TOP_CENTER);

        // Chỉ số cơ bản
        Label statsTitle = new Label("CHỈ SỐ CƠ BẢN");
        statsTitle.setStyle("""
            -fx-font-size: 17px;
            -fx-font-weight: bold;
            -fx-text-fill: #00BFFF;
            """);
        statsTitle.setEffect(new DropShadow(5, Color.BLACK));

        Label stats = new Label(info.stats);
        stats.setStyle("""
            -fx-font-size: 14px;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-line-spacing: 1.5;
            -fx-text-alignment: center;
            """);
        stats.setEffect(new DropShadow(3, Color.BLACK));
        stats.setWrapText(true);
        stats.setMaxWidth(400);  // Rộng hơn

        // Đường phân cách
        Region separator = new Region();
        separator.setPrefHeight(2);
        separator.setMaxWidth(320);
        separator.setStyle("-fx-background-color: #FFD700;");

        // Đặc điểm
        Label traitTitle = new Label("ĐẶC ĐIỂM");
        traitTitle.setStyle("""
            -fx-font-size: 17px;
            -fx-font-weight: bold;
            -fx-text-fill: #FFA500;
            """);
        traitTitle.setEffect(new DropShadow(5, Color.BLACK));

        Label trait = new Label(info.trait);
        trait.setStyle("""
            -fx-font-size: 14px;
            -fx-text-fill: #FFD700;
            -fx-font-weight: bold;
            -fx-line-spacing: 1.5;
            -fx-text-alignment: center;
            """);
        trait.setEffect(new DropShadow(3, Color.BLACK));
        trait.setWrapText(true);
        trait.setMaxWidth(400);

        infoBox.getChildren().addAll(statsTitle, stats, separator, traitTitle, trait);

        return infoBox;
    }




    // Class để lưu thông tin nhân vật
    private static class CharacterInfo {
        String title;
        String stats;
        String trait;
        String skills1; // Kỹ năng 1, 2, 3
        String skills2; // Kỹ năng 4, 5

        CharacterInfo(String title, String stats, String trait, String skills1, String skills2) {
            this.title = title;
            this.stats = stats;
            this.trait = trait;
            this.skills1 = skills1;
            this.skills2 = skills2;
        }
    }
}
