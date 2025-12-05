package com.example.model;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer extends Hero {
    private static final int MAX_DEPTH = 5;
    private boolean useAlphaBeta = true;

    public AIPlayer(String name, int maxHP, int maxMP, double position, int attack, int defense) {
        super(name, maxHP, maxMP, position, attack, defense);
    }

    @Override
    public void initSkills() {
        super.initSkills();
        skills.add(new Skill("Rage Strike", 15, 8, attack + 10, 0, 0));
        skills.add(new Skill("Fury Burst", 20, 12, attack + 20, 0, 0));
        skills.add(new Skill("Ultimate Rage", 30, 20, attack * 2, 0, 0));
    }


    public String chooseBestAction(long currentTime, Hero opponent, Game game) {
        if (!game.isRange()) {
            System.out.println("ai dang di chuyển lại gần đối thủ");
            return "Move Closer";
        }

        GameState root = new GameState(deepCopy(this), deepCopy(opponent), currentTime);
        List<GameState> children = generateSuccessors(root, true); // true = AI lượt
        children.sort((a, b) -> Integer.compare(b.damageDealt, a.damageDealt));
        int bestScore = Integer.MIN_VALUE;
        String bestMove = "Basic Attack";

        for (GameState child : children) {
            int score;
            if (useAlphaBeta) {
                score = alphaBeta(child, MAX_DEPTH - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            } else {
                score = minimax(false, child, MAX_DEPTH - 1); // false = lượt Player (MIN)
            }

            if (score > bestScore) {
                bestScore = score;
                bestMove = child.moveName;
            }
        }

        System.out.println("AI Chọn: " + bestMove +
                " (điểm: " + bestScore + ", thuật toán: " +
                (useAlphaBeta ? "Alpha-Beta" : "Minimax") + ")");

        if (!bestMove.equals("Basic Attack")) {
            useSkill(bestMove, currentTime, opponent);
        } else {
            opponent.takeDamage(attack);
        }
        return bestMove;
    }

    // minimax
    private int minimax(boolean maxmin, GameState state, int depth) {
        if (depth == 0 || state.isTerminal()) {
            return evaluate(state); // Trả về điểm đánh giá
        }
        if (maxmin == true) {
            int bestValue = -999999999;
            // Duyệt tất cả trạng thái con hợp lệ
            for (GameState newState : generateSuccessors(state, true)) {
                int value = minimax(false, newState, depth - 1);
                if (value > bestValue) {
                    bestValue = value;
                    // Ghi lại trạng thái/nước đi tốt nhất nếu cần . Để tại chỗ này: node global lưu cái move là gì (đên, attack,...)
                    // minimax , tạo class trung gian:
//                     System.out.println("MAX: " + newState.moveName + " = " + value);
                }
            }
            return bestValue;
        }
        // --- Lượt MIN (Player muốn điểm thấp nhất) ---
        else {
            int bestValue = 999999999;
            // Duyệt tất cả trạng thái con hợp lệ
            for (GameState newState : generateSuccessors(state, false)) {
                int value = minimax(true, newState, depth - 1);
                if (value < bestValue) {
                    bestValue = value;
                    // Ghi lại trạng thái/nước đi tốt nhất nếu cần
                    // System.out.println("MIN: " + newState.moveName + " = " + value);
                }
            }
            return bestValue;
        }
    }

    //alpha beta
    private int alphaBeta(GameState state, int depth, boolean maximizingPlayer, int alpha, int beta) {
        if (depth == 0 || state.isTerminal()) {
            return evaluate(state);
        }

        List<GameState> children = generateSuccessors(state, maximizingPlayer);
        children.sort((a, b) -> Integer.compare(b.damageDealt, a.damageDealt));

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (GameState child : children) {
                int eval = alphaBeta(child, depth - 1, false, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) break;
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

    // tao trang thai con
    private List<GameState> generateSuccessors(GameState state, boolean maxPlayer) {
        List<GameState> list = new ArrayList<>();
        Hero current = maxPlayer ? state.aiHero : state.playerHero;
        Hero enemy = maxPlayer ? state.playerHero : state.aiHero;

        // Basic Attack
        Hero aiCopy = deepCopy(state.aiHero);
        Hero playerCopy = deepCopy(state.playerHero);
        Hero attacker = maxPlayer ? aiCopy : playerCopy;
        Hero target = maxPlayer ? playerCopy : aiCopy;

        target.takeDamage(attacker.attack);
        list.add(new GameState(aiCopy, playerCopy, state.time + 1000, "Basic Attack", attacker.attack));

        // Skills
        for (Skill skill : current.skills) {
            if (skill.canUse(state.time, current.mp)) {
                aiCopy = deepCopy(state.aiHero);
                playerCopy = deepCopy(state.playerHero);
                attacker = maxPlayer ? aiCopy : playerCopy;
                target = maxPlayer ? playerCopy : aiCopy;

                if (attacker.useSkill(skill.getName(), state.time, target)) {
                    list.add(new GameState(aiCopy, playerCopy, state.time + 1000,
                            skill.getName(), skill.getDamage()));
                }
            }
        }
        return list;
    }

    private int evaluate(GameState state) {
        if (state.aiHero.hp <= 0) return -999999;
        if (state.playerHero.hp <= 0) return 999999;

        int score = 0;
        score += state.aiHero.hp * 15;
        score -= state.playerHero.hp * 18;
        score += state.aiHero.mp * 4;
        score -= state.playerHero.mp * 2;

        if (state.playerHero.hp <= 25) score += 400;
        if (state.playerHero.hp <= 12) score += 800;
        if (state.aiHero.hp <= 20) score -= 600;

        // Bonus theo class
        if (state.aiHero instanceof Fighter && state.aiHero.mp >= 22) score += 300;
        if (state.aiHero instanceof Mage && state.aiHero.mp >= 25 && state.playerHero.hp <= 40) score += 500;
        if (state.aiHero instanceof Marksman && state.playerHero.hp <= 35) score += 700;
        if (state.aiHero instanceof Support && state.aiHero.hp <= 30) score += 400;
        return score;
    }

    private Hero deepCopy(Hero original) {
        Hero copy;
        if (original instanceof Fighter) {
            copy = new Fighter(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
        } else if (original instanceof Mage) {
            copy = new Mage(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
        } else if (original instanceof Marksman) {
            copy = new Marksman(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
            ((Marksman) copy).critRate = ((Marksman) original).critRate;
        } else if (original instanceof Support) {
            copy = new Support(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
        } else {
            copy = new Fighter(original.name, original.maxHP, original.maxMP,
                    original.position, original.attack, original.defense);
        }

        copy.hp = original.hp;
        copy.mp = original.mp;
        copy.skills = new ArrayList<>();
        for (Skill s : original.skills) {
            Skill ns = new Skill(s.getName(), s.getMpCost(), s.getCooldown(),
                    s.getDamage(), s.getHealHP(), s.getHealMP());
            ns.setLastUsedTime(s.getLastUsedTime());
            copy.skills.add(ns);
        }
        return copy;
    }

    //
    public void setUseAlphaBeta(boolean use) {
        this.useAlphaBeta = use;
    }
}