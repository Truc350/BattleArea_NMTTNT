package com.example.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MatchHistory {
    private boolean isVictory;
    private String characterPath;
    private LocalDateTime matchTime;
    private String playerName;
    private String enemyName;
    private int playerFinalHP;
    private int enemyFinalHP;

    public MatchHistory(boolean isVictory, String characterPath, LocalDateTime matchTime, String playerName, String enemyName, int enemyFinalHP, int playerFinalHP) {
        this.isVictory = isVictory;
        this.characterPath = characterPath;
        this.matchTime = matchTime;
        this.playerName = playerName;
        this.enemyName = enemyName;
        this.enemyFinalHP = enemyFinalHP;
        this.playerFinalHP = playerFinalHP;
    }

    public boolean isVictory() {
        return isVictory;
    }

    public String getCharacterPath() {
        return characterPath;
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        return matchTime.format(formatter);
    }

    public LocalDateTime getMatchTime() {
        return matchTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getEnemyName() {
        return enemyName;
    }

    public int getPlayerFinalHP() {
        return playerFinalHP;
    }

    public int getEnemyFinalHP() {
        return enemyFinalHP;
    }
}
