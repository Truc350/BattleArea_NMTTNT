package com.example.model;

import java.util.Scanner;

public class Player {
    private Hero hero;

    public Player(Hero hero) {
        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }

    public void chooseHero(Hero hero) {
        this.hero = hero;

    }

    public void selectPlayerHero(Point position) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== CHỌN TƯỚNG CHO NGƯỜI CHƠI ===");
        System.out.println("1. Fighter (Chiến binh - Attack 20, Defense 15)");
        System.out.println("2. Marksman (Xạ thủ - Attack 25, chí mạng cao)");
        System.out.println("3. Mage (Pháp sư - Attack 18, MP mạnh)");
        System.out.println("4. Support (Hỗ trợ - Heal tốt, Defense 12)");
        System.out.print("Nhập lựa chọn (1-4): ");
        int choice = 0;
        while (choice < 1 || choice > 4) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 1 || choice > 4){
                    System.out.println("Sai! Nhập lại (1-4): ");
                }
            }catch ( NumberFormatException e){
                System.out.println("Phải nhập số! Nhập lại: ");
            }
        }
        HeroType type = HeroType.values()[choice-1];
        this.hero = Hero.getHero(type, type.name(), position);
        System.out.println("✅ Đã chọn: " + hero.getName() + " | HP: " + hero.getHp() + " | MP: " + hero.getMp() + " | Attack: " + hero.attack);
    }
}
