package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer extends Fighter {   // ← BẮT BUỘC extends Fighter (hoặc Marksman nếu muốn chí mạng)

    private static final int MAX_DEPTH = 7;        // đủ sâu để tính trước 3-4 lượt
    private boolean useAlphaBeta = true;

    public AIPlayer(String name, int maxHP, int maxMP, Point position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }


    public String chooseBestAction(long currentTime, Hero opponent, Game game) {
        if (!game.isRange()) {
            return "Move Closer";
        }

        GameState root = new GameState(deepCopy(this), deepCopy(opponent), currentTime);
        List<GameState> children = generateSuccessors(root, true);

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        for (GameState child : children) {
            int score = alphaBeta(child, MAX_DEPTH - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);

            if (score > bestScore) {
                bestScore = score;
                bestMove = child.getMove();
            }
        }

        // Log để bạn thấy AI đang "suy nghĩ" gì
        System.out.println("AI chọn: " + bestMove.getName() + " | Điểm dự đoán: " + bestScore);

        // Thực hiện thật
        if (!"Basic Attack".equals(bestMove.getName())) {
            useSkill(bestMove.getName(), currentTime, opponent);
        } else {
            opponent.takeDamage(this.attack);
        }
        return bestMove.getName();
    }

    // ======================= ALPHA-BETA SIÊU TỐI ƯU =======================
    private int alphaBeta(GameState state, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (depth == 0 || state.isTerminal()) {
            return evaluate(state);
        }

        List<GameState> children = generateSuccessors(state, maximizingPlayer);

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (GameState child : children) {
                int eval = alphaBeta(child, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) break; // cắt tỉa cực mạnh
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (GameState child : children) {
                int eval = alphaBeta(child, depth - 1, true, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, minEval);
                if (beta <= alpha) break;
            }
            return minEval;
        }
    }

    // ======================= TẠO NHÁNH CON – 100% SẠCH BUG =======================
    private List<GameState> generateSuccessors(GameState state, boolean maxPlayer) {
        List<GameState> list = new ArrayList<>();
        Hero aiBase = state.aiHero;
        Hero playerBase = state.playerHero;
        long nextTime = state.time + 1000;

        Hero current = maxPlayer ? aiBase : playerBase;
        Hero target  = maxPlayer ? playerBase : aiBase;

        // 1. Basic Attack
        {
            Hero aiCopy = deepCopy(aiBase);
            Hero plCopy = deepCopy(playerBase);
            (maxPlayer ? plCopy : aiCopy).takeDamage((maxPlayer ? aiCopy : plCopy).getAttack());
            list.add(new GameState(aiCopy, plCopy, nextTime, new Move("Basic Attack", (maxPlayer ? aiCopy : plCopy).getAttack(), (int)nextTime)));
        }

        // 2. Tất cả skill có thể dùng (kể cả Mana Regen)
        for (Skill s : current.getSkills()) {
            if (s.canUse(state.time, current.getMp())) {
                Hero aiCopy = deepCopy(aiBase);
                Hero plCopy = deepCopy(playerBase);
                Hero user = maxPlayer ? aiCopy : plCopy;
                Hero targ = maxPlayer ? plCopy : aiCopy;

                if (user.useSkill(s.getName(), state.time, targ)) {
                    list.add(new GameState(aiCopy, plCopy, nextTime, new Move(s.getName(), s.getDamage(), (int)nextTime)));
                }
            }
        }
        return list;
    }

    // ======================= HÀM ĐÁNH GIÁ – CỰC KỲ KHÔN =======================
    private int evaluate(GameState s) {
        Hero ai = s.aiHero;
        Hero pl = s.playerHero;

        if (ai.getHp() <= 0)  return -5000000;
        if (pl.getHp() <= 0)  return  5000000;

        int score = 0;

        // HP là vua
        score += ai.getHp() * 40;
        score -= pl.getHp() * 50;

        // Mana càng nhiều càng ngon
        score += ai.getMp() * 10;
        score -= pl.getMp() * 6;

        // Ưu tiên GIẾT NGAY khi có cơ hội
        if (pl.getHp() <= 40) score += 3000;
        if (pl.getHp() <= 20) score += 8000;
        if (pl.getHp() <= 10) score += 20000;

        // Tránh chết
        if (ai.getHp() <= 30) score -= 5000;
        if (ai.getHp() <= 15) score -= 15000;

        // Ưu tiên skill siêu mạnh
        if (ai.getMp() >= 22) score += 2000; // đủ Ultimate Rage
        if (ai.getMp() >= 30) score += 4000;

        return score;
    }

    // deepCopy – vẫn dùng cách cũ của bạn, đã ổn
    private Hero deepCopy(Hero original) {
        Hero copy = Hero.getHero(HeroType.FIGHTER, original.getName(), original.getPosition());
        copy.setHp(original.getHp());
        copy.setMp(original.getMp());
        copy.getSkills().clear();
        for (Skill sk : original.getSkills()) {
            Skill ns = new Skill(sk.getName(), sk.getMpCost(), sk.getCooldown(),
                    sk.getDamage(), sk.getHealHP(), sk.getHealMP());
            ns.setLastUsedTime(sk.getLastUsedTime());
            copy.getSkills().add(ns);
        }
        return copy;
    }
}