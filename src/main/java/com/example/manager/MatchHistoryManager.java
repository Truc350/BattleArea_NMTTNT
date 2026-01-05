package com.example.manager;

import com.example.model.MatchHistory;

import java.util.ArrayList;
import java.util.List;

public class MatchHistoryManager {
    private static MatchHistoryManager instance;
    private List<MatchHistory> matches;

    private MatchHistoryManager() {
        matches = new ArrayList<>();
    }

    public static MatchHistoryManager getInstance() {
        if (instance == null) {
            instance = new MatchHistoryManager();
        }
        return instance;
    }

    public void addMatch(MatchHistory match) {
        matches.add(0, match); // Thêm vào đầu danh sách (mới nhất trên cùng)
        System.out.println("✅ Đã lưu lịch sử trận đấu: " +
                (match.isVictory() ? "VICTORY" : "DEFEAT") +
                " - " + match.getFormattedTime());
    }

    public List<MatchHistory> getAllMatches() {
        return new ArrayList<>(matches); // Return copy để tránh modification
    }

    public int getTotalMatches() {
        return matches.size();
    }

    public int getWins() {
        return (int) matches.stream().filter(MatchHistory::isVictory).count();
    }

    public int getLosses() {
        return getTotalMatches() - getWins();
    }

    public void clearHistory() {
        matches.clear();
        System.out.println("🗑️ Đã xóa toàn bộ lịch sử trận đấu");
    }

}
